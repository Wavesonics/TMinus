package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.misc.Utilities;

import java.util.Date;

/**
 * A fragment representing a single Launch detail screen.
 * This fragment is either contained in a {@link LaunchListActivity}
 * in two-pane mode (on tablets) or a {@link LaunchDetailActivity}
 * on handsets.
 */
public class LaunchDetailFragment extends Fragment implements LaunchLoader.Listener
{
	public static final String TAG         = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";
	private ShareActionProvider m_shareActionProvider;
	private Launch              m_launchItem;
	private TimeReceiver        m_timeReceiver;

	private View m_contentView;
	private View m_progressBar;

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
	}

	@Override
	public void onStart()
	{
		super.onStart();

		m_timeReceiver = new TimeReceiver();
		IntentFilter intentFilter = new IntentFilter( Intent.ACTION_TIME_TICK );

		Activity activity = getActivity();
		activity.registerReceiver( m_timeReceiver, intentFilter );
	}

	@Override
	public void onStop()
	{
		super.onStop();

		Activity activity = getActivity();
		activity.unregisterReceiver( m_timeReceiver );

		m_timeReceiver = null;
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
		View rootView = inflater.inflate( R.layout.fragment_launch_detail, container, false );

		if( rootView != null )
		{
			m_contentView = rootView.findViewById( R.id.content_view );
			m_progressBar = rootView.findViewById( R.id.progressBar );

			loadLaunch();
		}

		return rootView;
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

	public int getLaunchId()
	{
		int launchId = -1;

		if( getArguments().containsKey( ARG_ITEM_ID ) )
		{
			launchId = getArguments().getInt( ARG_ITEM_ID );
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
		if( m_launchItem != null )
		{
			final View rootView = getView();

			final TextView name = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_mission_name );
			name.setText( m_launchItem.name );

			final TextView description = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_mission_description );
			description.setText( m_launchItem.mission.description );

			final TextView launchWindow = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_launch_window );
			launchWindow.setText( m_launchItem.windowstart.toString() );

			final TextView location = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_location );
			location.setText( m_launchItem.location.name );

			final TextView windowLength = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_window_length );
			final long windowLengthMs = m_launchItem.windowend.getTime() - m_launchItem.windowstart.getTime();
			windowLength.setText( Utilities.getFormattedTime( windowLengthMs ) );

			updateTimeViews();
		}
	}

	public void updateTimeViews()
	{
		if( m_launchItem != null )
		{
			final View rootView = getView();

			final TextView timeRemaining = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_time_remaining );
			final Date now = new Date();

			final long totalMsLeft = m_launchItem.windowstart.getTime() - now.getTime();
			timeRemaining.setText( Utilities.getFormattedTime( totalMsLeft ) );
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

	private void updateShareIntent()
	{
		if( m_launchItem != null && m_shareActionProvider != null )
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

		if( m_launchItem != null )
		{
			body += m_launchItem.mission.description + "\n\n";
			body += "Location: " + m_launchItem.location.name + "\n\n";
			body += "Expected Launch Time: " + m_launchItem.net + "\n\n";
		}

		return body;
	}

	private class TimeReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			updateTimeViews();
		}
	}

	@Override
	public void launchLoaded( Launch launch )
	{
		m_launchItem = launch;
		updateViews();
		updateShareIntent();
		showContent();
	}
}
