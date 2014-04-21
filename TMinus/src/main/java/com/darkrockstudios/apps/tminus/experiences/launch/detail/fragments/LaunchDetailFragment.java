package com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.R.id;
import com.darkrockstudios.apps.tminus.database.tables.RocketDetail;
import com.darkrockstudios.apps.tminus.dataupdate.DataUpdaterService;
import com.darkrockstudios.apps.tminus.experiences.launch.browse.LaunchListActivity;
import com.darkrockstudios.apps.tminus.experiences.launch.detail.LaunchDetailActivity;
import com.darkrockstudios.apps.tminus.experiences.launch.detail.adapters.MissionsAdapter;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.dataupdate.RocketDetailUpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.loaders.LaunchLoader;
import com.darkrockstudios.apps.tminus.loaders.LaunchLoader.Listener;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailLoader;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.darkrockstudios.apps.tminus.misc.Utilities;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * A fragment representing a single Launch detail screen.
 * This fragment is either contained in a {@link LaunchListActivity}
 * in two-pane mode (on tablets) or a {@link LaunchDetailActivity}
 * on handsets.
 */
public class LaunchDetailFragment extends Fragment implements Listener, RocketDetailLoader.Listener, Utilities.ZoomAnimationHandler, ExpandableListView.OnGroupExpandListener
{
	public static final  String TAG                         =
			LaunchDetailFragment.class.getSimpleName();
	public static final  String ARG_ITEM_ID                 = "item_id";
	private static final long   DISPLAY_COUNTDOWN_THRESHOLD = TimeUnit.DAYS.toMillis( 2 );
	private ShareActionProvider m_shareActionProvider;
	private Launch              m_launchItem;
	private RocketDetail        m_rocketDetail;
	private TimeReceiver        m_timeReceiver;
	private MissionsAdapter     m_missionAdapter;

	private int m_lastExpandedPosition;

	@InjectView(R.id.LAUNCHDETAIL_content_view)
	View m_contentView;

	@InjectView(R.id.LAUNCHDETAIL_mission_list)
	ExpandableListView m_listView;

	@InjectView(R.id.LAUNCHDETAIL_launch_name)
	TextView m_launchName;

	@InjectView(R.id.LAUNCHDETAIL_status)
	TextView m_status;

	@InjectView(R.id.LAUNCHDETAIL_net)
	TextView m_net;

	@InjectView(R.id.LAUNCHDETAIL_window_length)
	TextView m_windowLength;

	@Optional
	@InjectView(R.id.LAUNCHDETAIL_time_remaining)
	TextView m_timeRemaining;

	@Optional
	@InjectView(R.id.LAUNCHDETAIL_container_view)
	View m_containerView;

	@InjectView(R.id.progressBar)
	View m_progressBar;

	private RocketDetailUpdateReceiver m_rocketDetailUpdateReceiver;

	private Animator m_currentAnimator;
	private int      m_shortAnimationDuration;

	public LaunchDetailFragment()
	{
	}

	public static LaunchDetailFragment newInstance( final int launchId )
	{
		Bundle arguments = new Bundle();
		arguments.putInt( LaunchDetailFragment.ARG_ITEM_ID, launchId );
		LaunchDetailFragment fragment = new LaunchDetailFragment();
		fragment.setArguments( arguments );
		return fragment;
	}

	@Override
	public void onAttach( final Activity activity )
	{
		super.onAttach( activity );

		m_timeReceiver = new TimeReceiver();
		IntentFilter intentFilter = new IntentFilter( Intent.ACTION_TIME_TICK );

		activity.registerReceiver( m_timeReceiver, intentFilter );

		m_rocketDetailUpdateReceiver = new RocketDetailUpdateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction( RocketDetailUpdateTask.ACTION_ROCKET_DETAILS_UPDATED );
		filter.addAction( RocketDetailUpdateTask.ACTION_ROCKET_DETAILS_UPDATE_FAILED );
		filter.addDataScheme( TminusUri.SCHEME );
		activity.registerReceiver( m_rocketDetailUpdateReceiver, filter );
	}

	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		setHasOptionsMenu( true );

		m_missionAdapter = new MissionsAdapter( getActivity() );

		m_shortAnimationDuration = getResources().getInteger( android.R.integer.config_shortAnimTime );
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container,
	                          final Bundle savedInstanceState )
	{
		View rootView = inflater.inflate( R.layout.fragment_launch_detail, container, false );

		if( rootView != null )
		{
			ButterKnife.inject( this, rootView );

			m_listView.setAdapter( m_missionAdapter );
			m_listView.setOnGroupExpandListener( this );

			//m_countDownContainer.setVisibility( View.GONE );

			loadLaunch();
		}

		return rootView;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		handleCountDownContainer();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		Activity activity = getActivity();
		activity.unregisterReceiver( m_timeReceiver );

		m_timeReceiver = null;

		activity.unregisterReceiver( m_rocketDetailUpdateReceiver );
		m_rocketDetailUpdateReceiver = null;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	@Override
	public void onCreateOptionsMenu( final Menu menu, final MenuInflater inflater )
	{
		inflater.inflate( R.menu.launch_detail, menu );

		MenuItem item = menu.findItem( R.id.menu_item_share );
		if( item != null )
		{
			m_shareActionProvider = (ShareActionProvider) item.getActionProvider();
		}
		updateShareIntent();

		super.onCreateOptionsMenu( menu, inflater );
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		final boolean handled;

		switch( item.getItemId() )
		{
			case id.menu_item_add_to_calendar:
				addLaunchToCalendar();
				handled = true;
				break;
			default:
				handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}

	private void addLaunchToCalendar()
	{
		if( m_launchItem != null )
		{
			final String title = getString( R.string.CALENDAR_event_title, m_launchItem.name );
			final String description =
					getString( R.string.CALENDAR_event_description, m_launchItem.rocket.name,
					           m_launchItem.rocket.configuration );

			Pad pad = m_launchItem.location.pads.iterator().next();

			Intent intent = new Intent( Intent.ACTION_INSERT )
					                .setData( Events.CONTENT_URI )
					                .putExtra( CalendarContract.EXTRA_EVENT_BEGIN_TIME, m_launchItem.net.getMillis() )
					                .putExtra( CalendarContract.EXTRA_EVENT_END_TIME,
					                           m_launchItem.windowend.getMillis() )
					                .putExtra( Events.TITLE, title )
					                .putExtra( Events.DESCRIPTION, description )
					                .putExtra( Events.EVENT_LOCATION, pad.name )
					                .putExtra( Events.AVAILABILITY, Events.AVAILABILITY_BUSY );
			startActivity( intent );
		}
	}

	public int getLaunchId()
	{
		int launchId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_ITEM_ID ) )
		{
			launchId = arguments.getInt( ARG_ITEM_ID );
		}

		return launchId;
	}

	private void showContent()
	{
		if( m_contentView != null && m_progressBar != null )
		{
			m_contentView.setVisibility( View.VISIBLE );
			m_progressBar.setVisibility( View.GONE );
		}
	}

	private void showLoading()
	{
		if( m_contentView != null && m_progressBar != null )
		{
			m_contentView.setVisibility( View.GONE );
			m_progressBar.setVisibility( View.VISIBLE );
		}
	}

	private void updateViews()
	{
		if( m_launchItem != null && isAdded() )
		{
			final View rootView = getView();

			m_launchName.setText( m_launchItem.name );
			m_status.setText( Utilities.getStatusText( m_launchItem, rootView.getContext() ) );

			m_missionAdapter.clear();
			m_missionAdapter.addAll( m_launchItem.missions );
			m_listView.expandGroup( 0 );

			/*
			final TextView description =
					(TextView) rootView.findViewById( R.id.LAUNCHDETAIL_mission_description );
			*/

			//Drawable flagDrawable = FlagResourceUtility.getFlagDrawable( pad.location.countryCode, getActivity() );
			//location.setCompoundDrawablesWithIntrinsicBounds( null, null, flagDrawable, null );

			if( m_launchItem.windowend != null )
			{
				Duration windowLength = new Duration( m_launchItem.windowend, m_launchItem.windowstart );

				m_windowLength.setText( Utilities.getFormattedTime( windowLength.getMillis() ) );
			}
			else
			{
				m_windowLength.setText( "---" );
			}

			m_net.setText( m_launchItem.net.getYear() + "" );
			/*
			final TextView netView1 = (TextView) rootView.findViewById( R.id.launch_detail_net_1 );
			final TextView netView2 = (TextView) rootView.findViewById( R.id.launch_detail_net_2 );
			final TextView netView3 = (TextView) rootView.findViewById( R.id.launch_detail_net_3 );

			SimpleDateFormat monthDay = new SimpleDateFormat( "MMM dd" );
			SimpleDateFormat year = new SimpleDateFormat( "yyyy" );
			SimpleDateFormat time = new SimpleDateFormat( "HH:mm" );

			netView1.setText( monthDay.format( m_launchItem.net ) );
			netView2.setText( year.format( m_launchItem.net ) );
			netView3.setText( time.format( m_launchItem.net ) );
			*/

			updateTimeViews();
			handleCountDownContainer();
		}
	}

	public void updateTimeViews()
	{
		if( m_launchItem != null && m_timeRemaining != null )
		{
			final View rootView = getView();

			Duration timeLeft = new Duration( DateTime.now(), m_launchItem.net );
			m_timeRemaining.setText( Utilities.getFormattedTime( timeLeft.getMillis() ) );

			handleCountDownContainer();
		}
	}

	public void loadLaunch()
	{
		final int launchId = getLaunchId();
		if( launchId >= 0 )
		{
			showLoading();

			LaunchLoader loader = new LaunchLoader( getActivity(), this );
			loader.execute( launchId );
		}
	}

	private void handleCountDownContainer()
	{
		boolean alwaysShow = false;

		final Activity activity = getActivity();
		if( activity != null )
		{
			final SharedPreferences preferences =
					PreferenceManager.getDefaultSharedPreferences( activity );
			alwaysShow = preferences.getBoolean( Preferences.KEY_ALWAYS_SHOW_COUNT_DOWN, false );
		}

		if( m_launchItem != null )
		{
			final DateTime thresholdDate = m_launchItem.net.minus( DISPLAY_COUNTDOWN_THRESHOLD );

			if( m_launchItem.net.isAfter( DateTime.now() ) && thresholdDate.isBefore( DateTime.now() ) || alwaysShow )
			{
				//m_countDownContainer.setVisibility( View.VISIBLE );
			}
			else
			{
				//m_countDownContainer.setVisibility( View.GONE );
			}
		}
	}

	private void updateShareIntent()
	{
		if( m_launchItem != null && m_shareActionProvider != null && isAdded() )
		{
			Intent intent = new Intent( android.content.Intent.ACTION_SEND );
			intent.setType( "text/plain" );
			intent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET );

			// Add data to the intent, the receiving app will decide what to do with it.
			intent.putExtra( Intent.EXTRA_SUBJECT, "Upcoming Space Launch: " + m_launchItem.name );
			intent.putExtra( Intent.EXTRA_TEXT, generateShareBody() );

			m_shareActionProvider.setShareIntent( intent );
		}
	}

	private String generateShareBody()
	{
		String body = "";

		if( m_launchItem != null && isAdded() )
		{
			final String missionDescription;
			// TODO handle multiple missions
			if( m_launchItem.missions != null && m_launchItem.missions.size() > 0 )
			{
				Mission mission = m_launchItem.missions.iterator().next();
				missionDescription = mission.description;
			}
			else
			{
				missionDescription = getString( R.string.LAUNCHDETAIL_share_no_mission );
			}

			Pad pad = m_launchItem.location.pads.iterator().next();

			body = getString( R.string.LAUNCHDETAIL_share_details, missionDescription,
			                  pad.name, m_launchItem.net );
		}

		return body;
	}

	@Override
	public void launchLoaded( final Launch launch )
	{
		m_launchItem = launch;

		if( isAdded() )
		{
			loadRocketDetails();

			updateViews();
			updateShareIntent();
			showContent();
		}
	}

	private void loadRocketDetails()
	{
		Activity activity = getActivity();
		if( activity != null && m_launchItem != null )
		{
			RocketDetailLoader detailLoader = new RocketDetailLoader( activity, this );
			detailLoader.execute( m_launchItem.rocket.id );
		}
	}

	@Override
	public void rocketDetailLoaded( final RocketDetail rocketDetail )
	{
		m_rocketDetail = rocketDetail;

		updateViews();
	}

	@Override
	public void rocketDetailMissing( final int rocketId )
	{
		final Activity activity = getActivity();
		if( activity != null && m_launchItem != null )
		{
			startRocketDetailsUpdate();
		}
	}

	private void startRocketDetailsUpdate()
	{
		Activity activity = getActivity();
		if( m_launchItem != null && m_launchItem.rocket != null && activity != null && isAdded() )
		{
			Intent intent = new Intent( activity, DataUpdaterService.class );
			intent.setData( TminusUri.buildRocketUri( m_launchItem.rocket.id ) );
			intent.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, RocketDetailUpdateTask.UPDATE_TYPE );

			activity.startService( intent );
		}
	}

	@Override
	public void setCurrentAnimator( final Animator animator )
	{
		m_currentAnimator = animator;
	}

	@Override
	public Animator getCurrentAnimator()
	{
		return m_currentAnimator;
	}

	@Override
	public void onGroupExpand( final int groupPosition )
	{
		// Only allow one group to be expanded at a time
		if( m_lastExpandedPosition != -1 && groupPosition != m_lastExpandedPosition )
		{
			m_listView.collapseGroup( m_lastExpandedPosition );
		}
		m_lastExpandedPosition = groupPosition;

		m_listView.post( new Runnable()
		{
			@Override
			public void run()
			{
				m_listView.smoothScrollToPosition( groupPosition );
			}
		} );
	}

	private class TimeReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			updateTimeViews();
		}
	}

	private class RocketDetailUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( RocketDetailUpdateTask.ACTION_ROCKET_DETAILS_UPDATED
						    .equals( intent.getAction() ) )
				{
					Log.i( TAG,
					       "Received Rocket Detail update SUCCESS broadcast, will update the UI now." );

					final int rocketId = TminusUri.extractRocketId( intent.getData() );
					if( rocketId > 0 )
					{
						Log.i( TAG, "Rocket Detail fetch completely successfully for rocket id: " +
						            rocketId );

						RocketDetailLoader detailLoader =
								new RocketDetailLoader( activity, LaunchDetailFragment.this );
						detailLoader.execute( rocketId );

						activity.setProgressBarIndeterminateVisibility( false );
					}
				}
				else if( RocketDetailUpdateTask.ACTION_ROCKET_DETAILS_UPDATE_FAILED
						         .equals( intent.getAction() ) )
				{
					Log.w( TAG, "Received Rocket Detail update FAILURE broadcast." );

					final int rocketId = TminusUri.extractRocketId( intent.getData() );
					if( rocketId > 0 )
					{
						Log.w( TAG,
						       "Rocket Detail fetch completely failed for rocket id: " + rocketId );
					}

					// TODO: set failure image here

					activity.setProgressBarIndeterminateVisibility( false );
				}
			}
		}
	}
}
