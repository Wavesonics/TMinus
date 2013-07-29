package com.darkrockstudios.apps.tminus.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 7/28/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketLoader extends AsyncTask<Integer, Void, Rocket>
{
	private static final String TAG = RocketLoader.class.getSimpleName();

	public static interface RocketLoadListener
	{
		public void rocketLoaded( Rocket rocket );

		public void rocketLoadFailed( int rocketId );
	}

	private final Context            m_context;
	private final RocketLoadListener m_listener;
	private       int                m_rocketId;

	public RocketLoader( RocketLoadListener listener, Context context )
	{
		m_listener = listener;
		m_context = context;
	}

	@Override
	protected Rocket doInBackground( Integer... ids )
	{
		Rocket rocket = null;

		m_rocketId = ids[ 0 ];

		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( m_context, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();
				rocket = rocketDao.queryForId( m_rocketId );
			}
			catch( SQLException e )
			{
				e.printStackTrace();
			}

			OpenHelperManager.releaseHelper();
		}

		return rocket;
	}

	@Override
	protected void onPostExecute( Rocket result )
	{
		if( m_listener != null )
		{
			if( result != null )
			{
				Log.i( TAG, "Rocket loaded: " + result.name );
				m_listener.rocketLoaded( result );
			}
			else
			{
				Log.w( TAG, "Rocket failed to load: " + m_rocketId );
				m_listener.rocketLoadFailed( m_rocketId );
			}
		}
	}
}