package com.example.toshiba.instagramclone.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.toshiba.instagramclone.R;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class UniversalImageLoader {

    private static final int defaultImage = R.drawable.ic_android;
    private Context mContext;

    public UniversalImageLoader(Context context) {
        mContext = context;
    }

    public ImageLoaderConfiguration getConfig(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
        .showImageOnLoading(defaultImage)
        .showImageForEmptyUri(defaultImage)
                .considerExifParams(true)
        .showImageOnFail(defaultImage)
        .cacheOnDisk(true).cacheInMemory(true)
        .cacheOnDisk(true).resetViewBeforeLoading(true)
        .imageScaleType(ImageScaleType.EXACTLY)
        .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .diskCacheSize(100 * 1024 * 1024).build();

        return configuration;
    }

    /**
     * this method can be used to set images that are static. It can't be used if the images
     * are being changed in the fragment activity or if they are being set in a
     * gridview
     * @param imgURL
     * @param image
     * @param mProgressBar
     * @param append
     */
    public static void setImage(String imgURL, ImageView image, final ProgressBar mProgressBar,String append){
        try{
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View v) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(v.VISIBLE);

                }
            }

            @Override
            public void onLoadingFailed(String imageUri, View v, FailReason failReason) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(v.GONE);

                }

            }

            @Override
            public void onLoadingComplete(String imageUri, View v, Bitmap loadedImage) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(v.GONE);

                }

            }

            @Override
            public void onLoadingCancelled(String imageUri, View v) {
                if(mProgressBar != null){
                    mProgressBar.setVisibility(v.GONE);

                }

            }
        });
        }catch(IllegalStateException e){

        }
    }
}
