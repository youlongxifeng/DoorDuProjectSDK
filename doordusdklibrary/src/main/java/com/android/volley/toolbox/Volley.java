/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.volley.toolbox;

import java.io.File;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.ExecutorDelivery;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyLog;

public class Volley {

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @param stack
	 *            An {@link HttpStack} to use for the network, or null for
	 *            default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context, Network stack) {
		VolleyBuilder b = new VolleyBuilder(context);
		b.mStack = stack;
		b.mPoolSize = 3;
		return newRequestQueue(b);
	}

	/**
	 * Creates a default instance of the worker pool and calls
	 * {@link RequestQueue#start()} on it.
	 * 
	 * @param context
	 *            A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static RequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}

	public static RequestQueue newRequestQueue(VolleyBuilder builder) {
		File cacheDir = new File(builder.mCachePath);
		Network network = builder.mStack;
		if (null == network) {
			HttpStack stack = null;
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack(null,builder.mSslSocketFactory);
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See:
				// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(builder.mAgent));
			}
			network = new BasicNetwork(stack);
		}

		Handler h = builder.mHandler;
		if (null == h)
			h = new Handler(Looper.getMainLooper());
		RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir, builder.mCacheSize), network,
				builder.mPoolSize, new ExecutorDelivery(h));
		queue.start();
		return queue;
	}

	public static Cache getDefualtCache(File f, int maxSize) {
		return new DiskBasedCache(f, maxSize);
	}

	public static RetryPolicy getDefaultRetryPolicy(int initialTimeoutMs, int maxNumRetries, float backoffMultiplier) {
		return new DefaultRetryPolicy(initialTimeoutMs, maxNumRetries, backoffMultiplier);
	}

	public static void cancelRequest(final String url, RequestQueue queue) {
		queue.cancelAll(new RequestFilter() {

			@Override
			public boolean apply(Request<?> request) {
				// TODO Auto-generated method stub
				return url.equals(request.getUrl());
			}
		});
	}

	/**
	 * 设置打印日志的log
	 * 
	 * @param tag
	 */
	public static void setTag(String tag) {
		setTag(tag, Log.VERBOSE);
	}

	public static void setTag(String tag, int flag) {
		VolleyLog.setTag(tag, flag);
	}

}
