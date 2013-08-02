package com.darkrockstudios.apps.tminus.misc;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * Created by Adam on 8/1/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class BitmapCache extends LruCache implements ImageCache
{
	public BitmapCache( int maxSize )
	{
		super( maxSize );
	}

	@Override
	public Bitmap getBitmap( String url )
	{
		return (Bitmap)get( url );
	}

	@Override
	public void putBitmap( String url, Bitmap bitmap )
	{
		put( url, bitmap );
	}
}