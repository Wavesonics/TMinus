package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.darkrockstudios.apps.tminus.updatetasks.DataUpdaterService;
import com.darkrockstudios.apps.tminus.updatetasks.RocketUpdateTask;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Adam on 10/13/13.
 */
public class RocketBrowserFragment extends ListFragment implements OnRefreshListener
{
	private static final String TAG = RocketBrowserFragment.class.getSimpleName();

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks m_callbacks = s_dummyCallbacks;
	private RocketUpdateReceiver m_updateReceiver;
	private int m_activatedPosition = ListView.INVALID_POSITION;
	private ArrayAdapter<Rocket> m_adapter;
	private static final long UPDATE_THRESHOLD = TimeUnit.DAYS.toMillis( 7 );

	@InjectView(R.id.ROCKETLIST_pull_to_refresh)
	PullToRefreshLayout m_ptrLayout;

	@InjectView(android.R.id.list)
	ListView m_listView;

	private static Callbacks s_dummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected( Rocket rocket )
		{
		}
	};

	public static RocketBrowserFragment newInstance()
	{
		RocketBrowserFragment fragment = new RocketBrowserFragment();

		return fragment;
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
		View view = inflater.inflate( R.layout.fragment_rocket_list, null );
		ButterKnife.inject( this, view );

		m_adapter = new RocketListAdapter( getActivity() );
		AnimationAdapter animationAdapter = new ScaleInAnimationAdapter( m_adapter );
		setListAdapter( animationAdapter );

		animationAdapter.setAbsListView( m_listView );

		m_ptrLayout = (PullToRefreshLayout) view.findViewById( R.id.ROCKETLIST_pull_to_refresh );

		ActionBarPullToRefresh.from( getActivity() ).allChildrenArePullable().listener( this ).setup( m_ptrLayout );

		return view;
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

		boolean shouldRefresh = !reloadData();

		final SharedPreferences preferences = PreferenceManager
				                                      .getDefaultSharedPreferences( getActivity() );
		if( preferences.contains( Preferences.KEY_LAST_ROCKET_LIST_UPDATE ) )
		{
			long lastUpdated = preferences.getLong( Preferences.KEY_LAST_ROCKET_LIST_UPDATE, -1 );
			Date now = new Date();
			if( lastUpdated < now.getTime() - UPDATE_THRESHOLD )
			{
				shouldRefresh = true;
			}
		}
		else
		{
			shouldRefresh = true;
		}

		if( shouldRefresh )
		{
			refresh();
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
			m_callbacks = (Callbacks) activity;
		}

		m_updateReceiver = new RocketUpdateReceiver();

		IntentFilter m_updateIntentFilter = new IntentFilter();
		m_updateIntentFilter.addAction( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATED );
		m_updateIntentFilter.addAction( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATE_FAILED );
		activity.registerReceiver( m_updateReceiver, m_updateIntentFilter );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		m_callbacks = s_dummyCallbacks;

		Activity activity = getActivity();
		activity.unregisterReceiver( m_updateReceiver );
		m_updateReceiver = null;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	@Override
	public void onListItemClick( ListView listView, View view, int position, long id )
	{
		super.onListItemClick( listView, view, position, id );

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Rocket rocket = (Rocket) listView.getAdapter().getItem( position );
		m_callbacks.onItemSelected( rocket );
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
		if( activity != null && isAdded() )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				m_adapter.clear();

				try
				{
					Dao<Rocket, Integer> rocketDao = databaseHelper.getDao( Rocket.class );
					QueryBuilder<Rocket, Integer> queryBuilder = rocketDao.queryBuilder();
					//PreparedQuery<Rocket> query = queryBuilder.orderBy( "family_id", true ).prepare();

					List<Rocket> results = rocketDao.queryForAll();

					for( Rocket rocket : results )
					{
						rocket.refreshFamily( databaseHelper );
					}

					if( results != null && results.size() > 0 )
					{
						m_adapter.addAll( results );
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

	public void refresh()
	{
		requestRockets();
	}

	private void requestRockets()
	{
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			Log.d( TAG, "Requesting rockets..." );

			activity.setProgressBarIndeterminateVisibility( true );

			Intent rocketUpdate = new Intent( activity, DataUpdaterService.class );
			rocketUpdate.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, RocketUpdateTask.UPDATE_TYPE );
			activity.startService( rocketUpdate );
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

	private void hideLoadingIndicators()
	{
		if( m_ptrLayout != null )
		{
			m_ptrLayout.setRefreshComplete();
		}

		Activity activity = getActivity();
		if( activity != null )
		{
			activity.setProgressBarIndeterminateVisibility( false );
		}
	}

	@Override
	public void onRefreshStarted( View view )
	{
		refresh();
	}

	static class ViewHolder
	{
		@InjectView(R.id.ROCKETLISTITEM_rocket_name)
		TextView rocketNameView;

		@InjectView(R.id.ROCKETLISTITEM_rocket_configuration)
		TextView rocketConfigurationView;

		@InjectView(R.id.ROCKETLISTITEM_rocket_agencies)
		TextView rocketAgenciesView;

		public ViewHolder( final View view )
		{
			ButterKnife.inject( this, view );
		}
	}

	private static class RocketListAdapter extends ArrayAdapter<Rocket>
	{
		public RocketListAdapter( Context context )
		{
			super( context, R.layout.row_rocket_list_item );
		}

		@Override
		public View getView( int pos, View convertView, ViewGroup parent )
		{
			final View view;
			if( convertView != null )
			{
				view = convertView;
			}
			else
			{
				LayoutInflater inflater = LayoutInflater.from( getContext() );
				view = inflater.inflate( R.layout.row_rocket_list_item, parent, false );

				ViewHolder viewHolder = new ViewHolder( view );
				view.setTag( viewHolder );
			}

			ViewHolder viewHolder = (ViewHolder) view.getTag();

			Rocket rocket = getItem( pos );

			viewHolder.rocketNameView.setText( rocket.name );

			String agencies = "";
			if( rocket.family != null && rocket.family.agencies != null )
			{
				for( Iterator<Agency> it = rocket.family.agencies.iterator(); it.hasNext(); )
				{
					Agency agency = it.next();
					if( agency.abbrev != null )
					{
						agencies += agency.abbrev;
						if( it.hasNext() )
						{
							agencies += ", ";
						}
					}
				}
			}

			viewHolder.rocketAgenciesView.setText( agencies );

			final String configuration;
			if( rocket.configuration != null && rocket.configuration.trim().length() > 0 )
			{
				configuration = rocket.configuration.trim();
			}
			else
			{
				configuration = getContext().getString( R.string.ROCKETLIST_item_no_configuration );
			}

			viewHolder.rocketConfigurationView.setText( configuration );

			return view;
		}
	}

	public interface Callbacks
	{
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected( Rocket rocket );
	}

	private class RocketUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Rocket List update SUCCESS broadcast, will update the UI now." );

					reloadData();

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_rocket_list_update_complete, Style.CONFIRM ).show();
				}
				else if( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Rocket List update FAILURE broadcast." );

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_rocket_list_update_failed, Style.ALERT ).show();
				}
			}
		}
	}
}
