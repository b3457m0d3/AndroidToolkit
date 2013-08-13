package net.eledge.android.toolkit.async.listener;

public interface TaskListener<T> {

	void onTaskFinished(T result);
	
}
