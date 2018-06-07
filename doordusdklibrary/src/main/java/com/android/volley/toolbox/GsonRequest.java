package com.android.volley.toolbox;

import java.io.UnsupportedEncodingException;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyLog;
import com.android.volley.error.AuthFailureError;
import com.google.mgson.Gson;

public class GsonRequest<T> extends Request<T> {

	private Class<? extends T> clas;
	private Listener<T> mListener;

	public GsonRequest(String url, Class<? extends T> clas,
			Listener<T> listener, ErrorListener errlistener) {
		super(Method.GET, url, errlistener);
		this.clas = clas;
		mListener = listener;
	}

	private Object mPost;

	public GsonRequest(Object post, String url, Class<? extends T> clas,
			Listener<T> listener, ErrorListener errlistener) {
		super(Method.POST, url, errlistener);
		this.clas = clas;
		mListener = listener;
		mPost = post;
	}

	public static <T> GsonRequest<T> newRequest(String url,
			Class<? extends T> clas, Listener<T> listener,
			ErrorListener errlistener) {
		return new GsonRequest<T>(url, clas, listener, errlistener);
	}

	public static <T> GsonRequest<T> newRequest(Object post, String url,
			Class<? extends T> clas, Listener<T> listener,
			ErrorListener errlistener) {
		return new GsonRequest<T>(post, url, clas, listener, errlistener);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		String parsed;
		try {
			parsed = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
		} catch (UnsupportedEncodingException e) {
			parsed = new String(response.data);
		}
		VolleyLog.printJson(parsed);
		Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
		long live = getCacheLive();
		if (live > System.currentTimeMillis() && live != entry.ttl)
			entry.ttl = live;
		live = getRefershInerval();
		if (live > System.currentTimeMillis() && live != entry.softTtl)
			entry.softTtl = live;
        T r = null;
        try {
            Gson g = new Gson();
            r = g.fromJson(parsed, clas);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

		return Response.success(r, entry);
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		if (null != mPost) {
			if (String.class.isInstance(mPost)) {
				String str = (String) mPost;
				return str.getBytes();
			} else {
				return new Gson().toJson(mPost).getBytes();
			}
		}
		return super.getBody();
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		if (null != mListener)
			mListener.onResponse(response, mToken);
	}

}
