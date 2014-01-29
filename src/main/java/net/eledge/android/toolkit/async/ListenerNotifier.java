/*
 * Copyright (c) 2014 eLedge.net and the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
        for (TaskListener<T> listener : this.listeners) {
            if (notifyStart) {
                listener.onTaskStart();
            } else {
                listener.onTaskFinished(result);
            }
        }
    }

}
