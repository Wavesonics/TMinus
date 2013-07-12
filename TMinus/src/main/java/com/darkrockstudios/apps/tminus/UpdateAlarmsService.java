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
        super("UpdateAlarmsService");
    }

    @Override
    protected void doWakefulWork(Intent intent)
    {
        Log.i( TAG, "doWakefulWork" );

        final AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        final DatabaseHelper databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        if( databaseHelper != null )
        {
            try
            {
                Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
                QueryBuilder<Launch, Integer> queryBuilder = launchDao.queryBuilder();
                PreparedQuery<Launch> query = queryBuilder.orderBy("net", true).prepare();

                final Date cutOffDate = new Date( new Date().getTime() + TimeUnit.DAYS.toMillis( 10 ) );

                List<Launch> results = launchDao.query(query);
                for( Launch launch : results )
                {
                    if( launch.net.before( cutOffDate ) )
                    {
                        Intent serviceIntent = new Intent( this, NotificationService.class );
                        serviceIntent.putExtra( NotificationService.EXTRA_LAUNCH_ID, launch.id );

                        PendingIntent pendingIntent = PendingIntent.getService( this, 0, serviceIntent, 0 );

                        //alarmManager.set( AlarmManager.RTC_WAKEUP, launch.net.getTime(), pendingIntent );
                        alarmManager.set( AlarmManager.RTC_WAKEUP, new Date().getTime() + 5000, pendingIntent );

                        Log.d( TAG, "Setting alarm for Launch id: " + launch.id );
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
}
