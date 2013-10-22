package com.darkrockstudios.apps.tminus;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by adam on 10/21/13.
 */
public class RocketUpdateService extends Service
{
	private static final String TAG                              = RocketUpdateService.class.getSimpleName();
	public static final  String ACTION_ROCKET_LIST_UPDATED       = "com.darkrockstudios.apps.tminus.ACTION_ROCKET_LIST_UPDATED";
	public static final  String ACTION_ROCKET_LIST_UPDATE_FAILED =
			"com.darkrockstudios.apps.tminus.ACTION_ROCKET_LIST_UPDATE_FAILED";

	private Request m_rocketListRequest;

	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d( TAG, "RocketUpdateService started." );

		requestRockets();

		return START_NOT_STICKY;
	}

	private void requestRockets()
	{
		final String url = "http://launchlibrary.net/ll/json/rockets";

		if( m_rocketListRequest == null )
		{
			RocketListResponseListener listener = new RocketListResponseListener();
			JsonObjectRequest request = new JsonObjectRequest( url, null, listener, listener );
			request.setTag( this );
			m_rocketListRequest = TMinusApplication.getRequestQueue().add( request );
		}
	}

	private void sendSuccessBroadcast()
	{
		Log.i( TAG, "Rockets successfully updates, sending success broadcast." );
		final Intent intent = new Intent( ACTION_ROCKET_LIST_UPDATED );
		sendBroadcast( intent );
	}

	private void sendFailureBroadcast()
	{
		Log.i( TAG, "Rockets update failed, sending failure broadcast." );
		final Intent intent = new Intent( ACTION_ROCKET_LIST_UPDATE_FAILED );
		sendBroadcast( intent );
	}

	private class RocketListResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener
	{
		@Override
		public void onResponse( JSONObject response )
		{
			Log.i( TAG, "Rockets successfully retrieved from sever." );

			saveRocketList( response );
		}

		@Override
		public void onErrorResponse( VolleyError error )
		{
			Log.i( TAG, "Failed to retrieve Rockets from sever." );
			Log.i( TAG, error.getMessage() );

			failure();
		}

		private void saveRocketList( JSONObject response )
		{
			RocketListSaver rocketListSaver = new RocketListSaver();
			rocketListSaver.execute( response );
		}
	}

	private void success()
	{
		m_rocketListRequest = null;
		sendSuccessBroadcast();
		stopSelf();
	}

	private void failure()
	{
		m_rocketListRequest = null;
		sendFailureBroadcast();
		stopSelf();
	}

	private class RocketListSaver extends AsyncTask<JSONObject, Void, Boolean>
	{
		@Override
		protected Boolean doInBackground( JSONObject... responses )
		{
			boolean success = false;

			if( responses != null && responses.length > 0 )
			{
				JSONObject response = responses[0];

				final DatabaseHelper databaseHelper = OpenHelperManager
						                                      .getHelper( RocketUpdateService.this, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						final Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();

						JSONArray rockets = response.getJSONArray( "rocket" );
						if( rockets != null && rockets.length() > 0 )
						{
							final Gson gson = LaunchLibraryGson.create();

							final int n = rockets.length();
							for( int ii=0; ii<n; ++ii )
							{
								final Rocket rocket = gson.fromJson( rockets.get( ii ).toString(), Rocket.class );
								rocketDao.createOrUpdate( rocket );
							}

							final SharedPreferences preferences = PreferenceManager
									                                      .getDefaultSharedPreferences( RocketUpdateService.this );
							preferences.edit().putLong( Preferences.KEY_LAST_ROCKET_LIST_UPDATE, new Date().getTime() ).commit();

							Log.i(TAG, "Rockets after update: " + rocketDao.countOf() );

							success = true;
						}
					}
					catch( JSONException e )
					{
						e.printStackTrace();
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

			return success;
		}

		@Override
		protected void onPostExecute( Boolean success )
		{
			Log.d( TAG, "Background update complete." );
			if( success )
			{
				success();
			}
			else
			{
				failure();
			}
		}
	}
}
