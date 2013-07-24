package com.darkrockstudios.apps.tminus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.darkrockstudios.apps.tminus.R.string;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.misc.Utilities;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by adam on 7/10/13.
 */
public class NotificationService extends WakefulIntentService
{
	public static final String TAG                                     = NotificationService.class.getSimpleName();
	public static final String EXTRA_LAUNCH_ID                         = "launch_id";
	public static final String EXTRA_NOTIFICATION_TYPE                 = "notification_type";
	public static final int    EXTRA_NOTIFICATION_TYPE_REMINDER        = 1;
	public static final int    EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT = 2;
	public static final String NOTIFICATION_TAG_REMINDER               = "launch_reminder";
	public static final String NOTIFICATION_TAG_LAUNCH_IMMINENT        = "imminent_launch";

	public NotificationService()
	{
		super( NotificationService.class.getSimpleName() );
	}

	@Override
	protected void doWakefulWork( Intent intent )
	{
		Log.d( TAG, "Notification alarm:" );

		final int launchId = intent.getIntExtra( EXTRA_LAUNCH_ID, -1 );
		final int notificationType = intent.getIntExtra( EXTRA_NOTIFICATION_TYPE, -1 );
		if( launchId >= 0 && notificationType > 0 )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
					Launch launch = launchDao.queryForId( launchId );

					if( notificationType == EXTRA_NOTIFICATION_TYPE_REMINDER )
					{
						postReminderNotification( launch );
					}
					else if( notificationType == EXTRA_NOTIFICATION_TYPE_LAUNCH_IMMINENT )
					{
						postLaunchImminentNotification( launch );
					}
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}

				OpenHelperManager.releaseHelper();
			}
		}
	}

	private void postReminderNotification( Launch launch )
	{
		final String title = getString( R.string.NOTIFICATION_reminder_title );
		final String shortBody = launch.name + " " + Utilities.getDateText( launch.net );

		NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
		builder.setContentTitle( title );
		builder.setContentText( shortBody );
		builder.setSmallIcon( R.drawable.ic_stat_rocket );
		builder.setAutoCancel( true );

		final String summary = getString( string.NOTIFICATION_reminder_summary, launch.name, Utilities
				                                                                                     .getDateText( launch.net ), launch.location.name );
		builder.setStyle( new NotificationCompat.BigTextStyle()
				                  .bigText( summary ) );

		Intent launchDetailIntent = new Intent( this, LaunchDetailActivity.class );
		launchDetailIntent.putExtra( LaunchDetailFragment.ARG_ITEM_ID, launch.id );

		PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, launchDetailIntent, 0 );
		builder.setContentIntent( pendingIntent );

		Notification notification = builder.build();
		NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

		notificationManager.notify( NOTIFICATION_TAG_REMINDER, launch.id, notification );

		// TODO: Use a setting to control pebble alerts
		if( true )
		{
			sendAlertToPebble( title, shortBody );
		}
	}

	private void postLaunchImminentNotification( Launch launch )
	{
		final String title = getString( R.string.NOTIFICATION_launch_imminent_title );
		final String shortBody = launch.name + " " + Utilities.getDateText( launch.net );

		NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
		builder.setContentTitle( title );
		builder.setContentText( shortBody );
		builder.setSmallIcon( R.drawable.ic_stat_rocket );
		builder.setDefaults( Notification.DEFAULT_ALL );
		builder.setPriority( Notification.PRIORITY_HIGH );
		builder.setAutoCancel( true );

		final String summary = getString( string.NOTIFICATION_launch_imminent_summary, launch.name, Utilities
				                                                                                            .getDateText( launch.net ), launch.location.name );
		builder.setStyle( new NotificationCompat.BigTextStyle().bigText( summary ) );

		Intent launchDetailIntent = new Intent( this, CountDownActivity.class );
		launchDetailIntent.putExtra( CountDownActivity.ARG_ITEM_ID, launch.id );

		PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, launchDetailIntent, 0 );
		builder.setContentIntent( pendingIntent );

		Notification notification = builder.build();
		NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

		notificationManager.notify( NOTIFICATION_TAG_LAUNCH_IMMINENT, launch.id, notification );

		// TODO: Use a setting to control pebble alerts
		if( true )
		{
			sendAlertToPebble( title, shortBody );
		}
	}

	public void sendAlertToPebble( String title, String body )
	{
		final Intent intent = new Intent( "com.getpebble.action.SEND_NOTIFICATION" );

		final String appName = getString( string.app_name );

		final Map<String, String> data = new HashMap<String, String>();
		data.put( "title", title );
		data.put( "body", body );
		final JSONObject jsonData = new JSONObject( data );
		final String notificationData = new JSONArray().put( jsonData ).toString();

		intent.putExtra( "messageType", "PEBBLE_ALERT" );
		intent.putExtra( "sender", appName );
		intent.putExtra( "notificationData", notificationData );

		Log.d( TAG, "About to send a modal alert to Pebble: " + title );
		sendBroadcast( intent );
	}
}
