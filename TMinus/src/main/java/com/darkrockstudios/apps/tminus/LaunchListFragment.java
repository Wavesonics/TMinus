package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Date;

/**
 * A list fragment representing a list of Launches. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link com.darkrockstudios.apps.tminus.LaunchDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LaunchListFragment extends ListFragment
{
	private static final String    TAG                      = LaunchListFragment.class.getSimpleName();
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String    STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static       Callbacks s_dummyCallbacks         = new Callbacks()
	{
		@Override
		public void onItemSelected( Launch launch )
		{
		}
	};
	private static final long UPDATE_THRESHOLD_MS = 1 * 60 * 60 * 1000;
	private ArrayAdapter<Launch> m_adapter;
	private Callbacks m_callbacks         = s_dummyCallbacks;
	private int       m_activatedPosition = ListView.INVALID_POSITION;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LaunchListFragment()
	{
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_adapter = new ArrayAdapter<Launch>(
				                                    getActivity(),
				                                    android.R.layout.simple_list_item_activated_1,
				                                    android.R.id.text1 );

		setListAdapter( m_adapter );

		if( !reloadData() || shouldRefresh() )
		{
			refresh();
		}
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		// Restore the previously serialized activated item position.
		if( savedInstanceState != null
				    && savedInstanceState.containsKey( STATE_ACTIVATED_POSITION ) )
		{
			setActivatedPosition( savedInstanceState.getInt( STATE_ACTIVATED_POSITION ) );
		}
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		// Activities containing this fragment must implement its callbacks.
		if( !(activity instanceof Callbacks) )
		{
			throw new IllegalStateException( "Activity must implement fragment's callbacks." );
		}
		else
		{
			m_callbacks = (Callbacks)activity;
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		m_callbacks = s_dummyCallbacks;
	}

	@Override
	public void onStop()
	{
		super.onStop();

		TMinusApplication.getRequestQueue().cancelAll( this );
	}

	@Override
	public void onListItemClick( ListView listView, View view, int position, long id )
	{
		super.onListItemClick( listView, view, position, id );

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Launch launch = (Launch)listView.getAdapter().getItem( position );
		m_callbacks.onItemSelected( launch );
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		if( m_activatedPosition != ListView.INVALID_POSITION )
		{
			// Serialize and persist the activated item position.
			outState.putInt( STATE_ACTIVATED_POSITION, m_activatedPosition );
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick( boolean activateOnItemClick )
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode( activateOnItemClick
		                             ? ListView.CHOICE_MODE_SINGLE
		                             : ListView.CHOICE_MODE_NONE );
	}

	private void setActivatedPosition( int position )
	{
		if( position == ListView.INVALID_POSITION )
		{
			getListView().setItemChecked( m_activatedPosition, false );
		}
		else
		{
			getListView().setItemChecked( position, true );
		}

		m_activatedPosition = position;
	}

	private boolean reloadData()
	{
		boolean dataLoaded = false;

		final Activity activity = getActivity();
		if( activity != null )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				m_adapter.clear();

				try
				{
					Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();

					if( launchDao.countOf() > 0 )
					{
						for( Launch launch : launchDao )
						{
							m_adapter.add( launch );
						}

						dataLoaded = true;
					}
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}

				OpenHelperManager.releaseHelper();
			}
		}

		return dataLoaded;
	}

	private void requestLaunches()
	{
		Log.d( TAG, "Requesting launches..." );
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			activity.setProgressBarIndeterminateVisibility( true );
		}

		final String url = "http://launchlibrary.net/ll/json/next/10";

		LaunchListResponseListener listener = new LaunchListResponseListener();
		JsonObjectRequest request = new JsonObjectRequest( url, null, listener, listener );
		request.setTag( this );
		TMinusApplication.getRequestQueue().add( request );
	}

	private boolean shouldRefresh()
	{
		boolean refresh = false;

		final Activity activity = getActivity();
		if( activity != null )
		{
			final SharedPreferences preferences = activity.getPreferences( Context.MODE_PRIVATE );
			final long lastUpdatedMs = preferences.getLong( Preferences.KEY_LAST_UPDATED, 0 );
			final long nowMs = new Date().getTime();
			final long deltaMs = nowMs - lastUpdatedMs;

			if( deltaMs > UPDATE_THRESHOLD_MS )
			{
				refresh = true;
				Log.i( TAG, "Data is a bit old, we should refresh it." );
			}
			else
			{
				Log.d( TAG, "It's been " + deltaMs / 1000 + " seconds since our last update, no need to refresh." );
			}
		}

		return refresh;
	}

	public void refresh()
	{
		requestLaunches();
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks
	{
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected( Launch launch );
	}

	private class LaunchListResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener
	{
		@Override
		public void onResponse( JSONObject response )
		{
			LaunchListLoader loader = new LaunchListLoader();
			loader.execute( response );
		}

		@Override
		public void onErrorResponse( VolleyError error )
		{
			Log.i( TAG, error.getMessage() );
		}
	}

	private class LaunchListLoader extends AsyncTask<JSONObject, Void, Integer>
	{
		@Override
		protected Integer doInBackground( JSONObject... response )
		{
			int numLaunches = 0;

			final Gson gson = new GsonBuilder().setDateFormat( Launch.DATE_FORMAT ).create();

			final JSONObject launchListObj = response[ 0 ];

			final Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						final Dao<Launch, Integer> launchDao = databaseHelper.getLaunchDao();
						final Dao<Location, Integer> locationDao = databaseHelper.getLocationDao();
						final Dao<Mission, Integer> missionDao = databaseHelper.getMissionDao();
						final Dao<Rocket, Integer> rocketDao = databaseHelper.getRocketDao();

						final JSONArray launchListArray = launchListObj.getJSONArray( "launch" );
						for( int ii = 0; ii < launchListArray.length(); ++ii )
						{
							final JSONObject launchObj = launchListArray.getJSONObject( ii );
							if( launchObj != null && m_adapter != null )
							{
								Launch launch = gson.fromJson( launchObj.toString(), Launch.class );

								locationDao.createOrUpdate( launch.location );
								missionDao.createOrUpdate( launch.mission );
								rocketDao.createOrUpdate( launch.rocket );

								// This must be run after all the others are created so the IDs of the child objects can be set
								launchDao.createOrUpdate( launch );
							}
						}

						final SharedPreferences preferences = activity.getPreferences( Context.MODE_PRIVATE );
						preferences.edit().putLong( Preferences.KEY_LAST_UPDATED, new Date().getTime() ).commit();
						Log.d( TAG, "Refresh successful: " + launchDao.countOf() + " Launches in database." );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}
					catch( JSONException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return numLaunches;
		}

		@Override
		protected void onPostExecute( Integer result )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				reloadData();

				activity.setProgressBarIndeterminateVisibility( false );
				Toast.makeText( activity, R.string.TOAST_launch_list_refresh_complete, Toast.LENGTH_SHORT ).show();
			}
		}
	}
}
