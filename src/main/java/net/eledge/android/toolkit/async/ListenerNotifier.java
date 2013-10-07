package net.eledge.android.toolkit.async;

import net.eledge.android.toolkit.async.listener.TaskListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListenerNotifier<T> implements Runnable {

	private T result = null;

    private boolean notifyStart = false;

	private final List<TaskListener<T>> listeners = new ArrayList<TaskListener<T>>();

    public ListenerNotifier(TaskListener<T> listener) {
        this.listeners.add(listener);
        notifyStart = true;
    }

    public ListenerNotifier(Collection<TaskListener<T>> listeners) {
        this.listeners.addAll(listeners);
        notifyStart = true;
    }

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
            if (notifyStart) {
                listener.onTaskStart();
            } else {
                listener.onTaskFinished(result);
            }
		}
	}

}
