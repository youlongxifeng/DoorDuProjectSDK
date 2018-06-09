package com.dd.sdk.net;

import android.os.Handler;

import java.util.concurrent.Executor;

/**
 * @author Administrator
 * @name DoorDuProjectSDK
 * @class name：com.dd.sdk.net
 * @class describe
 * @time 2018/6/5 10:22
 * @change
 * @class describe
 */

public class ExecutorDelivery {
    /** Used for posting responses, typically to the main thread. */
    private final Executor mResponsePoster;
    private final NetCallback mCallback;
    private boolean mDestory;

    public ExecutorDelivery(final Handler handler, NetCallback callback) {
        // Make an Executor that just wraps the handler.
        mCallback = callback;
        mResponsePoster = new Executor() {
            @Override
            public void execute(Runnable command) {
                handler.post(command);
            }
        };
    }
    public void onDestory() {
        mDestory = true;
    }

    public void postResponse(Response response) {
        if (!mDestory)
            mResponsePoster.execute(new ResponseDeliveryRunnable(null, response));
    }

    public void postError(NetError error) {
        if (!mDestory)
            mResponsePoster.execute(new ResponseDeliveryRunnable(error, null));
    }


    private final class ResponseDeliveryRunnable implements Runnable {
        private NetError mError;
        private Response mResponse;

        public ResponseDeliveryRunnable(NetError error, Response response) {
            mError = error;
            mResponse = response;
        }

        @Override
        public void run() {
            if (null != mError)
                mCallback.onError(mError);
            else if (null != mResponse)
                mCallback.onResponse(mResponse);

        }
    }
}
