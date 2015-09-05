package com.lorem_ipsum.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.ImageView;

import com.lorem_ipsum.R;
import com.lorem_ipsum.requests.AuthImageDownloader;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by originally.us on 8/24/14.
 */
public class ImageLoaderUtils {

    private ImageLoaderUtils() {
    }


    //******************************************************************************
    // Universal Image Loader configurations
    //******************************************************************************

    public static void configImageLoader(Context context) {
        // get cache dir
        File cacheDir = StorageUtils.getCacheDirectory(context.getApplicationContext());
        if (cacheDir == null)
            cacheDir = Environment.getDownloadCacheDirectory();

        // init display options
        DisplayImageOptions options = getDisplayImageOptions(R.drawable.img_item_place_holder);

        // init config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context.getApplicationContext())
                .threadPoolSize(4)
                .memoryCache(new LRULimitedMemoryCache(3 * 1024 * 1024))
                .diskCache(new LimitedAgeDiscCache(cacheDir, 3600 * 24 * 7))
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .imageDownloader(new AuthImageDownloader(context.getApplicationContext()))
                .defaultDisplayImageOptions(options)
                .build();

        // init image loader
        ImageLoader.getInstance().init(config);
    }

    private static DisplayImageOptions getDisplayImageOptions(int placeHolderResource) {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(placeHolderResource)
                .showImageForEmptyUri(placeHolderResource)
                .showImageOnFail(placeHolderResource)
                //.displayer(new FadeInBitmapDisplayer(200))
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .resetViewBeforeLoading(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();
        return options;
    }


    //******************************************************************************
    // Util functions
    //******************************************************************************

    public static void displayImage(int drawableResourceId, ImageView imageView) {
        if (drawableResourceId <= 0) {
            imageView.setImageResource(0);
            return;
        }
        ImageLoader.getInstance().displayImage("drawable://" + drawableResourceId, imageView);
    }

    public static void displayImage(String imageUrl, ImageView imageView) {
        ImageLoader.getInstance().displayImage(imageUrl, imageView);
    }

    public static void displayImage(String imageUrl, ImageView imageView, int placeHolder) {
        DisplayImageOptions options = getDisplayImageOptions(placeHolder);
        ImageLoader.getInstance().displayImage(imageUrl, imageView, options);
    }

}
