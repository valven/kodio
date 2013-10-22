package com.valven.kodio.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.valven.kodio.KodIO;
import com.valven.kodio.R;
import com.valven.kodio.component.OrganizerView;
import com.valven.kodio.helper.ActivityHelper;
import com.valven.kodio.util.ImageLoader;

public class InfoActivity extends Activity {

	private JSONObject mData;

	private static final String MAP_URL = "http://maps.googleapis.com/maps/api/staticmap?center=%1$s,%2$s&zoom=15&size=%3$sx%4$s&markers=%1$s,%2$s&sensor=false";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// avoid NPEs
		if (!new ActivityHelper(this).onCreate()) {
			return;
		}

		setContentView(R.layout.activity_info);

		ImageView leftIcon = (ImageView) findViewById(R.id.leftIcon);
		leftIcon.setImageResource(R.drawable.back_arrow);
		leftIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		TextView subheader = (TextView) findViewById(R.id.subheader);
		subheader.setText(R.string.title_location);
		subheader.setTag("sticky");
		((ViewGroup) subheader.getParent()).removeView(subheader);

		mData = KodIO.DATA.optJSONObject("info");

		setupMap();

		TextView title = (TextView) findViewById(R.id.title);
		TextView address = (TextView) findViewById(R.id.address);

		title.setText(mData.optString("title"));
		address.setText(Html.fromHtml(mData.optString("detail_html")));

		LinearLayout container = (LinearLayout) findViewById(R.id.container);
		container.addView(subheader, 0);

		JSONArray organizers = mData.optJSONArray("organizers");
		for (int i = 0; i < organizers.length(); i++) {
			OrganizerView organizer = new OrganizerView(this);

			ViewGroup.LayoutParams params = new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			organizer.setLayoutParams(params);

			organizer.setData(organizers.optJSONObject(i));
			container.addView(organizer);
		}

		LayoutInflater inflater = LayoutInflater.from(this);
		View footer = inflater.inflate(R.layout.footer, null);
		container.addView(footer);
	}

	private void setupMap() {
		final ImageView map = (ImageView) findViewById(R.id.map);
		ViewTreeObserver vto = map.getViewTreeObserver();
		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
			public boolean onPreDraw() {
				int width = map.getMeasuredWidth() * 3 / 4;
				int height = map.getMeasuredHeight() * 3 / 4;
				JSONObject location = mData.optJSONObject("location");
				String url = String.format(MAP_URL, location.optDouble("lat"),
						location.optDouble("long"), width, height);

				ImageLoader.getInstance().displayImage(url, map);

				map.getViewTreeObserver().removeOnPreDrawListener(this);

				return true;
			}
		});
		map.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject location = mData.optJSONObject("location");
				launchGoogleMaps(getApplicationContext(),
						location.optDouble("lat"), location.optDouble("long"));
			}
		});
	}

	private void launchGoogleMaps(Context context, double latitude,
			double longitude) {
		try {
			String format = "geo:0,0?q=" + Double.toString(latitude) + ","
					+ Double.toString(longitude);
			Uri uri = Uri.parse(format);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			//maps application is unavailable, fallback to browser
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(String.format(
							"http://maps.google.com/maps?q=%1$s,%2$s&z=15",
							latitude, longitude)));
			startActivity(intent);
		}
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
