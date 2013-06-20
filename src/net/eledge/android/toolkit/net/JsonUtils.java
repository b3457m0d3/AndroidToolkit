package net.eledge.android.toolkit.net;

import org.json.JSONObject;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.json.JsonLoaderTask;

public class JsonUtils {

	public static void readJson(AsyncLoaderListener<JSONObject> listener, String url, String charset) {
		new JsonLoaderTask(listener).execute(new String[] { url, charset });
	}

}
