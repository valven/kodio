package com.valven.kodio.helper;

import com.valven.kodio.KodIO;
import com.valven.kodio.activity.SplashActivity;

import android.app.Activity;
import android.content.Intent;

public class ActivityHelper {
	
	private Activity mActivity;
	
	public ActivityHelper(Activity activity) {
		super();
		this.mActivity = activity;
	}

	public boolean onCreate(){
		if (KodIO.DATA != null){
			return true;
		}
		Intent intent = new Intent(mActivity, SplashActivity.class);
		mActivity.finish();
		mActivity.startActivity(intent);
		
		return false;
	}
}
