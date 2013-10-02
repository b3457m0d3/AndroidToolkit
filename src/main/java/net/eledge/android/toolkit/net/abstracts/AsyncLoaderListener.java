package net.eledge.android.toolkit.net.abstracts;


public interface AsyncLoaderListener<T> {
	
	void onFinished(T result, int httpStatus);
	
}