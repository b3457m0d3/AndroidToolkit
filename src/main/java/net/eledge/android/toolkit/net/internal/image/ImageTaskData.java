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

package net.eledge.android.toolkit.net.internal.image;

import android.graphics.Bitmap;
import android.widget.ImageView;

import net.eledge.android.toolkit.net.abstracts.AsyncLoaderListener;

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
