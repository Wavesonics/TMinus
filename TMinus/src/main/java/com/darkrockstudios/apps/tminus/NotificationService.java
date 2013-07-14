package com.darkrockstudios.apps.tminus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

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
		NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
		builder.setContentTitle( getString( R.string.NOTIFICATION_reminder_title ) );
		builder.setContentText( launch.name + launch.net.toString() );
		builder.setSmallIcon( R.drawable.ic_stat_rocket );
		builder.setAutoCancel( true );

		Intent launchDetailIntent = new Intent( this, LaunchDetailActivity.class );
		launchDetailIntent.putExtra( LaunchDetailFragment.ARG_ITEM_ID, launch.id );

		PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, launchDetailIntent, 0 );
		builder.setContentIntent( pendingIntent );

		Notification notification = builder.build();
		NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

		notificationManager.notify( NOTIFICATION_TAG_REMINDER, launch.id, notification );
	}

	private void postLaunchImminentNotification( Launch launch )
	{
		NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
		builder.setContentTitle( getString( R.string.NOTIFICATION_launch_imminent_title ) );
		builder.setContentText( launch.name + launch.net.toString() );
		builder.setSmallIcon( R.drawable.ic_stat_rocket );
		builder.setDefaults( Notification.DEFAULT_ALL );
		builder.setPriority( Notification.PRIORITY_HIGH );
		builder.setAutoCancel( true );

		Intent launchDetailIntent = new Intent( this, CountDownActivity.class );
		launchDetailIntent.putExtra( CountDownActivity.ARG_ITEM_ID, launch.id );

		PendingIntent pendingIntent = PendingIntent.getActivity( this, 0, launchDetailIntent, 0 );
		builder.setContentIntent( pendingIntent );

		Notification notification = builder.build();
		NotificationManager notificationManager = (NotificationManager)getSystemService( Context.NOTIFICATION_SERVICE );

		notificationManager.notify( NOTIFICATION_TAG_LAUNCH_IMMINENT, launch.id, notification );
	}
}
