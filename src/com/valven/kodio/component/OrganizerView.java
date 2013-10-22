package com.valven.kodio.component;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.valven.kodio.R;
import com.valven.kodio.util.ImageLoader;

public class OrganizerView extends LinearLayout {

	private JSONObject mData;

	public OrganizerView(Context context) {
		super(context);
		setOrientation(VERTICAL);
	}

	public void setData(JSONObject data) {
		mData = data;

		initComponents();
	}

	private void initComponents() {
		CustomTextView title = new CustomTextView(getContext());
		title.setText(mData.optString("title"));
		title.setGravity(Gravity.CENTER);
		title.setCustomFont(getContext(), "NoticiaText-Regular.ttf");
		title.setTextSize(18);
		title.setTextColor(getResources().getColor(android.R.color.white));
		title.setBackgroundColor(getResources().getColor(
				R.color.speakers_sub_header_bg));
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				16, getResources().getDisplayMetrics());
		title.setPadding(0, (int) padding, 0, (int) padding);

		LayoutParams params = new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		title.setLayoutParams(params);
		title.setTag("sticky");

		addView(title);
		Log.d("logo", padding+"");
		//add images of companies
		JSONArray companies = mData.optJSONArray("companies");
		LayoutInflater inflater = LayoutInflater.from(getContext());
		for (int i = 0; i < companies.length(); i++) {
			JSONObject company = companies.optJSONObject(i);
			ImageView image = (ImageView)inflater.inflate(R.layout.organizer_logo, null);
			addView(image);
			
			final String url = company.optString("url");
			image.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(url));
					v.getContext().startActivity(browserIntent);
				}
			});
			
			ImageLoader.getInstance().displayImage(company.optString("image"),
					image);
		}
	}

}
