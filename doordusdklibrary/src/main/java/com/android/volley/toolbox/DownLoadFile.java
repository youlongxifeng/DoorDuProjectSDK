package com.android.volley.toolbox;

import android.annotation.TargetApi;
import android.os.Build.VERSION_CODES;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.error.VolleyError;
import com.z.core.Util;

import java.io.File;

public class DownLoadFile extends Request<String> implements ErrorListener {
	private Listener<String> mListener;
	private String mPath;

	public DownLoadFile(String url, String path, Listener<String> listener) {
		super(Method.GET, url, null);
		mListener = listener;
		setErrorListener(this);
		mPath = path;
	}

	@TargetApi(VERSION_CODES.KITKAT)
    @Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {

		Util.saveByte(new File(mPath), response.data, false);
		Cache.Entry entry = HttpHeaderParser.parseCacheHeaders(response);
		long live = getCacheLive();
		if (live > System.currentTimeMillis() && live != entry.ttl)
			entry.ttl = live;
		live = getRefershInerval();
		if (live > System.currentTimeMillis() && live != entry.softTtl)
			entry.softTtl = live;

		return Response.success(mPath, entry);
	}

	@Override
	protected void deliverResponse(String response) {
		// TODO Auto-generated method stub
		if (null != mListener)
			mListener.onResponse(response, getToken());
	}

	@Override
	public void onErrorResponse(VolleyError error, int token) {
		if (null != mListener)
			mListener.onResponse("", token);
	}

}
