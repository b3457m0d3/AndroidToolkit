package net.eledge.android.toolkit.net.internal.image;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import net.eledge.android.toolkit.net.ImageCacheManager;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLConnection;

public class ImageTask extends AsyncTask<String, Void, Bitmap> {
	
	private ImageCacheManager cacheManager;
	private ImageTaskData data;
	
	public ImageTask(ImageCacheManager imageCacheManager, ImageTaskData data) {
		this.cacheManager = imageCacheManager;
		this.data = data;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap bitmap = null;
		if(!isCancelled()) {
			bitmap = loadBitmap(params[0]);
	    }
		return bitmap;
	}
	
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if(!isCancelled()) {
			if(data.imageView != null) {
				Activity a = (Activity) data.imageView.getContext();
                if (a != null) {
                    a.runOnUiThread(new BitmapDisplayer(bitmap, data));
                }
            }
		}
	}
	
	@Override
	protected void onCancelled() {
		cacheManager.stack.remove(this);
		super.onCancelled();
	}
	
	private Bitmap loadBitmap(String url) {
		try {
			URLConnection openConnection = new URL(url).openConnection();
			openConnection.setConnectTimeout(500);
			String filename = String.valueOf(url.hashCode());
			Bitmap bitmap = null;
			if(isCancelled()) {
				return null;
			}
			File bitmapFile = new File(cacheManager.cacheDir, filename);
			if (bitmapFile.exists()) {
				bitmap = BitmapFactory.decodeFile(bitmapFile.getPath());
			}
			if(isCancelled()) {
				return null;
			}
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
			if(isCancelled()) {
				return null;
			}
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeStream(openConnection
						.getInputStream());
				cacheFile(bitmap, bitmapFile);
			}
			cacheManager.refcache.put(url, new SoftReference<>(bitmap));
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
			} catch (Exception e) {
                Log.e(this.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	private class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		ImageTaskData item;

		public BitmapDisplayer(Bitmap b, ImageTaskData item) {
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
