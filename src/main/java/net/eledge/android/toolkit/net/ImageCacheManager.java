package net.eledge.android.toolkit.net;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.image.ImageTask;
import net.eledge.android.toolkit.net.internal.image.ImageTaskData;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

@SuppressLint("SimpleDateFormat")
public class ImageCacheManager {

	public long cacheDuration;

	public List<ImageTask> stack = new ArrayList<ImageTask>();

	public File cacheDir;

	public final HashMap<String, SoftReference<Bitmap>> refcache = new HashMap<String, SoftReference<Bitmap>>();

	public SimpleDateFormat mDateFormatter;

	public ImageCacheManager(Context context, long cacheDuration) {
		this.cacheDuration = cacheDuration;
		cacheDir = context.getCacheDir();
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
		ImageTask task = (ImageTask)imageView.getTag();
        if(task != null) {
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
		for (File file: cacheDir.listFiles()) {
			file.delete();
		}
		
	}
	
}
