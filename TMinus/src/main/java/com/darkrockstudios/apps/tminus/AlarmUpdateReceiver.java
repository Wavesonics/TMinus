package com.darkrockstudios.apps.tminus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by adam on 7/10/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class AlarmUpdateReceiver extends BroadcastReceiver
{
	private static final String TAG = AlarmUpdateReceiver.class.getSimpleName();

	public void onReceive( Context context, Intent intent )
	{
		Log.d( TAG, "Updating alarms due to Action: " + intent.getAction() );
		UpdateAlarmsService.sendWakefulWork( context, UpdateAlarmsService.class );
	}
}
