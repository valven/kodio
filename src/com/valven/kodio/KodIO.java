package com.valven.kodio;


import org.json.JSONObject;

import com.valven.kodio.util.ImageLoader;

import android.app.Application;

public class KodIO extends Application{
	
	public static JSONObject DATA = null;

	@Override
	public void onCreate(){
		super.onCreate();
		
		ImageLoader.init(getApplicationContext());
	}
}
