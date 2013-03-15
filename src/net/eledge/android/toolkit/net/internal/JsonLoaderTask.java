package net.eledge.android.toolkit.net.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.eledge.android.toolkit.net.abstracts.JsonLoadedListener;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public class JsonLoaderTask extends AsyncTask<String, Void, JSONObject> {

	private JsonLoadedListener mListener;

	private int mHttpStatus;

	public JsonLoaderTask(JsonLoadedListener listener) {
		mListener = listener;
	}

	@Override
	protected JSONObject doInBackground(String... params) {
		try {
			HttpResponse response = new DefaultHttpClient().execute(new HttpGet(params[0]));
			mHttpStatus = response.getStatusLine().getStatusCode();
			if (mHttpStatus == HttpStatus.SC_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),
						params[1]));
				StringBuilder json = new StringBuilder();
				String line = reader.readLine();
				while (line != null) {
					json.append(line);
					line = reader.readLine();
				}
				return new JSONObject(json.toString());
			}
		} catch (IOException e) {
		} catch (JSONException e) {
		}
		return null;
	}

	@Override
	protected void onPostExecute(JSONObject json) {
		if (json != null) {
			mListener.onLoadingFinished(json);
		} else {
			mListener.onLoadingFailed(mHttpStatus);
		}
	}

}