package com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.experiences.location.detail.fragments.LocationDetailFragment;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.views.slidingtabs.SlidingTabLayout;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Adam on 2/24/14.
 */
public class LaunchDetailContainerFragment extends Fragment
{
	public static final String ARG_ITEM_ID = "launch_id";

	private LaunchDetailPagerAdapter m_adapter;

	private Launch m_launch;

	@InjectView(R.id.LAUNCHDETAIL_pager_tabs)
	SlidingTabLayout m_tabs;

	@InjectView(R.id.LAUNCHDETAIL_pager)
	ViewPager m_pager;

	public static LaunchDetailContainerFragment newInstance( final int launchId )
	{
		Bundle arguments = new Bundle();
		arguments.putInt( LaunchDetailContainerFragment.ARG_ITEM_ID, launchId );
		LaunchDetailContainerFragment fragment = new LaunchDetailContainerFragment();
		fragment.setArguments( arguments );
		return fragment;
	}

	public LaunchDetailContainerFragment()
	{
	}

	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_adapter = new LaunchDetailPagerAdapter( getFragmentManager() );

		m_launch = getLaunch();
	}

	private Launch getLaunch()
	{
		Launch launch = null;

		Activity activity = getActivity();

		final int launchId = getLaunchId();
		if( launchId >= 0 )
		{
			DatabaseHelper helper = new DatabaseHelper( activity );
			try
			{
				Dao<Launch, Integer> launchDao = helper.getDao( Launch.class );
				launch = launchDao.queryForId( launchId );
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}
			finally
			{
				helper.close();
			}
		}

		return launch;
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_launch_container, container, false );

		if( view != null )
		{
			ButterKnife.inject( this, view );

			m_pager.setAdapter( m_adapter );
			m_tabs.setViewPager( m_pager );
		}

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
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

	private class LaunchDetailPagerAdapter extends FragmentStatePagerAdapter
	{

		public LaunchDetailPagerAdapter( final FragmentManager fm )
		{
			super( fm );
		}

		@Override
		public CharSequence getPageTitle( final int position )
		{
			String title = "";

			switch( position )
			{
				case 0:
					title = "Mission";
					break;
				case 1:
					title = "Location";
					break;
				case 2:
					title = "Rocket";
					break;
				default:
					title = "";
					break;
			}

			return title;
		}

		@Override
		public Fragment getItem( final int position )
		{
			final Fragment fragment;

			switch( position )
			{
				case 0:
					fragment = LaunchDetailFragment.newInstance( getLaunchId() );
					break;
				case 1:
					fragment = LocationDetailFragment
							           .newInstance( m_launch.location.id, LocationDetailFragment.ARG_UNSET_ID, true, false );
					break;
				case 2:
					fragment = RocketDetailFragment.newInstance( m_launch.rocket.id, false );
					break;
				default:
					fragment = null;
					break;
			}

			return fragment;
		}

		@Override
		public int getCount()
		{
			return 3;
		}
	}
}
