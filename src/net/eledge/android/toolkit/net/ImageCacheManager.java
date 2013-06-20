package net.eledge.android.toolkit.net;

import java.io.File;
import java.lang.ref.SoftReference;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import net.eledge.android.toolkit.net.internal.image.ImageQueue;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

@SuppressLint("SimpleDateFormat")
public class ImageCacheManager {

	public long cacheDuration;

	public ImageQueue queue;

	public File cacheDir;

	public final HashMap<String, SoftReference<Bitmap>> refcache = new HashMap<String, SoftReference<Bitmap>>();

	public SimpleDateFormat mDateFormatter;

	public ImageCacheManager(Context context, long cacheDuration) {
		this.cacheDuration = cacheDuration;
		queue = new ImageQueue(this);
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
			queue.queue(url, imageView, defaultDrawableId, listener);
			if (defaultDrawableId != -1) {
				imageView.setImageResource(defaultDrawableId);
			}
		}
	}
	
	public void clearQueue() {
		queue.clean();
	}
	
	public void clearCache() {
		queue.clean();
		refcache.clear();
		for (File file: cacheDir.listFiles()) {
			file.delete();
		}
		
	}
	
}
