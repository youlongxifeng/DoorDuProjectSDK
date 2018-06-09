package com.doordu.doorsdk.net;

import android.util.Log;

import com.doordu.doorsdk.manager.TCPManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelectableChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.doordu.doorsdk.net
 * @class describe
 * @time 2018/6/4 16:08
 * @change
 * @class describe
 */

public class INet extends Thread {
    private final static String TAG=INet.class.getCanonicalName();
    protected final static byte DIVIL_VALUE = '*';

    private volatile boolean mRunning = false;
    private final BlockingQueue<Request> mQueue;
    private final ByteBuffer mReadBuffer;
    private final ByteBuffer mWriteBuffer;
    private final ExecutorDelivery mDelivery;
    private final ResponseReader mReader;
    private SocketChannel mChannel;
    private Selector mSelector;
    private String mAddress;
    private int mPort;
    private boolean mConnection;
    private int mCount;
    public INet(String address, int port, BlockingQueue<Request> queue, ExecutorDelivery delivery, ResponseReader reader) {
        super("TCP_LOOP");
        mQueue = queue;
        mAddress = address;
        mPort = port;
        mDelivery = delivery;
        mReadBuffer = ByteBuffer.allocate(1024 * 7);// 7kb
        mWriteBuffer = ByteBuffer.allocate(1024 * 7);// 7kb
        mReader = reader;

    }

    public void quit() {
        mRunning = false;
        if (null != mSelector) {
            try {
                mSelector.close();
                mSelector = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != mChannel) {
            try {
                mChannel.close();
                mChannel = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        interrupt();
    }

    @Override
    public void run() {
        super.run();
        mRunning = true;
        if (init()) {
            try {
                while (mRunning) {
                    if (mSelector.select(180) > 0) {
                        Iterator<SelectionKey> keyIterator = mSelector.selectedKeys().iterator();
                        while (keyIterator.hasNext()) {
                            SelectionKey key = keyIterator.next();
                            if (key.isValid()) {
                                //Log.i(TAG,"INet===Connectable="+key.isConnectable()+"   Readable="+key.isReadable()+"  Writable="+key.isWritable());
                                if (key.isConnectable()) {
                                    SocketChannel channel = (SocketChannel) key.channel();
                                    channel.socket().setKeepAlive(true);
                                    channel.finishConnect();
                                    mConnection = true;
                                    mDelivery.postResponse(Response.obtain(TCPManager.CONNECTION, null));
                                    // nextAction(channel);
                                    // map.put(socketChannel, new
                                    // Handle());//把socket和handle进行绑定
                                } else if (key.isReadable()) {
                                    read(key);
                                } else if (key.isWritable()) {
                                    write(key);
                                }
                            }
                            keyIterator.remove();
                            key.cancel();
                        }
                    } else if (mConnection && mSelector.isOpen())
                        nextAction(mChannel);

                }

            } catch (Exception e) {
                e.printStackTrace();
                mDelivery.postError(NetError.connectionError(e));
            }
        }
        mDelivery.postError(new NetError(NetError.ERROR_END, "Net loop end"));
    }
    private boolean init() {
        boolean bl = false;
        try {
            mSelector = Selector.open();
            mChannel = SocketChannel.open();
            mChannel.configureBlocking(false);
            Log.i(TAG,"init  mAddress=="+mAddress+"  mPort="+mPort);
            mChannel.connect(new InetSocketAddress(mAddress, mPort));
            mChannel.register(mSelector, SelectionKey.OP_CONNECT);
            bl = true;
        } catch (Exception e) {
            e.printStackTrace();
            mDelivery.postError(NetError.connectionError(e.getCause()));
        }
        ByteBuffer sizeBuffer = ByteBuffer.allocate(4);
        sizeBuffer.flip();
        return bl;
    }


    private void read(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {
            while (true) {
                mReadBuffer.clear();
                int index = channel.read(mReadBuffer);
                if (index <= 0) {
                    mCount++;
                    if (mCount == 5)//之前这里没描述，日志定位不了
                        mDelivery.postError(new NetError(NetError.ERROR_END, "connect was broken"));
                    break;
                }
                mCount = 0;
                Response response = null;
                while ((response = mReader.response(mReadBuffer)) != null)
                    mDelivery.postResponse(response);
                if (index < mReadBuffer.capacity())
                    break;
            }
        } catch (ClosedChannelException e) {
            mDelivery.postError(NetError.readError(e.getCause()));
        } catch (IOException e) {
            mDelivery.postError(NetError.readError(e.getCause()));
        }

    }

    private void write(SelectionKey key) {
        Request r = null;
        try {
            r = mQueue.take();
            if (null != r) {
                SocketChannel channel = (SocketChannel) key.channel();
                while (true) {
                    // 防止内容过长一次写不完
                    mWriteBuffer.clear();
                    boolean bl = r.write(mWriteBuffer);
                    mWriteBuffer.flip();
                    channel.write(mWriteBuffer);
                    if (bl)
                        break;
                }
            }
            // }

        } catch (Exception e) {
            mDelivery.postError(NetError.writeError(r, e.getCause()));
        }

    }

    private void nextAction(AbstractSelectableChannel channel) throws ClosedChannelException {
        if (mQueue.isEmpty())
            mChannel.register(mSelector, SelectionKey.OP_READ);
        else
            mChannel.register(mSelector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
    }


}
