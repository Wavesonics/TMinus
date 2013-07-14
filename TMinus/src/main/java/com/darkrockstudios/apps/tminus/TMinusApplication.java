package com.darkrockstudios.apps.tminus;

import android.app.Application;
import android.preference.PreferenceManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Adam on 6/30/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class TMinusApplication extends Application
{
	private static RequestQueue s_requestQueue;

	public void onCreate()
	{
		super.onCreate();

		PreferenceManager.setDefaultValues( this, R.xml.general_preferences, false );

		s_requestQueue = Volley.newRequestQueue( this );
	}

	public static RequestQueue getRequestQueue()
	{
		return s_requestQueue;
	}
}
