package com.darkrockstudios.apps.tminus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.DeleteBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Adam on 7/13/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LaunchUpdateService extends Service
{
	public static final  String ACTION_LAUNCH_LIST_UPDATED       = "com.darkrockstudios.apps.tminus.ACTION_LAUNCH_LIST_UPDATED";
	public static final  String ACTION_LAUNCH_LIST_UPDATE_FAILED =
			"com.darkrockstudios.apps.tminus.ACTION_LAUNCH_LIST_UPDATE_FAILED";
	private static final String TAG                              = LaunchUpdateService.class.getSimpleName();
	private PowerManager.WakeLock m_wakeLock;

	public LaunchUpdateService()
	{
		super();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();

		Log.d( TAG, "LaunchUpdateService created." );

		final PowerManager powerManager = (PowerManager) getSystemService( Context.POWER_SERVICE );
		m_wakeLock = powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, TAG );
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		Log.d( TAG, "LaunchUpdateService destroyed." );

		if( m_wakeLock.isHeld() )
		{
			m_wakeLock.release();
		}
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d( TAG, "LaunchUpdateService started." );

		requestLaunches();

		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent )
	{
		return null;
	}

	private void requestLaunches()
	{
		Log.d( TAG, "Requesting Launches..." );
		if( !m_wakeLock.isHeld() )
		{
			m_wakeLock.acquire();

			Log.d( TAG, "WakeLock acquired." );
		}

		final String url = LaunchLibraryUrls.next( 20 );

		LaunchListResponseListener listener = new LaunchListResponseListener();
		JsonObjectRequest request = new JsonObjectRequest( url, null, listener, listener );
		request.setTag( this );
		TMinusApplication.getRequestQueue().add( request );
	}

	private void stopService()
	{
		TMinusApplication.getRequestQueue().cancelAll( this );
		if( m_wakeLock.isHeld() )
		{
			m_wakeLock.release();
		}
		stopSelf();
	}

	private void sendSuccessBroadcast()
	{
		Log.i( TAG, "Launches successfully updates, sending success broadcast." );
		final Intent intent = new Intent( ACTION_LAUNCH_LIST_UPDATED );
		sendBroadcast( intent );
	}

	private void sendFailureBroadcast()
	{
		Log.i( TAG, "Launches update failed, sending failure broadcast." );
		final Intent intent = new Intent( ACTION_LAUNCH_LIST_UPDATE_FAILED );
		sendBroadcast( intent );
	}

	private class LaunchListResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener
	{
		@Override
		public void onResponse( JSONObject response )
		{
			Log.i( TAG, "Launches successfully retrieved from sever." );
			LaunchListSaver loader = new LaunchListSaver();
			loader.execute( response );
		}

		@Override
		public void onErrorResponse( VolleyError error )
		{
			Log.i( TAG, "Failed to retrieve Launches from sever." );
			Log.i( TAG, error.getMessage() );
			sendFailureBroadcast();

			stopService();
		}
	}

	private class LaunchListSaver extends AsyncTask<JSONObject, Void, Long>
	{
		@Override
		protected Long doInBackground( JSONObject... response )
		{
			Log.d( TAG, "Beginning background processing of new Launches..." );

			long numLaunches = 0;

			final Gson gson = LaunchLibraryGson.create();

			final JSONObject launchListObj = response[ 0 ];

			final DatabaseHelper databaseHelper = OpenHelperManager
					                                      .getHelper( LaunchUpdateService.this, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					final Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
					final Dao<Location, Integer> locationDao = databaseHelper.getLocationDao();
					final Dao<Pad, Integer> padDao = databaseHelper.getPadDao();
					final Dao<Mission, Integer> missionDao = databaseHelper.getMissionDao();
					final Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();

					int numUpdated = 0;

					final JSONArray launchListArray = launchListObj.getJSONArray( "launch" );
					for( int ii = 0; ii < launchListArray.length(); ++ii )
					{
						try
						{
							final JSONObject launchObj = launchListArray.getJSONObject( ii );
							if( launchObj != null )
							{
								final Launch launch = parseLaunch( launchObj, gson );

								try
								{
									TransactionManager.callInTransaction( databaseHelper.getConnectionSource(),
									                                      new Callable<Void>()
									                                      {
										                                      public Void call() throws Exception
										                                      {
											                                      // If the launch already exists, cancel any alarms for it
											                                      if( launchDao.idExists( launchDao
													                                                              .extractId( launch ) ) )
											                                      {
												                                      UpdateAlarmsService
														                                      .cancelAlarmsForLaunch( launch,
														                                                              LaunchUpdateService.this );
											                                      }

											                                      locationDao
													                                      .createOrUpdate( launch.pad.location );
											                                      padDao
													                                      .createOrUpdate( launch.pad );

											                                      if( launch.mission != null )
											                                      {
												                                      missionDao
														                                      .createOrUpdate( launch.mission );
											                                      }

											                                      rocketDao
													                                      .createOrUpdate( launch.rocket );

											                                      // This must be run after all the others are created so the IDs of the child objects can be set
											                                      launchDao.createOrUpdate( launch );

											                                      return null;
										                                      }
									                                      } );
									++numUpdated;
								}
								catch( SQLException e )
								{
									Log.w( TAG, e.getMessage() );
								}
							}
						}
						catch( JsonSyntaxException e )
						{
							e.printStackTrace();
						}
						catch( JsonParseException e )
						{
							e.printStackTrace();
						}
					}

					Log.d( TAG, "Parsing and database work complete, launching AlarmUpdateService..." );
					// Now that we have new data, ensure our Alarms are set correctly
					startService( new Intent( LaunchUpdateService.this, UpdateAlarmsService.class ) );

					numLaunches = launchDao.countOf();

					final SharedPreferences preferences = PreferenceManager
							                                      .getDefaultSharedPreferences( LaunchUpdateService.this );
					preferences.edit().putLong( Preferences.KEY_LAST_UPDATED, new Date().getTime() ).commit();
					Log.d( TAG, "Refresh successful: " + numLaunches + " Launches in database." );
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}
				catch( JSONException e )
				{
					e.printStackTrace();
				}
				finally
				{
					OpenHelperManager.releaseHelper();
				}
			}

			cleanUpOldLaunches();

			return numLaunches;
		}

		private Launch parseLaunch( JSONObject launchObj, Gson gson ) throws JSONException
		{
			final Launch launch = gson.fromJson( launchObj.toString(), Launch.class );

			JSONObject locationObj = launchObj.getJSONObject( "location" );
			final Location location = gson.fromJson( locationObj.toString(), Location.class );

			JSONObject padObj = locationObj.getJSONObject( "pad" );
			final Pad pad = gson.fromJson( padObj.toString(), Pad.class );
			pad.location = location;

			launch.pad = pad;

			return launch;
		}

		@Override
		protected void onPostExecute( Long result )
		{
			Log.d( TAG, "Background update complete." );
			sendSuccessBroadcast();
			stopService();
		}

		private Date getOldLaunchThreshold()
		{
			final long MAX_DAYS_OLD = 5;

			Date now = new Date();
			Date cutOffDate = new Date( now.getTime() - TimeUnit.DAYS.toMillis( MAX_DAYS_OLD ) );

			return cutOffDate;
		}

		private void cleanUpOldLaunches()
		{
			final DatabaseHelper databaseHelper = OpenHelperManager
					                                      .getHelper( LaunchUpdateService.this, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					final Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();

					DeleteBuilder<Launch, Integer> builder = launchDao.deleteBuilder();
					builder.where().lt( "net", getOldLaunchThreshold() );

					launchDao.delete( builder.prepare() );
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}
				finally
				{
					OpenHelperManager.releaseHelper();
				}
			}
		}
	}
}
