package com.darkrockstudios.apps.tminus;

import android.app.Application;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.darkrockstudios.apps.tminus.misc.BitmapCache;

/**
 * Created by Adam on 6/30/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class TMinusApplication extends Application
{
	private static RequestQueue s_requestQueue;
	private static final int MAX_CACHE_SIZE = 5 * 1014 * 1024;
	private static BitmapCache s_bitmapCache;

	public void onCreate()
	{
		super.onCreate();

		PreferenceManager.setDefaultValues( this, R.xml.general_preferences, false );

		s_requestQueue = Volley.newRequestQueue( this );
		s_bitmapCache = new BitmapCache( MAX_CACHE_SIZE );
	}

	public static RequestQueue getRequestQueue()
	{
		return s_requestQueue;
	}

	public static BitmapCache getBitmapCache()
	{
		return s_bitmapCache;
	}
}
