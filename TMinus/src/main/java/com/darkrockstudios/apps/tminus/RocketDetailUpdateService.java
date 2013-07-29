package com.darkrockstudios.apps.tminus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Adam on 7/28/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailUpdateService extends Service
{
	private static final String TAG = RocketDetailUpdateService.class.getSimpleName();

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d( TAG, "RocketDetailUpdateService started." );

		requestRocketDetails();

		return START_STICKY;
	}

	public IBinder onBind( Intent intent )
	{
		return null;
	}

	private void requestRocketDetails()
	{

	}
}
