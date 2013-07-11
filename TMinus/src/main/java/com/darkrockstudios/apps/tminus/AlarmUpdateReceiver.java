package com.darkrockstudios.apps.tminus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by adam on 7/10/13.
 */
public class AlarmUpdateReceiver extends BroadcastReceiver
{
    public void onReceive(Context context, Intent intent)
    {
        UpdateAlarmsService.sendWakefulWork( context, UpdateAlarmsService.class );
    }
}
