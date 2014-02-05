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

package net.eledge.android.toolkit.gui;

import android.app.Activity;
import android.view.View;

import net.eledge.android.toolkit.gui.annotations.ViewResource;

import java.lang.reflect.Field;

public class ViewInjector {

    public enum Finder {
        VIEW {
            @Override
            public View findViewById(Object source, int id) {
                return ((View) source).findViewById(id);
            }
        },
        ACTIVITY {
            @Override
            public View findViewById(Object source, int id) {
                return ((Activity) source).findViewById(id);
            }
        };

        public abstract View findViewById(Object source, int id);
    }

    public static void inject(Activity target) throws IllegalArgumentException {
        inject(target, target);
    }

    public static void inject(Object target, Activity source) throws IllegalArgumentException {
        injectViews(target, source, Finder.ACTIVITY);
    }

    public static void inject(View target) throws IllegalArgumentException {
        inject(target, target);
    }

    public static void inject(Object target, View source) throws IllegalArgumentException {
        injectViews(target, source, Finder.VIEW);
    }

    private static void injectViews(Object target, Object source, Finder finder) throws IllegalArgumentException {
        Class<?> clazz = target.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(ViewResource.class)) {
                ViewResource viewResource = field.getAnnotation(ViewResource.class);
                View view = finder.findViewById(source, viewResource.value());
                if (view != null) {
                    try {
                        field.set(target, view);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException("Field not accessible " + field.getName());
                    }
                } else {
                    if (!viewResource.optional()) {
                        throw new IllegalArgumentException("No valid view found for " + field.getName());
                    }
                }
            }
        }
    }

}
