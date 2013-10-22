package com.valven.kodio.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class Utils {
	private static final SimpleDateFormat mDateFormat = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
	
	private static final SimpleDateFormat mPrintDateFormat = new SimpleDateFormat(
			"HH:mm", Locale.ENGLISH);
	
	public static Date getDate(String time){
		Date date=null;
		try {
			date = mDateFormat.parse(time);
		} catch (ParseException e) {
		}
		return date;
	}

	public static String getTime(String time) {
		Calendar cal = Calendar.getInstance();
		Date date=null;
		try {
			date = mDateFormat.parse(time);
			cal.setTime(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (date==null)
			return "00:00";
		else
			return mPrintDateFormat.format(date);
	}

	public static String nvl(String s1, String s2) {
		return TextUtils.isEmpty(s1) ? s2 : s1;
	}

	public static boolean checkInternetConnection(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			Log.w("utils", "Error occured while checking internet connection");
			return false;
		}
	}

	public static void setSharedPreference(Context context, String prefName,
			String value) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = prefs.edit();
		if (value == null) {
			editor.remove(prefName);
		} else {
			editor.putString(prefName, value);
		}
		editor.commit();
	}

	public static String getSharedPreference(Context context, String prefName,
			String defaultValue) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String value = prefs.getString(prefName, defaultValue);
		return value;
	}
	
	public static JSONArray sort(JSONArray array, Comparator<Object> c){
	    List<Object>  asList = new ArrayList<Object>(array.length());
	    for (int i=0; i<array.length(); i++){
	      asList.add(array.opt(i));
	    }
	    Collections.sort(asList, c);
	    JSONArray  res = new JSONArray();
	    for (Object o : asList){
	      res.put(o);
	    }
	    return res;
	}
}
