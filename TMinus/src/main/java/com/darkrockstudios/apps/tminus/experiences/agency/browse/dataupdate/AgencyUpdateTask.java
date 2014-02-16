package com.darkrockstudios.apps.tminus.experiences.agency.browse.dataupdate;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.AgencyType;
import com.darkrockstudios.apps.tminus.dataupdate.UpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryGson;
import com.darkrockstudios.apps.tminus.launchlibrary.LaunchLibraryUrls;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.google.gson.Gson;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;

/**
 * Created by Adam on 2/13/14.
 */
public class AgencyUpdateTask extends UpdateTask
{
	private static final String TAG         = AgencyUpdateTask.class.getSimpleName();
	public static final  String UPDATE_TYPE = "agencies";

	public static final String ACTION_AGENCY_LIST_UPDATED       =
			AgencyUpdateTask.class.getPackage() + ".ACTION_AGENCY_LIST_UPDATED";
	public static final String ACTION_AGENCY_LIST_UPDATE_FAILED =
			AgencyUpdateTask.class.getPackage() + ".ACTION_AGENCY_LIST_UPDATE_FAILED";

	public AgencyUpdateTask( final Context context )
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
					final Gson gson = LaunchLibraryGson.create();

					updateTypes( response, databaseHelper, gson );

					updateAgencies( response, databaseHelper, gson );

					final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( getContext() );
					preferences.edit().putLong( Preferences.KEY_LAST_AGENCY_LIST_UPDATE, DateTime.now().getMillis() )
					           .commit();

					success = true;
				}
				catch( SQLException | JSONException e )
				{
					e.printStackTrace();
				}
			}
		}

		return success;
	}

	private void updateTypes( final JSONObject response, final DatabaseHelper databaseHelper, final Gson gson ) throws SQLException, JSONException
	{
		final Dao<AgencyType, Integer> agencyTypeDao = databaseHelper.getDao( AgencyType.class );
		JSONArray types = response.getJSONArray( "types" );
		if( types != null && types.length() > 0 )
		{
			final int n = types.length();
			for( int ii = 0; ii < n; ++ii )
			{
				final AgencyType agencyType = gson.fromJson( types.get( ii ).toString(), AgencyType.class );
				agencyTypeDao.createOrUpdate( agencyType );
			}

			Log.i( TAG, "Agency Types after update: " + agencyTypeDao.countOf() );
		}
	}

	private void updateAgencies( final JSONObject response, final DatabaseHelper databaseHelper, final Gson gson ) throws SQLException, JSONException
	{
		JSONArray agencies = response.getJSONArray( "agencies" );
		if( agencies != null && agencies.length() > 0 )
		{
			final Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );

			final int n = agencies.length();
			for( int ii = 0; ii < n; ++ii )
			{
				final Agency agency = gson.fromJson( agencies.get( ii ).toString(), Agency.class );
				// Gotta clean this entry
				agency.name = Html.fromHtml( agency.name ).toString();
				agencyDao.createOrUpdate( agency );
			}

			Log.i( TAG, "Agencies after update: " + agencyDao.countOf() );
		}
	}

	@Override
	public String getRequestUrl()
	{
		return LaunchLibraryUrls.agencies();
	}

	@Override
	public String getSuccessIntentAction()
	{
		return ACTION_AGENCY_LIST_UPDATED;
	}

	@Override
	public String getFailureIntentAction()
	{
		return ACTION_AGENCY_LIST_UPDATE_FAILED;
	}
}
