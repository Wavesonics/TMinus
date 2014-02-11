package com.darkrockstudios.apps.tminus.experiences.launch.browse.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.LaunchUpdateService;
import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.R.layout;
import com.darkrockstudios.apps.tminus.base.fragments.BaseBrowserFragment;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments.LaunchDetailFragment;
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

/**
 * A list fragment representing a list of Launches. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link LaunchDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LaunchListFragment extends BaseBrowserFragment
{
	private static final String TAG                   = LaunchListFragment.class.getSimpleName();
	public static final  String ARG_PREVIOUS_LAUNCHES = LaunchListFragment.class.getPackage() + ".PREVIOUS_LAUNCHES";

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks
	{
		public void onItemSelected( Launch launch );
	}

	private static Callbacks s_dummyCallbacks = new Callbacks()
	{
		@Override
		public void onItemSelected( final Launch launch )
		{
		}
	};

	private boolean              m_previousLaunches;
	private ArrayAdapter<Launch> m_adapter;
	private Callbacks m_callbacks = s_dummyCallbacks;
	private LaunchUpdateReceiver m_updateReceiver;

	public static LaunchListFragment newInstance( final boolean previousLaunches )
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
	public void onCreate( final Bundle savedInstanceState )
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
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container,
	                          final Bundle savedInstanceState )
	{
		View view = super.onCreateView( inflater, container, savedInstanceState );

		setListAdapter( m_adapter );

		return view;
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		if( !reloadData() )
		{
			refresh();
		}
	}

	@Override
	public void onAttach( final Activity activity )
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
	public void onListItemClick( final ListView listView, final View view, final int position, final long id )
	{
		super.onListItemClick( listView, view, position, id );

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Launch launch = (Launch) listView.getAdapter().getItem( position );
		m_callbacks.onItemSelected( launch );
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
					Dao<Launch, Integer> launchDao = databaseHelper.getDao( Launch.class );
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
				catch( final SQLException e )
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

	@Override
	public void refresh()
	{
		requestLaunches();
	}

	private static class LaunchListAdapter extends ArrayAdapter<Launch>
	{
		private static int LAUNCH_ITEM = 0;
		private static int EMPTY       = 1;

		public LaunchListAdapter( final Context context )
		{
			super( context, layout.row_launch_list_item );
		}

		@Override
		public int getViewTypeCount()
		{
			return 2;

		}

		@Override
		public int getItemViewType( final int position )
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
		public View getView( final int pos, final View convertView, final ViewGroup parent )
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
		public void onReceive( final Context context, final Intent intent )
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
