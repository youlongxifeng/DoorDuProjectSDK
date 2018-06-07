package com.z.core;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyLog;
import com.z.core.Sentry.SentryEventBuilder.SentryEventLevel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Sentry {

    private final static String VERSION = "0.2.0";

    private Context context;

    private String baseUrl;
    private String dsn;
    private String packageName;
    private int verifySsl;
    private SentryEventCaptureListener captureListener;

    private static final String TAG = "Sentry";
    private static final String DEFAULT_BASE_URL = "https://app.getsentry.com";
    private JSONObject mDeviceInfo;
    private String mGuid;
    private String mVersion;
    private String mOs;
    private boolean mInit;
    private Sentry() {
    }

    private static Sentry getInstance() {
        return LazyHolder.instance;
    }

    public void setDeviceInfo(JSONObject info) {
        mDeviceInfo = info;
    }

    private final static class LazyHolder {
        private static Sentry instance = new Sentry();
    }

    public static void init(Context context, String dsn) {
        Sentry.init(context, DEFAULT_BASE_URL, dsn);
    }

    public static void init(Context context, String baseUrl, String dsn) {
        init(context, baseUrl, dsn, null);
    }

    public static void init(Context context, String baseUrl, String dsn, JSONObject deviceInfo) {
        final Sentry s = Sentry.getInstance();
        s.mInit = true;
        s.context = context;
        s.baseUrl = baseUrl;
        s.dsn = dsn;
        s.packageName = context.getPackageName();
        s.mDeviceInfo = deviceInfo;
        s.verifySsl = getVerifySsl(dsn);
//		Sentry.getInstance().setupUncaughtExceptionHandler();
        if (null != deviceInfo) {
            final String guid = "Guid";
            final String ver = "Version";
            final String core = "Core";
            try {
                if (deviceInfo.has(guid))
                    s.mGuid = deviceInfo.getString(guid);
                if (deviceInfo.has(ver))
                    s.mVersion = deviceInfo.getString(ver);
                if (deviceInfo.has(core))
                    s.mOs = deviceInfo.getString(core);
            } catch (Exception e) {

            }

        }
    }

    public void setGuid(String guid) {
        mGuid = guid;
    }

    public static void setDefaulInfo(JSONObject info) {
        Sentry.getInstance().mDeviceInfo = info;
    }

    private static int getVerifySsl(String dsn) {
        int verifySsl = 1;
        List<NameValuePair> params = getAllGetParams(dsn);
        for (NameValuePair param : params) {
            if (param.getName().equals("verify_ssl"))
                return Integer.parseInt(param.getValue());
        }
        return verifySsl;
    }

    private static List<NameValuePair> getAllGetParams(String dsn) {
        List<NameValuePair> params = null;
        try {
            params = URLEncodedUtils.parse(new URI(dsn), "UTF-8");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return params;
    }

    private void setupUncaughtExceptionHandler() {

        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler != null) {
            VolleyLog.d("Sentry Debugged current handler class = %s ", currentHandler.getClass());
            // Log.d("Debugged", "current handler class=" +
            // currentHandler.getClass().getName());
        }

        // don't register again if already registered
        if (!(currentHandler instanceof SentryUncaughtExceptionHandler)) {
            // Register default exceptions handler
            Thread.setDefaultUncaughtExceptionHandler(new SentryUncaughtExceptionHandler(currentHandler, context));
        }

        sendAllCachedCapturedEvents();
    }

    private static String createXSentryAuthHeader() {
        String header = "";

        Uri uri = Uri.parse(Sentry.getInstance().dsn);
        VolleyLog.d("Sentry URI - %s ", uri);
        // Log.d("Sentry", "URI - " + uri);
        String authority = uri.getAuthority().replace("@" + uri.getHost(), "");

        String[] authorityParts = authority.split(":");
        String publicKey = authorityParts[0];
        String secretKey = authorityParts[1];

        header += "Sentry sentry_version=4,";
        header += "sentry_client=sentry-android/" + VERSION + ",";
        header += "sentry_timestamp=" + System.currentTimeMillis() + ",";
        header += "sentry_key=" + publicKey + ",";
        header += "sentry_secret=" + secretKey;

        return header;
    }

    private static String getProjectId() {
        Uri uri = Uri.parse(Sentry.getInstance().dsn);
        String path = uri.getPath();
        String projectId = path.substring(path.lastIndexOf("/") + 1);

        return projectId;
    }

    public static void sendAllCachedCapturedEvents() {
        try {
            ArrayList<SentryEventRequest> unsentRequests = InternalStorage.getInstance().getUnsentRequests();
            VolleyLog.d("Sentry Sending up - %d cached response(s)", unsentRequests.size());
            // Log.d(Sentry.TAG, "Sending up " + unsentRequests.size() +
            // " cached response(s)");
            for (SentryEventRequest request : unsentRequests) {
                Sentry.doCaptureEventPost(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param captureListener
     *            the captureListener to set
     */
    public static void setCaptureListener(SentryEventCaptureListener captureListener) {
        Sentry.getInstance().captureListener = captureListener;
    }

    public static void captureMessage(String message) {
        Sentry.captureMessage(message, SentryEventLevel.INFO);
    }

    public static void captureMessage(String message, SentryEventLevel level) {
        Sentry.captureEvent(new SentryEventBuilder().setMessage(message).setLevel(level));
    }

    public static void captureException(Throwable t) {
        Sentry.captureException(t, SentryEventLevel.ERROR);
    }

    public static void captureException(Throwable t, SentryEventLevel level) {
        String culprit = getCause(t, t.getMessage());

        Sentry.captureEvent(new SentryEventBuilder().setMessage(t.getMessage()).setCulprit(culprit).setLevel(level).setException(t));

    }

    public static void captureUncaughtException(Context context, Throwable t) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        t.printStackTrace(printWriter);
        try {
            // Random number to avoid duplicate files
            long random = System.currentTimeMillis();

            // Embed version in stacktrace filename
            File stacktrace = new File(getStacktraceLocation(context), "raven-" + String.valueOf(random) + ".stacktrace");
            VolleyLog.d("Sentry Writing unhandled exception to: %s", stacktrace.getAbsolutePath());
            // Log.d(TAG, "Writing unhandled exception to: " +
            // stacktrace.getAbsolutePath());

            // Write the stacktrace to disk
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stacktrace));
            oos.writeObject(t);
            oos.flush();
            // Close up everything
            oos.close();
        } catch (Exception ebos) {
            // Nothing much we can do about this - the game is over
            ebos.printStackTrace();
        }
        VolleyLog.d("Sentry %s", result.toString());
        // Log.d(TAG, result.toString());
    }

    private static String getCause(Throwable t, String culprit) {
        for (StackTraceElement stackTrace : t.getStackTrace()) {
            if (stackTrace.toString().contains(Sentry.getInstance().packageName)) {
                culprit = stackTrace.toString();
                break;
            }
        }

        return culprit;
    }

    private static File getStacktraceLocation(Context context) {
        return new File(context.getCacheDir(), "crashes");
    }

    private static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString();
    }

    public static void captureEvent(String message, SentryEventLevel level, String[] extContent) {
        try {
            Sentry.SentryEventBuilder buidler = new Sentry.SentryEventBuilder();
            try {
                JSONObject extend = buidler.getExtra();
                int len = extContent.length;
                for (int i = 0; i < len; i += 2) {
                    extend.put(extContent[i], extContent[i + 1]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            buidler.setMessage(message);
            buidler.setLevel(level);
            captureEvent(buidler);
        } catch (Exception e) {
        }

    }

    public static void captureEvent(String message, SentryEventLevel level, String extendKey, String extendContent) {
        captureEvent(message, level, new String[] { extendKey, extendContent });
    }

    public static void captureEvent(SentryEventBuilder builder) {
        if(!Sentry.getInstance().mInit) //ignore
            return;
        final SentryEventRequest request;
        if (Sentry.getInstance().captureListener != null) {

            builder = Sentry.getInstance().captureListener.beforeCapture(builder);
            if (builder == null) {
                VolleyLog.d("Sentry %s", "SentryEventBuilder in captureEvent is null");
                // Log.e(Sentry.TAG,
                // "SentryEventBuilder in captureEvent is null");
                return;
            }

            request = new SentryEventRequest(builder);
        } else {
            request = new SentryEventRequest(builder);
        }
        VolleyLog.d("Sentry Request - %s", request.getRequestData());
        // Log.d(TAG, "Request - " + request.getRequestData());

        // Check if on main thread - if not, run on main thread
        if (Looper.myLooper() == Looper.getMainLooper()) {
            doCaptureEventPost(request);
        } else if (Sentry.getInstance().context != null) {

            // HandlerThread thread = new HandlerThread("SentryThread") {
            // };
            // thread.start();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    doCaptureEventPost(request);
                }
            };
            Handler h = new Handler(Looper.getMainLooper());
            h.post(runnable);

        }

    }

    private static boolean shouldAttemptPost() {
        PackageManager pm = Sentry.getInstance().context.getPackageManager();
        int hasPerm = pm.checkPermission(android.Manifest.permission.ACCESS_NETWORK_STATE, Sentry.getInstance().context.getPackageName());
        if (hasPerm == PackageManager.PERMISSION_DENIED) {
            return true;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) Sentry.getInstance().context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static class ExSSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public ExSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);
            TrustManager x509TrustManager = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { x509TrustManager }, null);
        }

        public ExSSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            super(null);
            sslContext = context;
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    public static HttpClient getHttpsClient(HttpClient client) {
        try {
            X509TrustManager x509TrustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { x509TrustManager }, null);
            SSLSocketFactory sslSocketFactory = new ExSSLSocketFactory(sslContext);
            sslSocketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager clientConnectionManager = client.getConnectionManager();
            SchemeRegistry schemeRegistry = clientConnectionManager.getSchemeRegistry();
            schemeRegistry.register(new Scheme("https", sslSocketFactory, 443));
            return new DefaultHttpClient(clientConnectionManager, client.getParams());
        } catch (Exception ex) {
            return null;
        }
    }

    private static void doCaptureEventPost(final SentryEventRequest request) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                if (!shouldAttemptPost()) {
                    InternalStorage.getInstance().addRequest(request);
                    return null;
                }
                // 因为现在不会持久化,失败的时候重试下
                boolean r = false;
                for (int i = 0; i < 3; i++) {
                    HttpClient httpClient;
                    if (Sentry.getInstance().verifySsl != 0) {
                        httpClient = new DefaultHttpClient();
                    } else {
                        httpClient = getHttpsClient(new DefaultHttpClient());
                    }
                    HttpPost httpPost = new HttpPost(Sentry.getInstance().baseUrl + "/api/" + getProjectId() + "/store/");

                    int TIMEOUT_MILLISEC = i * 10000 + 5000; // = 20 seconds
                    HttpParams httpParams = httpPost.getParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);

                    boolean success = false;
                    try {
                        httpPost.setHeader("X-Sentry-Auth", createXSentryAuthHeader());
                        httpPost.setHeader("User-Agent", "sentry-android/" + VERSION);
                        httpPost.setHeader("Content-Type", "text/html; charset=utf-8");
                        httpPost.setEntity(new StringEntity(request.getRequestData(), "utf-8"));
                        // httpPost.setEntity(new
                        // StringEntity(request.getRequestData()));
                        //这里竟然报内存溢出
                        HttpResponse httpResponse = httpClient.execute(httpPost);

                        int status = httpResponse.getStatusLine().getStatusCode();
                        byte[] byteResp = null;

                        // Gets the input stream and unpackages the response
                        // into a
                        // command
                        if (httpResponse.getEntity() != null) {
                            try {
                                InputStream in = httpResponse.getEntity().getContent();
                                byteResp = this.readBytes(in);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        String stringResponse = null;
                        Charset charsetInput = Charset.forName("UTF-8");
                        CharsetDecoder decoder = charsetInput.newDecoder();
                        CharBuffer cbuf = null;
                        try {
                            cbuf = decoder.decode(ByteBuffer.wrap(byteResp));
                            stringResponse = cbuf.toString();
                        } catch (CharacterCodingException e) {
                            e.printStackTrace();
                        }

                        success = (status == 200);
                        VolleyLog.d("Sentry SendEvent - %d  %s", status, stringResponse);
                        r = true;
                        break;
                        // Log.d(TAG, "SendEvent - " + status + " " +
                        // stringResponse);
                    } catch (ClientProtocolException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        VolleyLog.d("Sentry IOException  %s", Sentry.getInstance().baseUrl + "/api/" + getProjectId() + "/store/");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                final String str = request.lv;
                if (!r && ("error".equals(str) || "fatal".equals(str))) {
                    InternalStorage.getInstance().addRequest(request);
                } else {
                    InternalStorage.getInstance().removeBuilder(request);
                }

                return null;

            }

            private byte[] readBytes(InputStream inputStream) throws IOException {
                // this dynamically extends to take the bytes you read
                ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

                // this is storage overwritten on each iteration with bytes
                int bufferSize = 1024;
                byte[] buffer = new byte[bufferSize];

                // we need to know how may bytes were read to write them to the
                // byteBuffer
                int len = 0;
                while ((len = inputStream.read(buffer)) != -1) {
                    byteBuffer.write(buffer, 0, len);
                }

                // and then we can return your byte array.
                return byteBuffer.toByteArray();
            }

        }.execute();

    }

    private class SentryUncaughtExceptionHandler implements UncaughtExceptionHandler {

        private UncaughtExceptionHandler defaultExceptionHandler;
        private Context context;

        // constructor
        public SentryUncaughtExceptionHandler(UncaughtExceptionHandler pDefaultExceptionHandler, Context context) {
            defaultExceptionHandler = pDefaultExceptionHandler;
            this.context = context;
        }

        @Override
        public void uncaughtException(Thread thread, Throwable e) {
            // Here you should have a more robust, permanent record of problems
            SentryEventBuilder builder = new SentryEventBuilder(e, SentryEventLevel.FATAL);
            if (Sentry.getInstance().captureListener != null) {
                builder = Sentry.getInstance().captureListener.beforeCapture(builder);
            }

            if (builder != null) {
                InternalStorage.getInstance().addRequest(new SentryEventRequest(builder));
            } else {
                VolleyLog.d("Sentry  %s", "SentryEventBuilder in uncaughtException is null");
                // Log.e(Sentry.TAG,
                // "SentryEventBuilder in uncaughtException is null");
            }

            // call original handler
            if (VolleyLog.DEBUG)
                defaultExceptionHandler.uncaughtException(thread, e);
            else {
                Log.e("zxj", "error", e);
                final String pn = "com.doordu";
                try {
                    final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                    Method method = ActivityManager.class.getMethod("forceStopPackage", String.class);
                    method.invoke(am, pn);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            // try {
            // Thread.sleep(3000);
            // } catch (InterruptedException e2) {
            // e2.printStackTrace();
            // }
            // 退出程序
            // android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(1);
        }

    }

    private static class InternalStorage {

        private final static String FILE_NAME = "unsent_requests";
        private ArrayList<SentryEventRequest> unsentRequests;

        private static InternalStorage getInstance() {
            return LazyHolder.instance;
        }

        private static class LazyHolder {
            private static InternalStorage instance = new InternalStorage();
        }

        private InternalStorage() {
            this.unsentRequests = this.readObject(Sentry.getInstance().context);
        }

        /**
         * @return the unsentRequests
         */
        public ArrayList<SentryEventRequest> getUnsentRequests() {
            return unsentRequests;
        }

        public void addRequest(SentryEventRequest request) {
            synchronized (this) {
                VolleyLog.d("Sentry Adding request - %s ", request.uuid);
                // Log.d(Sentry.TAG, "Adding request - " + request.uuid);
                if (!this.unsentRequests.contains(request) && ("error".equals(request.lv) || "fatal".equals(request.lv))) {
                    this.unsentRequests.add(request);
                    this.writeObject(Sentry.getInstance().context, this.unsentRequests);

                }
            }
        }

        public void removeBuilder(SentryEventRequest request) {
            synchronized (this) {
                VolleyLog.d("Sentry Removing request - %s ", request.uuid);
                // Log.d(Sentry.TAG, " Sentry Removing request - " +
                // request.uuid);
                boolean bl = this.unsentRequests.remove(request);
                if (bl)
                    this.writeObject(Sentry.getInstance().context, this.unsentRequests);
            }
        }

        private void writeObject(Context context, ArrayList<SentryEventRequest> requests) {
            FileOutputStream fos = null;
            try {
                fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(requests);
                oos.close();
                fos.close();
                fos = null;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != fos) {
                    try {
                        fos.close();
                        fos = null;
                    } catch (Exception e2) {
                        // TODO: handle exception
                    }
                }
            }
        }

        private ArrayList<SentryEventRequest> readObject(Context context) {
            FileInputStream fis = null;
            ArrayList<SentryEventRequest> r = null;
            try {
                fis = context.openFileInput(FILE_NAME);
                ObjectInputStream ois = new ObjectInputStream(fis);
                r = (ArrayList<SentryEventRequest>) ois.readObject();
                ois.close();
                if (null != r && !r.isEmpty()) {
                    Iterator<SentryEventRequest> iter = r.iterator();
                    String str = null;
                    while (iter.hasNext()) {
                        str = iter.next().lv;
                        if (!"error".equals(str) && !"fatal".equals(str)) {
                            iter.remove();
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != fis) {
                    try {
                        fis.close();
                        fis = null;
                    } catch (Exception e2) {
                    }
                }
            }
            return r == null ? new ArrayList<SentryEventRequest>() : r;
        }
    }

    public abstract static class SentryEventCaptureListener {

        public abstract SentryEventBuilder beforeCapture(SentryEventBuilder builder);

    }

    public static class SentryEventRequest implements Serializable {
        private String requestData;
        private UUID uuid;
        private String lv;

        public SentryEventRequest(SentryEventBuilder builder) {
            lv = builder.getLevel();
            this.requestData = new JSONObject(builder.event).toString();
            if(!TextUtils.isEmpty(requestData) && requestData.length() > 125001)
            {
                requestData = requestData.substring(0, 125000);
            }
            // Log.i("zxj", "Sentry reqeust data " + requestData);
            this.uuid = UUID.randomUUID();
        }

        /**
         * @return the requestData
         */
        public String getRequestData() {
            return requestData;
        }

        /**
         * @return the uuid
         */
        public UUID getUuid() {
            return uuid;
        }

        @Override
        public boolean equals(Object other) {
            SentryEventRequest otherRequest = (SentryEventRequest) other;

            if (this.uuid != null && otherRequest.uuid != null) {
                return uuid.equals(otherRequest.uuid);
            }

            return false;
        }

    }

    public static class SentryEventBuilder  {



        private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        static {
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        }

        private Map<String, Object> event;

        public static enum SentryEventLevel {

            FATAL("fatal"), ERROR("error"), WARNING("warning"), INFO("info"), DEBUG("debug");

            private String value;

            SentryEventLevel(String value) {
                this.value = value;
            }

        }

        public SentryEventBuilder() {
            event = new HashMap<String, Object>();
            event.put("event_id", UUID.randomUUID().toString().replace("-", ""));
            // event.put("platform", "java");
            this.setTimestamp(System.currentTimeMillis());
            final Sentry s = Sentry.getInstance();
            // if (null != s.mDeviceInfo)
            // setExtra(s.mDeviceInfo);
            if (!TextUtils.isEmpty(s.mGuid)) {

                JSONObject user = new JSONObject();
                try {
                    user.put("guid", s.mGuid);
                    user.put("version", s.mVersion);
                    user.put("core", s.mOs);
                    // ["device","Samsung SM-A5000"],["os","Android 4.4.4"]
                } catch (Exception e) {
                    // TODO: handle exception
                }

                // "sentry.interfaces.User": {
                // "ip_address": "125.88.122.103"
                // },
                // Log.i("zxj", "1232  "+ user.toString());
                // event.put("sentry.interfaces.User", user);
                setTags(user);
                setCulprit("http://"+s.mGuid);

                //
                // JSONObject user = new JSONObject();
                // try {
                // user.put("ip_address", s.mGuid);
                // } catch (Exception e) {
                //
                // }
                // event.put("sentry.interfaces.User", user);
            }

        }

        public SentryEventBuilder(Throwable t, SentryEventLevel level) {
            this();

            String culprit = getCause(t, t.getMessage());

            this.setMessage(t.getMessage()).setCulprit(culprit).setLevel(level).setException(t);
        }

        /**
         * "message": "SyntaxError: Wattttt!"
         *
         * @param message
         * @return
         */
        public SentryEventBuilder setMessage(String message) {
            event.put("message", message);
            return this;
        }

        /**
         * "timestamp": "2011-05-02T17:41:36"
         *
         * @param timestamp
         * @return
         */
        public SentryEventBuilder setTimestamp(long timestamp) {
            event.put("timestamp", sdf.format(new Date(timestamp)));
            return this;
        }

        /**
         * "level": "warning"
         *
         * @param level
         * @return
         */
        public SentryEventBuilder setLevel(SentryEventLevel level) {
            event.put("level", level.value);
            return this;
        }

        public String getLevel() {
            String r = null;
            if (event.containsKey("level"))
                r = (String) event.get("level");
            return r;
        }

        /**
         * "logger": "my.logger.name"
         *
         * @param logger
         * @return
         */
        public SentryEventBuilder setLogger(String logger) {
            event.put("logger", logger);
            return this;
        }

        /**
         * "culprit": "my.module.function_name"
         *
         * @param culprit
         * @return
         */
        public SentryEventBuilder setCulprit(String culprit) {
            event.put("culprit", culprit);
            return this;
        }

        /**
         *
         * @return
         */
        public SentryEventBuilder setUser(Map<String, String> user) {
            setUser(new JSONObject(user));
            return this;
        }

        public SentryEventBuilder setUser(JSONObject user) {
            event.put("user", user);
            return this;
        }

        public JSONObject getUser() {
            if (!event.containsKey("user")) {
                setTags(new HashMap<String, String>());
            }

            return (JSONObject) event.get("user");
        }

        /**
         *
         * @param tags
         * @return
         */
        public SentryEventBuilder setTags(Map<String, String> tags) {
            setTags(new JSONObject(tags));
            return this;
        }

        public SentryEventBuilder setTags(JSONObject tags) {
            event.put("tags", tags);
            return this;
        }

        public JSONObject getTags() {
            if (!event.containsKey("tags")) {
                setTags(new HashMap<String, String>());
            }

            return (JSONObject) event.get("tags");
        }

        /**
         *
         * @param serverName
         * @return
         */
        public SentryEventBuilder setServerName(String serverName) {
            event.put("server_name", serverName);
            return this;
        }

        /**
         *
         * @param name
         * @param version
         * @return
         */
        public SentryEventBuilder addModule(String name, String version) {
            JSONArray modules;
            if (!event.containsKey("modules")) {
                modules = new JSONArray();
                event.put("modules", modules);
            } else {
                modules = (JSONArray) event.get("modules");
            }

            if (name != null && version != null) {
                String[] module = { name, version };
                modules.put(new JSONArray(Arrays.asList(module)));
            }

            return this;
        }

        /**
         *
         * @param extra
         * @return
         */
        public SentryEventBuilder setExtra(Map<String, String> extra) {
            setExtra(new JSONObject(extra));
            return this;
        }

        public SentryEventBuilder setExtra(JSONObject extra) {
            event.put("extra", extra);
            return this;
        }

        public JSONObject getExtra() {
            if (!event.containsKey("extra")) {
                setExtra(new HashMap<String, String>());
            }

            return (JSONObject) event.get("extra");
        }

        /**
         *
         * @param t
         * @return
         */
        public SentryEventBuilder setException(Throwable t) {
            JSONArray values = new JSONArray();

            while (t != null) {
                JSONObject exception = new JSONObject();

                try {
                    exception.put("type", t.getClass().getSimpleName());
                    exception.put("value", t.getMessage());
                    exception.put("module", t.getClass().getPackage().getName());
                    exception.put("stacktrace", getStackTrace(t));

                    values.put(exception);
                } catch (JSONException e) {
                    VolleyLog.e(e, "Failed to build sentry report for  %s", t.getMessage());
                    // Log.e(TAG, "Failed to build sentry report for " + t, e);
                }

                t = t.getCause();
            }

            JSONObject exceptionReport = new JSONObject();

            try {
                exceptionReport.put("values", values);
                event.put("exception", exceptionReport);
            } catch (JSONException e) {
                VolleyLog.e(e, "Sentry Unable to attach exception to event  %s", values.toString());
                // Log.e(TAG, "Unable to attach exception to event " + values,
                // e);
            }

            return this;
        }

        public static JSONObject getStackTrace(Throwable t) throws JSONException {
            JSONArray frameList = new JSONArray();

            for (StackTraceElement ste : t.getStackTrace()) {
                JSONObject frame = new JSONObject();

                String method = ste.getMethodName();
                if (method.length() != 0) {
                    frame.put("function", method);
                }

                int lineno = ste.getLineNumber();
                if (!ste.isNativeMethod() && lineno >= 0) {
                    frame.put("lineno", lineno);
                }

                boolean inApp = true;

                String className = ste.getClassName();
                frame.put("module", className);

                // Take out some of the system packages to improve the exception
                // folding on the sentry server
                if (className.startsWith("android.") || className.startsWith("java.") || className.startsWith("dalvik.")
                        || className.startsWith("com.android.")) {

                    inApp = false;
                }

                frame.put("in_app", inApp);

                frameList.put(frame);
            }

            JSONObject frameHash = new JSONObject();
            frameHash.put("frames", frameList);

            return frameHash;
        }
    }

    public static class MessageException extends Exception {
        public MessageException(String message) {
            super(message);
        }
    }

}
