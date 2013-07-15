package com.darkrockstudios.apps.tminus;


import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R.string;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 7/14/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailFragment extends DialogFragment
{
	public static final String TAG         = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";

	private Rocket m_rocket;

	private TextView m_rocketName;
	private TextView m_rocketConfiguration;

	public static RocketDetailFragment newInstance( int rocketId )
	{
		RocketDetailFragment rocketDetailFragment = new RocketDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( ARG_ITEM_ID, rocketId );
		rocketDetailFragment.setArguments( arguments );

		return rocketDetailFragment;
	}

	public RocketDetailFragment()
	{
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		Dialog dialog = getDialog();
		if( dialog != null )
		{
			dialog.setTitle( string.ROCKETDETAIL_title );
		}

		View rootView = inflater.inflate( R.layout.fragment_rocket_detail, container, false );

		if( rootView != null )
		{
			//m_contentView = rootView.findViewById( R.id.content_view );
			//m_progressBar = rootView.findViewById( R.id.progressBar );
			m_rocketName = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_name );
			m_rocketConfiguration = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_configuration );

			loadRocket();
		}

		return rootView;
	}

	private void updateViews()
	{
		if( m_rocket != null )
		{
			m_rocketName.setText( m_rocket.name );
			m_rocketConfiguration.setText( m_rocket.configuration );
		}
	}

	private void loadRocket()
	{
		int rocketId = getRocketId();

		if( rocketId >= 0 )
		{
			RocketLoader rocketLoader = new RocketLoader();
			rocketLoader.execute( rocketId );
		}
	}

	public int getRocketId()
	{
		int rocketId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_ITEM_ID ) )
		{
			rocketId = arguments.getInt( ARG_ITEM_ID );
		}

		return rocketId;
	}

	private class RocketLoader extends AsyncTask<Integer, Void, Rocket>
	{
		@Override
		protected Rocket doInBackground( Integer... ids )
		{
			Rocket rocket = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();
						rocket = rocketDao.queryForId( ids[ 0 ] );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return rocket;
		}

		@Override
		protected void onPostExecute( Rocket result )
		{
			Log.d( TAG, "Rocket details loaded." );
			m_rocket = result;

			updateViews();
		}
	}
}
