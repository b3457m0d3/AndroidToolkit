package net.eledge.android.toolkit.net.abstracts;

import org.json.JSONObject;

public interface JsonLoadedListener {
	public void onLoadingFailed(int httpStatus);
	public void onLoadingFinished(JSONObject json);
}