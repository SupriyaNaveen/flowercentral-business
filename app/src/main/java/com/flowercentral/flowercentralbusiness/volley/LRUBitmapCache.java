package com.flowercentral.flowercentralbusiness.volley;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

class LRUBitmapCache extends LruCache<String, Bitmap>
        implements ImageLoader.ImageCache {


    private LRUBitmapCache(int maxSize) {
        super(maxSize);
    }

    LRUBitmapCache() {
        this(getDefaultLruCacheSize());
    }

    private static int getDefaultLruCacheSize() {
        return (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getByteCount() / 1024;

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
//            return value.getByteCount() / 1024;
//        } else {
//            return (value.getRowBytes() * value.getHeight()) / 1024;
//        }
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url, bitmap);
    }
}
