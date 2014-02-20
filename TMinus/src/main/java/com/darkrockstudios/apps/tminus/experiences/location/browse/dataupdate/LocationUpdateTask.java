package com.darkrockstudios.apps.tminus.experiences.location.browse.dataupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.DatabaseUtilities;
import com.darkrockstudios.apps.tminus.dataupdate.UpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by Adam on 10/24/13.
 */
public class LocationUpdateTask extends UpdateTask
{
	private static final String TAG = LocationUpdateTask.class.getSimpleName();

	public static final String UPDATE_TYPE = "locations";

	public static final String ACTION_LOCATION_LIST_UPDATED       =
			LocationUpdateTask.class.getPackage() + ".ACTION_LOCATION_LIST_UPDATED";
	public static final String ACTION_LOCATION_LIST_UPDATE_FAILED =
			LocationUpdateTask.class.getPackage() + ".ACTION_LOCATION_LIST_UPDATE_FAILED";

	public LocationUpdateTask( final Context context )
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
					JSONArray locations = response.getJSONArray( "locations" );
					if( locations != null && locations.length() > 0 )
					{
						final Gson gson = LaunchLibraryGson.create();

						final int n = locations.length();
						for( int ii = 0; ii < n; ++ii )
						{
							JSONObject locationObj = locations.getJSONObject( ii );

							final Location location = gson.fromJson( locationObj.toString(), Location.class );
							DatabaseUtilities.saveLocation( location, databaseHelper );
						}

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
						preferences.edit().putLong( Preferences.KEY_LAST_LOCATION_LIST_UPDATE, new Date().getTime() ).commit();

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
		return LaunchLibraryUrls.locations();
	}

	@Override
	public String getSuccessIntentAction()
	{
		return ACTION_LOCATION_LIST_UPDATED;
	}

	@Override
	public String getFailureIntentAction()
	{
		return ACTION_LOCATION_LIST_UPDATE_FAILED;
	}
}
