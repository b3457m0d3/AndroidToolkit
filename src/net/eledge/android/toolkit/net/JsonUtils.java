package net.eledge.android.toolkit.net;


import net.eledge.android.toolkit.net.internal.JsonLoaderTask;

import org.json.JSONObject;


public class JsonUtils {

	public static void readJson(JsonLoadedListener listener, String url, String charset) {
		new JsonLoaderTask(listener).execute(new String[] { url, charset });
	}

	public interface JsonLoadedListener {
		public void onLoadingFailed(int httpStatus);
		public void onLoadingFinished(JSONObject json);
	}

}
