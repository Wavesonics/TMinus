package com.darkrockstudios.apps.tminus.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.RocketDetailUpdateService;
import com.darkrockstudios.apps.tminus.TMinusApplication;
import com.darkrockstudios.apps.tminus.database.RocketDetail;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailLoader;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailLoader.Listener;
import com.darkrockstudios.apps.tminus.loaders.RocketLoader;
import com.darkrockstudios.apps.tminus.loaders.RocketLoader.RocketLoadListener;
import com.darkrockstudios.apps.tminus.misc.DiskBitmapCache;

import java.io.File;

/**
 * Created by Adam on 7/14/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailFragment extends DialogFragment implements Listener, RocketLoadListener
{
	public static final String TAG         = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";
	private File                       m_dataDirectory;
	private Rocket                     m_rocket;
	private RocketDetail               m_rocketDetail;
	private NetworkImageView           m_rocketImage;
	private TextView                   m_rocketName;
	private TextView                   m_rocketConfiguration;
	private TextView                   m_rocketSummary;
	private RocketDetailUpdateReceiver m_updateReceiver;
	private IntentFilter               m_updateIntentFilter;

	public RocketDetailFragment()
	{
	}

	public static RocketDetailFragment newInstance( int rocketId )
	{
		RocketDetailFragment rocketDetailFragment = new RocketDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( ARG_ITEM_ID, rocketId );
		rocketDetailFragment.setArguments( arguments );

		return rocketDetailFragment;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		Dialog dialog = getDialog();
		if( dialog != null )
		{
			dialog.setTitle( R.string.ROCKETDETAIL_title );
		}

		View rootView = inflater.inflate( R.layout.fragment_rocket_detail, container, false );

		if( rootView != null )
		{
			m_rocketImage = (NetworkImageView)rootView.findViewById( R.id.ROCKETDETAIL_rocket_image );
			m_rocketName = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_name );
			m_rocketConfiguration = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_configuration );
			m_rocketSummary = (TextView)rootView.findViewById( R.id.ROCKETDETAIL_details );

			loadRocket();
		}

		return rootView;
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		String dataDirPath = activity.getApplicationInfo().dataDir;
		m_dataDirectory = new File( dataDirPath );

		m_updateReceiver = new RocketDetailUpdateReceiver();
		m_updateIntentFilter = new IntentFilter();
		m_updateIntentFilter.addAction( RocketDetailUpdateService.ACTION_ROCKET_DETAIL_UPDATED );
		m_updateIntentFilter.addAction( RocketDetailUpdateService.ACTION_ROCKET_DETAIL_UPDATE_FAILED );
		activity.registerReceiver( m_updateReceiver, m_updateIntentFilter );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		Activity activity = getActivity();
		activity.unregisterReceiver( m_updateReceiver );
		m_updateReceiver = null;
	}

	private void updateViews()
	{
		if( m_rocket != null && isAdded() )
		{
			m_rocketName.setText( m_rocket.name );
			m_rocketConfiguration.setText( m_rocket.configuration );

			if( m_rocketDetail != null )
			{
				final int MAX_CACHE_SIZE = 10 * 1024 * 1024;
				ImageLoader imageLoader = new ImageLoader( TMinusApplication
						                                           .getRequestQueue(), new DiskBitmapCache( m_dataDirectory, MAX_CACHE_SIZE ) );
				m_rocketImage.setImageUrl( m_rocketDetail.imageUrl, imageLoader );
				if( m_rocketDetail.summary != null )
				{
					m_rocketSummary.setText( Html.fromHtml( m_rocketDetail.summary ) );
				}
			}
		}
	}

	private void loadRocket()
	{
		final Activity activity = getActivity();
		final int rocketId = getRocketId();

		if( rocketId >= 0 && activity != null )
		{
			RocketLoader rocketLoader = new RocketLoader( this, activity );
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

	@Override
	public void rocketLoaded( Rocket rocket )
	{
		m_rocket = rocket;

		final Activity activity = getActivity();
		if( m_rocket != null && activity != null )
		{
			RocketDetailLoader detailLoader = new RocketDetailLoader( activity, this );
			detailLoader.execute( m_rocket.id );

			updateViews();
		}
	}

	@Override
	public void rocketLoadFailed( int rocketId )
	{
		// TODO: Handle rocket load failure
	}

	@Override
	public void rocketDetailLoaded( RocketDetail rocketDetail )
	{
		m_rocketDetail = rocketDetail;
		updateViews();
	}

	@Override
	public void rocketDetailMissing( int rocketId )
	{
		final Activity activity = getActivity();
		if( activity != null && m_rocket != null )
		{
			fetchRocketDetails();
		}
	}

	private void fetchRocketDetails()
	{
		final Activity activity = getActivity();
		if( activity != null && m_rocket != null )
		{
			Intent intent = new Intent( activity, RocketDetailUpdateService.class );
			intent.putExtra( RocketDetailUpdateService.EXTRA_ROCKET_ID, m_rocket.id );
			activity.startService( intent );
		}
	}

	private class RocketDetailUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( RocketDetailUpdateService.ACTION_ROCKET_DETAIL_UPDATED.equals( intent.getAction() ) )
				{
					Log.i( TAG, "Received Rocket Detail update SUCCESS broadcast, will update the UI now." );

					final int rocketId = intent.getIntExtra( RocketDetailUpdateService.EXTRA_ROCKET_ID, -1 );
					if( rocketId > 0 )
					{
						Log.i( TAG, "Rocket Detail fetch completely successfully for rocket id: " + rocketId );

						RocketDetailLoader detailLoader = new RocketDetailLoader( activity, RocketDetailFragment.this );
						detailLoader.execute( rocketId );

						activity.setProgressBarIndeterminateVisibility( false );
						Toast.makeText( activity, R.string.TOAST_launch_list_refresh_complete, Toast.LENGTH_SHORT )
						     .show();
					}

				}
				else if( RocketDetailUpdateService.ACTION_ROCKET_DETAIL_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.w( TAG, "Received Rocket Detail update FAILURE broadcast." );

					final int rocketId = intent.getIntExtra( RocketDetailUpdateService.EXTRA_ROCKET_ID, -1 );
					if( rocketId > 0 )
					{
						Log.w( TAG, "Rocket Detail fetch completely failed for rocket id: " + rocketId );
					}

					m_rocketSummary.setText( R.string.ROCKETDETAIL_no_summary );

					//Toast.makeText( activity, R.string.TOAST_rocket_detail_update_failed, Toast.LENGTH_LONG ).show();
					activity.setProgressBarIndeterminateVisibility( false );
				}
			}
		}
	}
}
