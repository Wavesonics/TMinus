package com.darkrockstudios.apps.tminus.loaders;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.RocketDetail;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by adam on 7/21/13.
 */
public class RocketDetailLoader extends AsyncTask<Integer, Void, RocketDetail>
{
    public static interface Listener
    {
        public void rocketDetailLoaded( RocketDetail rocketDetail );
        public void rocketDetailImageMissing();
        public void rocketDetailSummaryMissing();
    }

    private static final String TAG = RocketDetailLoader.class.getSimpleName();

    private Context m_context;
    private RocketDetailLoader.Listener m_listener;

    public RocketDetailLoader( Context context, RocketDetailLoader.Listener listener )
    {
        m_context = context;
        m_listener = listener;
    }

    @Override
    protected RocketDetail doInBackground( Integer[] ids )
    {
        RocketDetail rocketDetail = null;

        if( m_context != null )
        {
            final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( m_context, DatabaseHelper.class );
            if( databaseHelper != null )
            {
                try
                {
                    Dao<RocketDetail, Integer> rocketDetailDao = databaseHelper.getRocketDetailDao();
                    rocketDetail = rocketDetailDao.queryForId( ids[ 0 ] );

                }
                catch( SQLException e )
                {
                    e.printStackTrace();
                }

                OpenHelperManager.releaseHelper();
            }
        }

        return rocketDetail;
    }

    @Override
    protected void onPostExecute( RocketDetail result )
    {
        Log.d(TAG, "RocketDetail loaded.");
        m_listener.rocketDetailLoaded(result);
    }
}
