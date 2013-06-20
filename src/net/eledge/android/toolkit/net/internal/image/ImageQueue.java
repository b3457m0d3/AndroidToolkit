package net.eledge.android.toolkit.net.internal.image;

import java.util.Stack;

import net.eledge.android.toolkit.net.ImageCacheManager;
import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageQueue {

	private ImageQueueRunner runner;

	public ImageQueue(ImageCacheManager imageCacheManager) {
		runner = new ImageQueueRunner(imageCacheManager);
	}

	protected Stack<ImageQueueItem> stack = new Stack<ImageQueueItem>();

	public void queue(String url, ImageView imageView, int defaultDrawableId,
			AsyncLoaderListener<Bitmap> listener) {
		clean(imageView);
		synchronized (stack) {
			stack.push(new ImageQueueItem(url, imageView, defaultDrawableId, listener));
			stack.notifyAll();
		}
		if (runner.thread.getState() == Thread.State.NEW){
			runner.thread.start();
		}
	}

	public void clean() {
		stack.clear();
	}

	public void clean(ImageView imageView) {
		stack.remove(imageView);
	}

	public class ImageQueueItem {
		public String url;
		public ImageView imageView;
		public int imageResource = -1;
		public AsyncLoaderListener<Bitmap> listener = null;

		private ImageQueueItem(String url, ImageView imageView,
				int imageResource, AsyncLoaderListener<Bitmap> listener) {
			this.url = url;
			this.imageView = imageView;
			this.imageResource = imageResource;
			this.listener = listener;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof ImageQueueItem) {
				ImageQueueItem oItem = (ImageQueueItem) o;
				return this.imageView.equals(oItem.imageView);
			}
			return false;
		}
	}

}
