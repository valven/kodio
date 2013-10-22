package com.valven.kodio.adapter;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.valven.kodio.R;
import com.valven.kodio.util.ImageLoader;
import com.valven.kodio.util.Utils;

public class SpeakerAdapter extends BaseAdapter {

	private JSONArray mData;

	private Context mContext;

	private LayoutInflater mInflater;

	private class ViewHolder {
		private ImageView picture;
		private TextView date;
		private TextView name;
		private TextView info;
	}

	public SpeakerAdapter(Context context, JSONArray data) {
		mContext = context;
		mData = data;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mData.length();
	}

	@Override
	public Object getItem(int position) {
		return mData.opt(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.speaker_row, null);

			holder = new ViewHolder();
			holder.date = (TextView) view.findViewById(R.id.time);
			holder.picture = (ImageView) view.findViewById(R.id.image);
			holder.info = (TextView) view.findViewById(R.id.speaker_info);
			holder.name = (TextView) view.findViewById(R.id.speaker_name);

			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		JSONObject data = mData.optJSONObject(position);
		holder.name.setText(data.optString("name"));
		holder.info.setText(data.optString("title"));

		holder.date.setText(Utils.getTime(data.optString("speech_time")));
		holder.picture.setImageResource(0);
		ImageLoader.getInstance().displayImage(data.optString("avatar"),
				holder.picture);

		return view;
	}

}
