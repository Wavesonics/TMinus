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
    public static final String TAG = NotificationService.class.getSimpleName();
    public static final String EXTRA_LAUNCH_ID = "launch_id";
    public static final String NOTIFICATION_TAG_LAUNCH = "launch";

    public NotificationService()
    {
        super( "NotificationService" );
    }

    @Override
    protected void doWakefulWork(Intent intent)
    {
        Log.d( TAG, "Lets post a notification!" );

        final int launchId = intent.getIntExtra( EXTRA_LAUNCH_ID, -1 );
        if( launchId >= 0 )
        {
            final DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
            if( databaseHelper != null )
            {
                try
                {
                    Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
                    Launch launch = launchDao.queryForId( launchId );

                    NotificationCompat.Builder builder = new NotificationCompat.Builder( this );
                    builder.setContentTitle( getString(R.string.NOTIFICATION_launch_title) );
                    builder.setContentText(launch.name + "\n" + launch.net.toString());
                    builder.setSmallIcon(R.drawable.stat_launch);
                    builder.setAutoCancel(true);

                    Intent launchDetailIntent = new Intent( this, LaunchDetailActivity.class );
                    launchDetailIntent.putExtra( LaunchDetailFragment.ARG_ITEM_ID, launchId );

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchDetailIntent, 0);
                    builder.setContentIntent( pendingIntent );

                    Notification notification = builder.build();

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify( NOTIFICATION_TAG_LAUNCH, launchId, notification );

                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}