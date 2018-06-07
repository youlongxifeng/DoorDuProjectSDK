package com.android.volley.toolbox;

public interface Secret {

	
	public byte[] decryption(byte[] data, int start, int count);
	
	public byte[] encryption(byte[] data, int start, int count);
}
