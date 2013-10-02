package net.eledge.android.toolkit.async;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.eledge.android.toolkit.async.listener.TaskListener;

public class ListenerNotifier<T> implements Runnable {

	private final T result;

	private final List<TaskListener<T>> listeners = new ArrayList<TaskListener<T>>();

	public ListenerNotifier(TaskListener<T> listener, T result) {
		this.listeners.add(listener);
		this.result = result;
	}
	
	public ListenerNotifier(Collection<TaskListener<T>> listeners, T result) {
		this.listeners.addAll(listeners);
		this.result = result;
	}

	public void run() {
		for (TaskListener<T> listener: this.listeners) {
			listener.onTaskFinished(result);
		}
	}

}
