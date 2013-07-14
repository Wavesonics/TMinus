package com.darkrockstudios.apps.tminus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * Created by adam on 7/10/13.
 */
public class UpdateAlarmsService extends WakefulIntentService
{
	private static final String TAG = UpdateAlarmsService.class.getSimpleName();

	public UpdateAlarmsService()
	{
		super( "UpdateAlarmsService" );
	}

	@Override
	protected void doWakefulWork( Intent intent )
	{
		Log.i( TAG, "doWakefulWork" );

		final AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE );

		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				final Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
				final QueryBuilder<Launch, Integer> queryBuilder = launchDao.queryBuilder();
				final PreparedQuery<Launch> query = queryBuilder.orderBy( "net", true ).prepare();

				final Date cutOffDate = new Date( new Date().getTime() + TimeUnit.DAYS.toMillis( 10 ) );

				final List<Launch> results = launchDao.query( query );
				for( Launch launch : results )
				{
					if( launch.net.before( cutOffDate ) )
					{
						setReminderAlarm( launch, alarmManager );
						setImminentLaunchAlarm( launch, alarmManager );

						Log.d( TAG, "Setting alarms for Launch id: " + launch.id );
					}
					else
					{
						Log.d( TAG, "No more alarms to set!" );
						break;
					}
				}
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}

			OpenHelperManager.releaseHelper();
		}
	}

	private void setReminderAlarm( Launch launch, AlarmManager alarmManager )
	{
		Intent serviceIntent = new Intent( this, NotificationService.class );
		serviceIntent.putExtra( NotificationService.EXTRA_LAUNCH_ID, launch.id );
		serviceIntent
				.putExtra( NotificationService.EXTRA_NOTIFICATION_TYPE, NotificationService.EXTRA_NOTIFICATION_TYPE_REMINDER );

		PendingIntent pendingIntent = PendingIntent
				                              .getService( this, getUniqueRequestCode( launch, NotificationService.EXTRA_NOTIFICATION_TYPE_REMINDER ), serviceIntent, 0 );

		long dayBefore = launch.net.getTime() - TimeUnit.DAYS.toMillis( 1 );
		alarmManager.set( AlarmManager.RTC, dayBefore, pendingIntent );
	}

	private void setImminentLaunchAlarm( Launch launch, AlarmManager alarmManager )
	{
		Intent serviceIntent = new Intent( this, NotificationService.class );
		serviceIntent.putExtra( NotificationService.EXTRA_LAUNCH_ID, launch.id );
		serviceIntent
				.putExtra( NotificationService.EXTRA_NOTIFICATION_TYPE, NotificationService.EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT );

		PendingIntent pendingIntent = PendingIntent
				                              .getService( this, getUniqueRequestCode( launch, NotificationService.EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT ), serviceIntent, 0 );

		long tiggerTime = launch.net.getTime() - TimeUnit.MINUTES.toMillis( 10 );
		alarmManager.set( AlarmManager.RTC_WAKEUP, tiggerTime, pendingIntent );
	}

	public static int getUniqueRequestCode( Launch launch, int notificationType )
	{
		return launch.id * 10 + notificationType;
	}

	public static void cancelAlarmsForLaunch( Launch launch, Context context )
	{
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );

		final Intent serviceIntent = new Intent( context, NotificationService.class );

		final PendingIntent pendingIntentReminder = PendingIntent
				                                            .getService( context, getUniqueRequestCode( launch, NotificationService.EXTRA_NOTIFICATION_TYPE_REMINDER ), serviceIntent, 0 );
		final PendingIntent pendingIntentLaunchImminent = PendingIntent
				                                                  .getService( context, getUniqueRequestCode( launch, NotificationService.EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT ), serviceIntent, 0 );

		alarmManager.cancel( pendingIntentReminder );
		alarmManager.cancel( pendingIntentLaunchImminent );
	}
}
