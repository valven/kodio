package com.valven.kodio.activity;

import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.valven.kodio.R;
import com.valven.kodio.helper.ActivityHelper;
import com.valven.kodio.util.ImageLoader;
import com.valven.kodio.util.Utils;

public class SpeakerDetailActivity extends Activity {

	public static final String ARG_DATA = "speaker_data";

	private JSONObject mData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// avoid NPEs
		if (!new ActivityHelper(this).onCreate()) {
			return;
		}

		setContentView(R.layout.activity_speaker_detail);

		ImageView leftIcon = (ImageView) findViewById(R.id.leftIcon);
		leftIcon.setImageResource(R.drawable.back_arrow);
		leftIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String json = extras.getString(ARG_DATA);
		if (json != null) {
			try {
				mData = new JSONObject(json);
			} catch (JSONException e) {
			}
		}

		initComponents();

	}

	private void initComponents() {
		if (mData == null)
			return;

		TextView title = (TextView) findViewById(R.id.subheader);
		ImageView image = (ImageView) findViewById(R.id.image);
		TextView detailTitle = (TextView) findViewById(R.id.detail_title);
		TextView speechTitle = (TextView) findViewById(R.id.speech_title);
		TextView time = (TextView) findViewById(R.id.detail_time);
		TextView bio = (TextView) findViewById(R.id.detail_bio);
		TextView info = (TextView) findViewById(R.id.detail_info);
		ImageView github = (ImageView) findViewById(R.id.github);
		ImageView twitter = (ImageView) findViewById(R.id.twitter);

		ImageLoader.getInstance()
				.displayImage(mData.optString("avatar"), image);
		title.setText(mData.optString("name"));
		detailTitle.setText(mData.optString("title"));
		speechTitle.setText(mData.optString("speech_title"));
		time.setText(Utils.getTime(mData.optString("speech_time")));

		bio.setMovementMethod(LinkMovementMethod.getInstance());
		bio.setText(Html.fromHtml(mData.optString("bio_html")));
		
		info.setMovementMethod(LinkMovementMethod.getInstance());
		info.setText(Html.fromHtml(mData.optString("speech_detail_html")));

		String githubUrl = mData.optString("github");
		if (TextUtils.isEmpty(githubUrl)) {
			github.setVisibility(View.INVISIBLE);
		} else {
			try {
				new URL(githubUrl);

				// url is valid
				github.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						String githubUrl = mData.optString("github");

						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(githubUrl));
						startActivity(browserIntent);
					}
				});
			} catch (MalformedURLException e) {
				// url is invalid
				github.setVisibility(View.INVISIBLE);
			}
		}

		String twitterUser = mData.optString("twitter");
		if (TextUtils.isEmpty(twitterUser)) {
			twitter.setVisibility(View.INVISIBLE);
		} else {
			twitter.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String twitterUser = mData.optString("twitter");

					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
							.parse("http://twitter.com/" + twitterUser));
					startActivity(browserIntent);
				}
			});
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
