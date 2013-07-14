package com.darkrockstudios.apps.tminus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by adam on 7/8/13.
 */
class LaunchLoader extends AsyncTask<Integer, Void, Launch>
{
	public static interface Listener
	{
		public void launchLoaded( Launch launch );
	}

	private static final String TAG = LaunchLoader.class.getSimpleName();

	private Context               m_context;
	private LaunchLoader.Listener m_listener;

	public LaunchLoader( Context context, LaunchLoader.Listener listener )
	{
		m_context = context;
		m_listener = listener;
	}

	@Override
	protected Launch doInBackground( Integer... ids )
	{
		Launch launch = null;

		if( m_context != null )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( m_context, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
					launch = launchDao.queryForId( ids[ 0 ] );
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}

				OpenHelperManager.releaseHelper();
			}
		}

		return launch;
	}

	@Override
	protected void onPostExecute( Launch result )
	{
		Log.d( TAG, "Launch details loaded." );
		m_listener.launchLoaded( result );
	}
}