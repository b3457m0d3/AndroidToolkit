package net.eledge.android.toolkit.net;

import net.eledge.android.toolkit.net.abstracts.JsonLoadedListener;
import net.eledge.android.toolkit.net.internal.JsonLoaderTask;

public class JsonUtils {

	public static void readJson(JsonLoadedListener listener, String url, String charset) {
		new JsonLoaderTask(listener).execute(new String[] { url, charset });
	}

}
