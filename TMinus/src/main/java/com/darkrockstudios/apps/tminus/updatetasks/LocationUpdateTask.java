package com.darkrockstudios.apps.tminus.updatetasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
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

	public LocationUpdateTask( Context context )
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
					final Dao<Location, Integer> locInfoDao = databaseHelper.getLocationDao();
					final Dao<Pad, Integer> locationDao = databaseHelper.getPadDao();

					JSONArray locations = response.getJSONArray( "location" );
					if( locations != null && locations.length() > 0 )
					{
						final Gson gson = LaunchLibraryGson.create();

						final int n = locations.length();
						for( int ii = 0; ii < n; ++ii )
						{
							JSONObject locationObj = locations.getJSONObject( ii );

							final Location location = gson.fromJson( locationObj.toString(), Location.class );
							Dao.CreateOrUpdateStatus status = locInfoDao.createOrUpdate( location );

							if( status.isCreated() || status.isUpdated() )
							{
								JSONArray pads = locationObj.getJSONArray( "pads" );

								if( pads != null )
								{
									for( int xx = 0; xx < pads.length(); ++xx )
									{
										final Pad pad =
												gson.fromJson( pads.getJSONObject( xx ).toString(), Pad.class );
										pad.location = location; // Set it's parent
										locationDao.createOrUpdate( pad );
									}
								}
							}
						}

						final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
						preferences.edit().putLong( Preferences.KEY_LAST_LOCATION_LIST_UPDATE, new Date().getTime() ).commit();

						Log.i( TAG, "Locations after update: " + locationDao.countOf() );

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
		return LaunchLibraryUrls.padList();
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
