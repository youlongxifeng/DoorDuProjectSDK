package com.android.volley.toolbox;

import java.io.File;

import javax.net.ssl.SSLSocketFactory;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

import com.android.volley.Network;

public class VolleyBuilder {
	String mCachePath;
	Handler mHandler;
	int mPoolSize;
	String mAgent;
	Network mStack;
	int mCacheSize;
	SSLSocketFactory mSslSocketFactory;
	
	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";

	public VolleyBuilder(Context context) {
		mCachePath = new File(context.getCacheDir(), DEFAULT_CACHE_DIR).getAbsolutePath();
		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}
		mAgent = userAgent;
		mPoolSize = 1;
		mCacheSize = 5 * 1024 * 1024;
	}

	public VolleyBuilder setHandler(Handler handler) {
		mHandler = handler;
		return this;
	}

	public VolleyBuilder setThreadPoolSize(int size) {
		mPoolSize = size;
		return this;
	}

	public VolleyBuilder setCachePath(String path) {
		mCachePath = path;
		return this;
	}

	public VolleyBuilder setUserAgent(String agent) {
		mAgent = agent;
		return this;
	}

	public VolleyBuilder setHttpStack(Network stack) {
		mStack = stack;
		return this;
	}

	public VolleyBuilder setCacheSize(int size) {
		mCacheSize = size;
		return this;
	}
	
	public VolleyBuilder setSSLSocketFactory(SSLSocketFactory factory)
	{
		mSslSocketFactory = factory;
		return this;
	}
	

}
