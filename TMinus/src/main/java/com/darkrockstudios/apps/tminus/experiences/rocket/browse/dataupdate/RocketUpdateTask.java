package com.darkrockstudios.apps.tminus.experiences.rocket.browse.dataupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.DatabaseUtilities;
import com.darkrockstudios.apps.tminus.dataupdate.UpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

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

	public RocketUpdateTask( final Context context )
	{
		super( context );
	}

	@Override
	public boolean handleData( final JSONObject response )
	{
		boolean success = false;

		if( response != null )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( getContext(), DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					JSONArray rockets = response.getJSONArray( "rockets" );
					if( rockets != null && rockets.length() > 0 )
					{
						final Gson gson = LaunchLibraryGson.create();

						final int n = rockets.length();
						for( int ii = 0; ii < n; ++ii )
						{
							final Rocket rocket = gson.fromJson( rockets.get( ii ).toString(), Rocket.class );

							DatabaseUtilities.saveRocket( rocket, databaseHelper );
						}

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
						preferences.edit().putLong( Preferences.KEY_LAST_ROCKET_LIST_UPDATE, DateTime.now().getMillis() )
						           .commit();

						success = true;
					}
				}
				catch( final JSONException e )
				{
					e.printStackTrace();
				}
				catch( final SQLException e )
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
