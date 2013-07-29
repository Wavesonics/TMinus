package com.darkrockstudios.apps.tminus.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.TMinusApplication;
import com.darkrockstudios.apps.tminus.database.RocketDetail;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailFetcher;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailFetcher.RocketDetailFetchListener;
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
public class RocketDetailFragment extends DialogFragment implements Listener, RocketLoadListener, RocketDetailFetchListener
{
	public static final String TAG         = LaunchDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";
	private File             m_dataDirectory;
	private Rocket           m_rocket;
	private RocketDetail     m_rocketDetail;
	private NetworkImageView m_rocketImage;
	private TextView         m_rocketName;
	private TextView         m_rocketConfiguration;
	private TextView         m_rocketSummary;

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
	}

	private void updateViews()
	{
		if( m_rocket != null )
		{
			m_rocketName.setText( m_rocket.name );
			m_rocketConfiguration.setText( m_rocket.configuration );

			if( m_rocketDetail != null )
			{
				final int MAX_CACHE_SIZE = 10 * 1024 * 1024;
				ImageLoader imageLoader = new ImageLoader( TMinusApplication
						                                           .getRequestQueue(), new DiskBitmapCache( m_dataDirectory, MAX_CACHE_SIZE ) );
				m_rocketImage.setImageUrl( m_rocketDetail.imageUrl, imageLoader );
				m_rocketSummary.setText( Html.fromHtml( m_rocketDetail.summary ) );
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
			RocketDetailFetcher.requestRocketDetails( m_rocket, this, activity );
		}
	}

	@Override
	public void rocketDetailFetchSuccessful( int rocketId )
	{
		Log.i( TAG, "Rocket Detail fetch completely successfully for rocket id: " + rocketId );

		final Activity activity = getActivity();
		if( activity != null && m_rocket != null )
		{
			RocketDetailLoader detailLoader = new RocketDetailLoader( activity, this );
			detailLoader.execute( m_rocket.id );
		}
	}

	@Override
	public void rocketDetailFetchFailed( int rocketId )
	{
		Log.w( TAG, "Rocket Detail fetch completely failed for rocket id: " + rocketId );
		m_rocketSummary.setText( R.string.ROCKETDETAIL_no_summary );
	}
}
