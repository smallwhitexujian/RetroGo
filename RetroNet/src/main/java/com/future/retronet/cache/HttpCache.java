package com.future.retronet.cache;

import android.content.Context;

import com.future.retronet.logger.LogUtil;

import java.io.File;

import okhttp3.Cache;

public class HttpCache {
    /**
     * set cahe times is 3 days
     */
    public static final int maxStale = 60 * 60 * 24 * 3;
    /**
     * read from cache for 60 s
     */
    public static final int maxAge = 60;
    /**
     * cache file's max size (10 MB)
     */
    public static final int maxSize = 10 * 1024 * 1024;

    /**
     * create the cache file to store the cache
     *
     * @param context the Context can get the CacheDir {@link Context#getCacheDir()}
     * @see #getCache(File)
     */
    public static Cache getCache(Context context) {
        return getCache(new File(context.getExternalCacheDir(), "retro-cache/"));
    }

    /**
     * create the cache file to store the cache
     *
     * @param dirPath the dirPath will store cache
     * @see #getCache(File)
     */
    public static Cache getCache(String dirPath) {
        return getCache(new File(dirPath));
    }

    /**
     * create the cache file to store the cache, and max size is 10mb
     *
     * @param cacheFileDir the dir will store cache
     */
    public static Cache getCache(File cacheFileDir) {
        Cache cache = null;
        try {
            if (cacheFileDir == null) {
                LogUtil.e("Cache file is null.");
                return null;
            }
            //create cache dir
            if (!cacheFileDir.exists()) {
                cacheFileDir.mkdirs();
            }
            if (!cacheFileDir.canRead()) {
                LogUtil.e("Cache dir is unreadable. please check sdcard");
                return null;
            }
            cache = new Cache(cacheFileDir, maxSize);
        } catch (Exception e) {
            LogUtil.e("Could not create cache dir!");
        }
        return cache;
    }
}
