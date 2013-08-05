package com.darkrockstudios.apps.tminus;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
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
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class UpdateAlarmsService extends WakefulIntentService
{
	private static final String TAG = UpdateAlarmsService.class.getSimpleName();

	public UpdateAlarmsService()
	{
		super( UpdateAlarmsService.class.getSimpleName() );
	}

	public static void cancelAlarmsForLaunch( Launch launch, Context context )
	{
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );

		final Intent reminderIntent = new Intent( context, NotificationService.class );
		reminderIntent.setData( TminusUri.buildLaunchReminderNotification( launch.id ) );

		final Intent imminentIntent = new Intent( context, NotificationService.class );
		reminderIntent.setData( TminusUri.buildLaunchImminentNotification( launch.id ) );

		final PendingIntent pendingIntentReminder = PendingIntent
				                                            .getService( context, 0, reminderIntent, 0 );
		final PendingIntent pendingIntentLaunchImminent = PendingIntent
				                                                  .getService( context, 0, imminentIntent, 0 );

		alarmManager.cancel( pendingIntentReminder );
		alarmManager.cancel( pendingIntentLaunchImminent );
	}

	public static void cancelAutoUpdateAlarm( Context context )
	{
		final AlarmManager alarmManager = (AlarmManager)context.getSystemService( Context.ALARM_SERVICE );
		alarmManager.cancel( createLaunchUpdateIntent( context ) );
	}

	@Override
	protected void doWakefulWork( Intent intent )
	{
		final AlarmManager alarmManager = (AlarmManager)getSystemService( Context.ALARM_SERVICE );

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( this );
		final boolean showReminderNotifications = preferences.getBoolean( Preferences.KEY_REMINDER_NOTIFICATION, true );
		final boolean showImminentLaunchNotifications = preferences
				                                                .getBoolean( Preferences.KEY_IMMINENT_LAUNCH_NOTIFICATION, true );

		final boolean automaticUpdating = preferences.getBoolean( Preferences.KEY_AUTOMATIC_UPDATING, true );
		if( automaticUpdating )
		{
			long updateFrequency = Long.parseLong( preferences
					                                       .getString( Preferences.KEY_AUTOMATIC_UPDATING_FREQUENCY, "24" ) );
			updateFrequency = TimeUnit.HOURS.toMillis( updateFrequency );
			setLaunchUpdateAlarm( alarmManager, updateFrequency );
		}

		Log.i( TAG, "Updating Alarms for upcoming Launches..." );
		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				final Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
				final QueryBuilder<Launch, Integer> queryBuilder = launchDao.queryBuilder();
				final PreparedQuery<Launch> query = queryBuilder.orderBy( "net", true ).prepare();

				final Date cutOffDate = new Date( new Date().getTime() + TimeUnit.DAYS.toMillis( 2 ) );

				final List<Launch> results = launchDao.query( query );
				for( final Launch launch : results )
				{
					if( launch.net.before( cutOffDate ) )
					{
						Log.d( TAG, "Setting alarms for Launch id: " + launch.id );
						if( showReminderNotifications )
						{
							setReminderAlarm( launch, alarmManager );
						}

						if( showImminentLaunchNotifications )
						{
							setImminentLaunchAlarm( launch, alarmManager );
						}
					}
					else
					{
						Log.d( TAG, "No more alarms to set." );
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

	private static PendingIntent createLaunchUpdateIntent( Context context )
	{
		final Intent serviceIntent = new Intent( context, LaunchUpdateService.class );
		return PendingIntent.getService( context, 0, serviceIntent, PendingIntent.FLAG_UPDATE_CURRENT );
	}

	private void setLaunchUpdateAlarm( AlarmManager alarmManager, long updateFrequency )
	{
		Log.d( TAG, "Setting the auto-update alarm for Launches every " + TimeUnit.MILLISECONDS
		                                                                          .toHours( updateFrequency ) + " hours" );

		final long now = new Date().getTime();
		alarmManager
				.setInexactRepeating( AlarmManager.RTC, now + updateFrequency, updateFrequency, createLaunchUpdateIntent( this ) );
	}

	private void setReminderAlarm( Launch launch, AlarmManager alarmManager )
	{
		Intent serviceIntent = new Intent( this, NotificationService.class );
		serviceIntent.setData( TminusUri.buildLaunchReminderNotification( launch.id ) );
		serviceIntent.putExtra( NotificationService.EXTRA_LAUNCH_ID, launch.id );

		serviceIntent
				.putExtra( NotificationService.EXTRA_NOTIFICATION_TYPE, NotificationService.EXTRA_NOTIFICATION_TYPE_REMINDER );

		PendingIntent pendingIntent = PendingIntent
				                              .getService( this, 0, serviceIntent, 0 );

		final long dayBefore = launch.net.getTime() - TimeUnit.DAYS.toMillis( 1 );
		// Don't set alarms for the past
		if( new Date( dayBefore ).after( new Date() ) )
		{
			alarmManager.set( AlarmManager.RTC, dayBefore, pendingIntent );
		}
		else
		{
			Log.d( TAG, "Not setting reminder alarm for launch id " + launch.id + " because it has already passed." );
		}
	}

	private void setImminentLaunchAlarm( Launch launch, AlarmManager alarmManager )
	{
		Intent serviceIntent = new Intent( this, NotificationService.class );
		serviceIntent.setData( TminusUri.buildLaunchImminentNotification( launch.id ) );
		serviceIntent.putExtra( NotificationService.EXTRA_LAUNCH_ID, launch.id );
		serviceIntent
				.putExtra( NotificationService.EXTRA_NOTIFICATION_TYPE, NotificationService.EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT );

		PendingIntent pendingIntent = PendingIntent
				                              .getService( this, 0, serviceIntent, 0 );

		long triggerTime = launch.net.getTime() - TimeUnit.MINUTES.toMillis( 10 );
		if( new Date( triggerTime ).after( new Date() ) )
		{
			alarmManager.set( AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent );
		}
		else
		{
			Log.d( TAG, "Not setting imminent launch alarm for launch id " + launch.id + " because it has already passed." );
		}
	}
}
