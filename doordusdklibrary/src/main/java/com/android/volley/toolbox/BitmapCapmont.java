package com.android.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView.ScaleType;

public class BitmapCapmont {

	public String url;
	public Bitmap bitmap;
	public int token;

	
	public BitmapCapmont(String url)
	{
		this.url = url;
	}
	
	public BitmapCapmont(String url,Bitmap bitmap,int token)
	{
		this.url = url;
		this.bitmap = bitmap;
		this.token = token;
	}
}
