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

package net.eledge.android.toolkit.net;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.image.ImageTask;
import net.eledge.android.toolkit.net.internal.image.ImageTaskData;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressLint("SimpleDateFormat")
public class ImageCacheManager {

    public long cacheDuration;

    public final List<ImageTask> stack = new ArrayList<>();

    public File cacheDir;

    public final HashMap<String, SoftReference<Bitmap>> refcache = new HashMap<>();

    public SimpleDateFormat mDateFormatter;

    public ImageCacheManager(Context context, long cacheDuration) {
        this.cacheDuration = cacheDuration;
        cacheDir = context.getCacheDir();
        assert cacheDir != null;
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        mDateFormatter = new SimpleDateFormat("EEE',' dd MMM yyyy HH:mm:ss zzz");
    }

    public void displayImage(String url, ImageView imageView,
                             int defaultDrawableId) {
        displayImage(url, imageView, defaultDrawableId, null);
    }

    public void displayImage(String url, ImageView imageView,
                             int defaultDrawableId, AsyncLoaderListener<Bitmap> listener) {
        if (refcache.containsKey(url)) {
            imageView.setImageBitmap(refcache.get(url).get());

        } else {
            queue(url, imageView, defaultDrawableId, listener);
            if (defaultDrawableId != -1) {
                imageView.setImageResource(defaultDrawableId);
            }
        }
    }

    private void queue(String url, ImageView imageView, int defaultDrawableId,
                       AsyncLoaderListener<Bitmap> listener) {
        ImageTask task = (ImageTask) imageView.getTag();
        if (task != null) {
            task.cancel(true);
        }
        imageView.setImageBitmap(null);
        ImageTaskData data = new ImageTaskData(url, imageView, defaultDrawableId, listener);
        task = new ImageTask(this, data);
        imageView.setTag(task);
        synchronized (stack) {
            stack.add(task);
            stack.notifyAll();
        }
        task.execute(url);
    }

    public void clearQueue() {
        for (ImageTask task : stack) {
            task.cancel(true);
        }
    }

    public void clearCache() {
        clearQueue();
        refcache.clear();
        for (File file : cacheDir.listFiles()) {
            file.delete();
        }

    }

}
