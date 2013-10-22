package com.valven.kodio.activity;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.valven.kodio.KodIO;
import com.valven.kodio.R;
import com.valven.kodio.model.Response;
import com.valven.kodio.task.Callback;
import com.valven.kodio.task.DataTask;
import com.valven.kodio.util.HttpUtils.HttpRequest;
import com.valven.kodio.util.Utils;

public class SplashActivity extends Activity implements Callback {

	private static String DATA_URL = "http://s3.amazonaws.com/kodio/data.json";

	private AsyncTask<Object, Void, Response> loader;

	public static final String ARG_DATA = "json_data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if (KodIO.DATA == null) {
			if (!Utils.checkInternetConnection(this)) {
				String data = loadJSONFromCache();
				if (data != null) {
					Log.d("splash", "using data from cache");
					handleResponse(data);
				} else {
					new AlertDialog.Builder(this)
							.setMessage(R.string.no_connection)
							.setCancelable(false)
							.setPositiveButton(android.R.string.ok,
									new OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											finish();
										}
									}).show();
				}
			} else {
				loader = new DataTask(this);
				loader.execute(DATA_URL, HttpRequest.GET);
			}
		} else {
			switchActivity();
		}
	}

	@Override
	public void handleResponse(String response) {
		if (response == null) {
			response = Utils.getSharedPreference(getApplicationContext(),
					SplashActivity.ARG_DATA, null);
			Log.d("splash", "using data from cache");
		}
		try {
			KodIO.DATA = new JSONObject(response);
			Utils.setSharedPreference(getApplicationContext(),
					SplashActivity.ARG_DATA, response);

			switchActivity();
		} catch (Exception e) {
			e.printStackTrace();
			
			response = Utils.getSharedPreference(getApplicationContext(),
					SplashActivity.ARG_DATA, null);
			if (response != null){
				Log.w("splash", "json is invalid. using data from cache");
				handleResponse(response);
			} else {
				Log.w("splash", "json is invalid.");
			}
		}
	}

	private String loadJSONFromCache() {
		String data = Utils.getSharedPreference(this, SplashActivity.ARG_DATA,
				null);
		return data;
	}

	private void switchActivity() {
		Intent intent = new Intent(this, SpeakersActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		finish();
		startActivity(intent);
		overridePendingTransition(R.anim.fade, R.anim.hold);
	}
	
	@Override
	public void onStart() {
		super.onStart();
		EasyTracker.getInstance(this).activityStart(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance(this).activityStart(this);
	}

}
