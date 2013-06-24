package com.darkrockstudios.apps.tminus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R.id;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;

/**
 * A fragment representing a single Launch detail screen.
 * This fragment is either contained in a {@link LaunchListActivity}
 * in two-pane mode (on tablets) or a {@link LaunchDetailActivity}
 * on handsets.
 */
public class LaunchDetailFragment extends Fragment
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	private Launch m_launchItem;

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

		if( getArguments().containsKey( ARG_ITEM_ID ) )
		{
			m_launchItem = (Launch)getArguments().getSerializable( ARG_ITEM_ID );
		}
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		View rootView = inflater.inflate( R.layout.fragment_launch_detail, container, false );

		if( m_launchItem != null )
		{
			final TextView name = (TextView)rootView.findViewById( id.LAUNCHDETAIL_mission_name );
			name.setText( m_launchItem.name );

			final TextView description = (TextView)rootView.findViewById( R.id.LAUNCHDETAIL_mission_description );
			description.setText( m_launchItem.mission.description );

			final TextView launchWindow = (TextView)rootView.findViewById( id.LAUNCHDETAIL_launch_window );
			launchWindow.setText( m_launchItem.windowstart );

			final TextView location = (TextView)rootView.findViewById( id.LAUNCHDETAIL_location );
			location.setText( m_launchItem.location.name );
		}

		return rootView;
	}
}
