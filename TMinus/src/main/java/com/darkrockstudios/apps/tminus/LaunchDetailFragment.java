package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R.id;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * A fragment representing a single Launch detail screen.
 * This fragment is either contained in a {@link LaunchListActivity}
 * in two-pane mode (on tablets) or a {@link LaunchDetailActivity}
 * on handsets.
 */
public class LaunchDetailFragment extends Fragment
{
	public static final String TAG = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";
	private ShareActionProvider m_shareActionProvider;
	private Launch              m_launchItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LaunchDetailFragment()
	{
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setHasOptionsMenu( true );

		loadLaunch();
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		return inflater.inflate( R.layout.fragment_launch_detail, container, false );
	}

	@Override
	public void onCreateOptionsMenu( Menu menu, MenuInflater inflater )
	{
		inflater.inflate( R.menu.launch_detail, menu );

		MenuItem item = menu.findItem( R.id.menu_item_share );
		if( item != null )
		{
			m_shareActionProvider = (ShareActionProvider)item.getActionProvider();
		}
		updateShareIntent();

		super.onCreateOptionsMenu( menu, inflater );
	}

	private void updateViews()
	{
		if( m_launchItem != null )
		{
			final View rootView = getView();

			final TextView name = (TextView)rootView.findViewById( id.LAUNCHDETAIL_mission_name );
			name.setText( m_launchItem.name );

			final TextView description = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_mission_description );
			description.setText( m_launchItem.mission.description );

			final TextView launchWindow = (TextView)rootView.findViewById( id.LAUNCHDETAIL_launch_window );
			launchWindow.setText( m_launchItem.windowstart );

			final TextView location = (TextView)rootView.findViewById( id.LAUNCHDETAIL_location );
			location.setText( m_launchItem.location.name );
		}
	}

	public void loadLaunch()
	{
		if( getArguments().containsKey( ARG_ITEM_ID ) )
		{
			int launchId = getArguments().getInt( ARG_ITEM_ID );

			LaunchLoader loader = new LaunchLoader();
			loader.execute( launchId );
		}
	}

	private void updateShareIntent()
	{
		if( m_launchItem != null && m_shareActionProvider != null )
		{
			Intent intent = new Intent( android.content.Intent.ACTION_SEND );
			intent.setType( "text/plain" );
			intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET );

			// Add data to the intent, the receiving app will decide what to do with it.
			intent.putExtra( Intent.EXTRA_SUBJECT, m_launchItem.name );
			intent.putExtra( Intent.EXTRA_TEXT, m_launchItem.mission.description );

			m_shareActionProvider.setShareIntent( intent );
		}
	}

	private class LaunchLoader extends AsyncTask<Integer, Void, Launch>
	{
		@Override
		protected Launch doInBackground( Integer... ids )
		{
			Launch launch = null;

			final Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
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
			m_launchItem = result;
			updateViews();
			updateShareIntent();

			Log.d( TAG, "Launch details loaded." );
		}
	}
}
