package com.darkrockstudios.apps.tminus.updatetasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
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
 * Created by Adam on 10/23/13.
 */
public class RocketUpdateTask extends UpdateTask
{
	private static final String TAG = RocketUpdateTask.class.getSimpleName();

	public static final String UPDATE_TYPE = "rockets";

	public static final String ACTION_ROCKET_LIST_UPDATED       =
			RocketUpdateTask.class.getPackage() + ".ACTION_ROCKET_LIST_UPDATED";
	public static final String ACTION_ROCKET_LIST_UPDATE_FAILED =
			RocketUpdateTask.class.getPackage() + ".ACTION_ROCKET_LIST_UPDATE_FAILED";

	public RocketUpdateTask( Context context )
	{
		super( context );
	}

	@Override
	public boolean handleData( JSONObject response )
	{
		boolean success = false;

		if( response != null )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( getContext(), DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					final Dao<Rocket, Integer> rocketDao = databaseHelper.getDao( Rocket.class );

					JSONArray rockets = response.getJSONArray( "rockets" );
					if( rockets != null && rockets.length() > 0 )
					{
						final Gson gson = LaunchLibraryGson.create();

						final int n = rockets.length();
						for( int ii = 0; ii < n; ++ii )
						{
							final Rocket rocket = gson.fromJson( rockets.get( ii ).toString(), Rocket.class );
							rocketDao.createOrUpdate( rocket );
						}

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
						preferences.edit().putLong( Preferences.KEY_LAST_ROCKET_LIST_UPDATE, new Date().getTime() ).commit();

						Log.i( TAG, "Rockets after update: " + rocketDao.countOf() );

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
	public String getRequestUrl()
	{
		return LaunchLibraryUrls.rockets();
	}

	@Override
	public String getSuccessIntentAction()
	{
		return ACTION_ROCKET_LIST_UPDATED;
	}

	@Override
	public String getFailureIntentAction()
	{
		return ACTION_ROCKET_LIST_UPDATE_FAILED;
	}
}
