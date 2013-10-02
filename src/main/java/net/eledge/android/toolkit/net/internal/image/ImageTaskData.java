package net.eledge.android.toolkit.net.internal.image;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class ImageTaskData {
	
	public String url;
	public ImageView imageView;
	public int imageResource = -1;
	public AsyncLoaderListener<Bitmap> listener = null;

	public ImageTaskData(String url, ImageView imageView, int imageResource,
			AsyncLoaderListener<Bitmap> listener) {
		this.url = url;
		this.imageView = imageView;
		this.imageResource = imageResource;
		this.listener = listener;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ImageTaskData) {
			ImageTaskData oItem = (ImageTaskData) o;
			return this.imageView.equals(oItem.imageView);
		}
		return false;
	}
}
