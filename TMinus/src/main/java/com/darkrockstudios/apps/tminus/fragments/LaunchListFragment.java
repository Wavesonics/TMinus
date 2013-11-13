package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.LaunchUpdateService;
import com.darkrockstudios.apps.tminus.PullToRefreshProvider;
import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.R.layout;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.misc.Utilities;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;

/**
 * A list fragment representing a list of Launches. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link com.darkrockstudios.apps.tminus.fragments.LaunchDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LaunchListFragment extends ListFragment implements PullToRefreshAttacher.OnRefreshListener
{
	private static final String TAG                   = LaunchListFragment.class.getSimpleName();
	public static final  String ARG_PREVIOUS_LAUNCHES = LaunchListFragment.class.getPackage() + ".PREVIOUS_LAUNCHES";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks s_dummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected( Launch launch )
		{
		}
	};

	private boolean m_previousLaunches;
	private ArrayAdapter<Launch> m_adapter;
	private Callbacks m_callbacks         = s_dummyCallbacks;
	private int       m_activatedPosition = ListView.INVALID_POSITION;
	private LaunchUpdateReceiver  m_updateReceiver;
	private PullToRefreshAttacher m_pullToRefreshAttacher;

	public static LaunchListFragment newInstance( boolean previousLaunches )
	{
		LaunchListFragment fragment = new LaunchListFragment();

		Bundle arguments = new Bundle();
		arguments.putBoolean( LaunchListFragment.ARG_PREVIOUS_LAUNCHES, previousLaunches );
		fragment.setArguments( arguments );

		return fragment;
	}

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

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_PREVIOUS_LAUNCHES ) )
		{
			m_previousLaunches = arguments.getBoolean( ARG_PREVIOUS_LAUNCHES );
		}
		else
		{
			m_previousLaunches = false;
		}

		m_adapter = new LaunchListAdapter( getActivity() );

		setListAdapter( m_adapter );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_launch_list, null );

		if( m_pullToRefreshAttacher != null )
		{
			PullToRefreshLayout ptrLayout =
					(PullToRefreshLayout) view.findViewById( R.id.LAUNCHLIST_pull_to_refresh );
			ptrLayout.setPullToRefreshAttacher( m_pullToRefreshAttacher, this );
		}

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

		if( !reloadData() )
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

		if( activity instanceof PullToRefreshProvider )
		{
			m_pullToRefreshAttacher = ((PullToRefreshProvider) activity).getPullToRefreshAttacher();
		}

		m_updateReceiver = new LaunchUpdateReceiver();

		IntentFilter m_updateIntentFilter = new IntentFilter();
		m_updateIntentFilter.addAction( LaunchUpdateService.ACTION_LAUNCH_LIST_UPDATED );
		m_updateIntentFilter.addAction( LaunchUpdateService.ACTION_LAUNCH_LIST_UPDATE_FAILED );
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
	public void onListItemClick( ListView listView, View view, int position, long id )
	{
		super.onListItemClick( listView, view, position, id );

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Launch launch = (Launch) listView.getAdapter().getItem( position );
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
					QueryBuilder<Launch, Integer> queryBuilder = launchDao.queryBuilder();
					final PreparedQuery<Launch> query;

					if( !m_previousLaunches )
					{
						queryBuilder.where().ge( "net", new Date() );
						queryBuilder.orderBy( "net", true ).prepare();
						query = queryBuilder.prepare();
					}
					else
					{
						queryBuilder.where().le( "net", new Date() );
						queryBuilder.orderBy( "net", false ).prepare();
						query = queryBuilder.prepare();
					}

					List<Launch> results = launchDao.query( query );
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

	private void requestLaunches()
	{
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			Log.d( TAG, "Requesting launches..." );

			activity.setProgressBarIndeterminateVisibility( true );

			Intent launchUpdate = new Intent( activity, LaunchUpdateService.class );
			if( m_previousLaunches )
			{
				launchUpdate.putExtra( LaunchUpdateService.EXTRA_REQUEST_PREVIOUS_LAUNCHES, true );
			}
			activity.startService( launchUpdate );
		}
	}

	public void refresh()
	{
		requestLaunches();
	}

	@Override
	public void onRefreshStarted( View view )
	{
		refresh();
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

	private void hideLoadingIndicators()
	{
		if( m_pullToRefreshAttacher != null )
		{
			m_pullToRefreshAttacher.setRefreshComplete();
		}

		Activity activity = getActivity();
		if( activity != null )
		{
			activity.setProgressBarIndeterminateVisibility( false );
		}
	}

	private static class LaunchListAdapter extends ArrayAdapter<Launch>
	{
		private static int LAUNCH_ITEM = 0;
		private static int EMPTY       = 1;

		public LaunchListAdapter( Context context )
		{
			super( context, layout.row_launch_list_item );
		}

		@Override
		public int getViewTypeCount()
		{
			return 2;

		}

		@Override
		public int getItemViewType( int position )
		{
			final int type;

			if( super.getCount() > 0 )
			{
				type = LAUNCH_ITEM;
			}
			else
			{
				type = EMPTY;
			}

			return type;
		}

		@Override
		public int getCount()
		{
			int count = super.getCount();

			// Cell for "no launches" text
			if( count == 0 )
			{
				count = 1;
			}

			return count;
		}

		@Override
		public View getView( int pos, View convertView, ViewGroup parent )
		{
			final int viewType = getItemViewType( pos );
			View view = convertView;
			if( view == null )
			{
				LayoutInflater inflater = (LayoutInflater) getContext()
						                                           .getSystemService( Context.LAYOUT_INFLATER_SERVICE );

				if( viewType == LAUNCH_ITEM )
				{
					view = inflater.inflate( R.layout.row_launch_list_item, null );
				}
				else
				{
					view = inflater.inflate( android.R.layout.simple_list_item_1, null );
				}
			}

			if( viewType == LAUNCH_ITEM )
			{
				final Launch launch = getItem( pos );

				final TextView titleView = (TextView) view.findViewById( R.id.launch_list_item_title );
				titleView.setText( launch.name );

				final TextView descriptionView = (TextView) view.findViewById( R.id.launch_list_item_description );
				if( launch.mission != null )
				{
					descriptionView.setText( launch.mission.description );
				}
				else
				{
					descriptionView.setText( R.string.LAUNCHLIST_no_mission_details );
				}

				final TextView netView1 = (TextView) view.findViewById( R.id.launch_list_item_net_1 );
				final TextView netView2 = (TextView) view.findViewById( R.id.launch_list_item_net_2 );
				final TextView netView3 = (TextView) view.findViewById( R.id.launch_list_item_net_3 );

				SimpleDateFormat monthDay = new SimpleDateFormat( "MMM dd" );
				SimpleDateFormat year = new SimpleDateFormat( "yyyy" );
				SimpleDateFormat time = new SimpleDateFormat( "HH:mm" );

				netView1.setText( monthDay.format( launch.net ) );
				netView2.setText( year.format( launch.net ) );
				netView3.setText( time.format( launch.net ) );

				final ImageView typeIcon = (ImageView) view.findViewById( R.id.LAUNCHLIST_type_icon );
				typeIcon.setImageResource( Utilities.getLaunchTypeResource( launch.mission ) );
			}
			else
			{
				TextView textView = (TextView) view.findViewById( android.R.id.text1 );
				textView.setText( R.string.LAUNCHLIST_no_launches );
			}

			return view;
		}
	}

	private class LaunchUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( LaunchUpdateService.ACTION_LAUNCH_LIST_UPDATED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Launch List update SUCCESS broadcast, will update the UI now." );

					reloadData();
					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_launch_list_refresh_complete, Style.CONFIRM ).show();
				}
				else if( LaunchUpdateService.ACTION_LAUNCH_LIST_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Launch List update FAILURE broadcast." );

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_launch_list_refresh_failed, Style.ALERT ).show();
				}
			}
		}
	}
}
