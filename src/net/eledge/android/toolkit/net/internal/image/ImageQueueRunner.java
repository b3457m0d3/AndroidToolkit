package net.eledge.android.toolkit.net.internal.image;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;

import net.eledge.android.toolkit.net.ImageCacheManager;
import net.eledge.android.toolkit.net.internal.image.ImageQueue.ImageQueueItem;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageQueueRunner {

	private ImageCacheManager cacheManager;
	protected Thread thread;
	private QueueRunner runner = new QueueRunner();

	/** Creates a new instance of the ImageThreadLoader */
	public ImageQueueRunner(ImageCacheManager imageCacheManager) {
		this.cacheManager = imageCacheManager;
		thread = new Thread(runner);
//		thread.setPriority(Thread.NORM_PRIORITY - 1);
	}

	/**
	 * Provides a Runnable class to handle loading the image from the URL and
	 * settings the ImageView on the UI thread.
	 */
	private class QueueRunner implements Runnable {
		public void run() {
			try {
				while (true) {
					if (cacheManager.queue.stack.size() == 0) {
						synchronized (cacheManager.queue.stack) {
							cacheManager.queue.stack.wait();
						}
					}
					
					if(Thread.interrupted()) {
						break;
					}

					if (cacheManager.queue.stack.size() != 0) {
						final ImageQueueItem item;
						synchronized (cacheManager.queue.stack) {
							item = cacheManager.queue.stack.pop();
						}

						final Bitmap bmp = loadBitmap(item.url);

//						Object tag = item.imageView.getTag();
//						if (tag != null && ((String) tag).equals(item.url)) {
							Activity a = (Activity) item.imageView.getContext();
							a.runOnUiThread(new BitmapDisplayer(bmp, item));
//						}
					}
					
					if(Thread.interrupted()) {
						break;
					}

				}

			} catch (InterruptedException e) {}
		}
	}
	
	private Bitmap loadBitmap(String url) {
		try {
			URLConnection openConnection = new URL(url).openConnection();
			String filename = String.valueOf(url.hashCode());

			File bitmapFile = new File(cacheManager.cacheDir, filename);
			Bitmap bitmap = BitmapFactory.decodeFile(bitmapFile.getPath());

			if (bitmap != null) {
				long bitmapTimeMillis = bitmapFile.lastModified();
				if ((System.currentTimeMillis() - bitmapTimeMillis) >= cacheManager.cacheDuration) {
					String lastMod = openConnection
							.getHeaderField("Last-Modified");
					long lastModTimeMillis = cacheManager.mDateFormatter.parse(lastMod)
							.getTime();

					if (lastModTimeMillis > bitmapTimeMillis) {
						// Discard the connection and return the cached version
						bitmap = null;
					}
				}
			}
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeStream(openConnection
						.getInputStream());
				cacheFile(bitmap, bitmapFile);
			}
			cacheManager.refcache.put(url, new SoftReference<Bitmap>(bitmap));
			return bitmap;

		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}	

	private void cacheFile(Bitmap bitmap, File bitmapFile) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(bitmapFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (Exception ex) {
			}
		}
	}
	
	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageQueueItem item;

		public BitmapDisplayer(Bitmap b, ImageQueueItem item) {
			this.bitmap = b;
			this.item = item;
		}

		public void run() {
			if (bitmap != null) {
				item.imageView.setImageBitmap(bitmap);
				if (item.listener != null) {
					item.listener.onFinished(bitmap, HttpStatus.SC_OK);
				}
			} else {
				if (item.imageResource != -1) {
					item.imageView.setImageResource(item.imageResource);
				}
				if (item.listener != null) {
					item.listener.onFinished(null, HttpStatus.SC_NOT_FOUND);
				}
			}
		}
	}

}
