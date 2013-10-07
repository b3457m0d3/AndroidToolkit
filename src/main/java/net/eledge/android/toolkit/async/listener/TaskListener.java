package net.eledge.android.toolkit.async.listener;

public interface TaskListener<T> {

    void onTaskStart();

	void onTaskFinished(T result);
	
}
