package com.darkrockstudios.apps.tminus.misc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by Adam on 7/15/13.
 * Dark Rock Studios
 * darkrockstudios.com
 * <p/>
 * Shamelessly pilfered from:
 * http://howrobotswork.wordpress.com/category/android/volley/networkimageview/
 */
public class DiskBitmapCache extends DiskBasedCache implements ImageLoader.ImageCache
{
	public DiskBitmapCache( File rootDirectory, int maxCacheSizeInBytes )
	{
		super( rootDirectory, maxCacheSizeInBytes );
	}

	public DiskBitmapCache( File cacheDir )
	{
		super( cacheDir );
	}

	public Bitmap getBitmap( String url )
	{
		final Entry requestedItem = get( url );

		if( requestedItem == null )
		{
			return null;
		}

		return BitmapFactory.decodeByteArray( requestedItem.data, 0, requestedItem.data.length );
	}

	public void putBitmap( String url, Bitmap bitmap )
	{
		final Entry entry = new Entry();

		ByteBuffer buffer = ByteBuffer.allocate( bitmap.getByteCount() );
		bitmap.copyPixelsToBuffer( buffer );
		entry.data = buffer.array();

		put( url, entry );
	}
}