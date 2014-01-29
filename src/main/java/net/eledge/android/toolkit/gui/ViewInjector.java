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
        for (Field field : clazz.getFields()) {
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
