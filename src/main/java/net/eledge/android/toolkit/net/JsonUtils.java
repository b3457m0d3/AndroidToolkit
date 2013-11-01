package net.eledge.android.toolkit.net;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.json.JsonLoaderTask;

import org.json.JSONObject;

public class JsonUtils {

	public static void readJson(AsyncLoaderListener<JSONObject> listener, String url, String charset) {
		new JsonLoaderTask(listener).execute(url, charset);
	}

}
