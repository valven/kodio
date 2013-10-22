package com.valven.kodio.activity;

import java.util.Comparator;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.analytics.tracking.android.EasyTracker;
import com.valven.kodio.KodIO;
import com.valven.kodio.R;
import com.valven.kodio.adapter.SpeakerAdapter;
import com.valven.kodio.helper.ActivityHelper;
import com.valven.kodio.util.Utils;

public class SpeakersActivity extends ListActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//avoid NPEs
		if (!new ActivityHelper(this).onCreate()){
			return;
		}
		
		setContentView(R.layout.activity_speakers);
		
		ImageView rightIcon = (ImageView)findViewById(R.id.rightIcon);
		rightIcon.setImageResource(R.drawable.info_icon);
		rightIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SpeakersActivity.this, InfoActivity.class);
				startActivity(intent);
			}
		});
		
		TextView title = (TextView)findViewById(R.id.subheader);
		title.setText(R.string.title_speakers);
		
		JSONArray sessions = KodIO.DATA.optJSONArray("sessions");
		sessions = Utils.sort(sessions, new Comparator<Object>(){
		   public int compare(Object a, Object b){
		      JSONObject ja = (JSONObject)a;
		      JSONObject jb = (JSONObject)b;
		      Date d1 = Utils.getDate(ja.optString("speech_time"));
		      Date d2 = Utils.getDate(jb.optString("speech_time"));
		      return d1.compareTo(d2);
		   }
		});

		SpeakerAdapter adapter = new SpeakerAdapter(getApplicationContext(),
				sessions);
		setListAdapter(adapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> list, View view, int position,
					long id) {
				JSONObject data = (JSONObject)getListAdapter().getItem(position);
				Intent intent = new Intent(getApplicationContext(), SpeakerDetailActivity.class);
				Bundle extras = new Bundle();
				extras.putString(SpeakerDetailActivity.ARG_DATA, data.toString());
				intent.putExtras(extras);
				startActivity(intent);
			}
		});
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
