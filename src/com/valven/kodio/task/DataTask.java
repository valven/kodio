package com.valven.kodio.task;

import android.os.AsyncTask;
import android.util.Log;

import com.valven.kodio.model.Response;
import com.valven.kodio.util.HttpUtils;
import com.valven.kodio.util.HttpUtils.HttpRequest;

public class DataTask extends AsyncTask<Object, Void, Response> {
	
	private Callback mCallback;

	public DataTask(Callback callback) {
		mCallback = callback;
	}

	@Override
	protected Response doInBackground(Object... params) {
		String url = (String) params[0];
		HttpRequest request = (HttpRequest) params[1];

		Response response = HttpUtils.sendRequest(url, null, null, request);
		return response;
	}

	@Override
	protected void onPostExecute(Response response) {
		super.onPostExecute(response);
		String data = response != null ? response.getContent() : null;
		Log.d("task", "response :" + data);
		mCallback.handleResponse(data);
	}

	@Override
	protected void onCancelled() {
		super.onCancelled();
		mCallback.handleResponse(null);
	}

}
