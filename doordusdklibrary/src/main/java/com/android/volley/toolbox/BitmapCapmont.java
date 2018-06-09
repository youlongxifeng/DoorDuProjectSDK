package com.android.volley.toolbox;

import android.graphics.Bitmap;

public class BitmapCapmont {

	public String url;
	public Bitmap bitmap;
	public String token;

	
	public BitmapCapmont(String url)
	{
		this.url = url;
	}
	
	public BitmapCapmont(String url,Bitmap bitmap,String token)
	{
		this.url = url;
		this.bitmap = bitmap;
		this.token = token;
	}
}
