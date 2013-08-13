package net.eledge.android.toolkit.async;

import net.eledge.android.toolkit.async.listener.TaskListener;

public class ListenerNotifier<T> implements Runnable {

	private T result;

	private TaskListener<T> listener;

	public ListenerNotifier(TaskListener<T> listener, T result) {
		this.listener = listener;
		this.result = result;
	}

	public void run() {
		listener.onTaskFinished(result);
	}

}
