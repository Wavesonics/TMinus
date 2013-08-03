package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 7/24/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LocationDetailFragment extends DialogFragment
{
	public static final  String TAG              = LocationDetailFragment.class.getSimpleName();
	public static final  String ARG_ITEM_ID      = "item_id";
	private static final String MAP_FRAGMENT_TAG = "MapFragment";
	private Location    m_location;
	private FrameLayout m_mapContainer;

	public LocationDetailFragment()
	{
	}

	public static LocationDetailFragment newInstance( int locationId )
	{
		LocationDetailFragment locationDetailFragment = new LocationDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( ARG_ITEM_ID, locationId );
		locationDetailFragment.setArguments( arguments );

		return locationDetailFragment;
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		Dialog dialog = getDialog();
		if( dialog != null )
		{
			dialog.setTitle( R.string.LOCATIONDETAIL_title );
		}

		View rootView = inflater.inflate( R.layout.fragment_location_detail, container, false );

		if( rootView != null )
		{
			m_mapContainer = (FrameLayout)rootView.findViewById( R.id.LOCATIONDETAIL_map_container );

			SupportMapFragment mapFragment = createMap();
			getChildFragmentManager().beginTransaction().add( R.id.LOCATIONDETAIL_map_container, mapFragment, MAP_FRAGMENT_TAG ).commit();
		}

		return rootView;
	}

	private SupportMapFragment createMap()
	{
		GoogleMapOptions options = new GoogleMapOptions();

		//options.useViewLifecycleInFragment( true );
		options.compassEnabled( false );
		options.zoomControlsEnabled( false );
		options.mapType( GoogleMap.MAP_TYPE_SATELLITE );

		LatLng pos = getLocation();
		CameraPosition camPos = new CameraPosition( pos, 15.0f, 30f, 112.5f );
		options.camera( camPos );

		SupportMapFragment mapFragment = SupportMapFragment.newInstance( options );

		return mapFragment;
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		if( activity != null )
		{
			int googlePlayServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable( activity );
			if( googlePlayServicesAvailable != ConnectionResult.SUCCESS )
			{
				// TODO: Tell the user whats up and allow them to fix it
			}

			loadLocation();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		LatLng pos = getLocation();

		SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentByTag( MAP_FRAGMENT_TAG );
		GoogleMap map = mapFragment.getMap();
		if( map != null )
		{
			MarkerOptions marker = new MarkerOptions();
			marker.position( pos );
			map.addMarker( marker );
		}
	}

	public void updateViews()
	{
		if( m_location != null )
		{

		}
	}

	private void loadLocation()
	{
		int locationId = getLocationId();

		if( locationId >= 0 )
		{
			LocationLoader locationLoader = new LocationLoader();
			locationLoader.execute( locationId );
		}
	}

	private LatLng getLocation()
	{
		LatLng pos = new LatLng( 28.583333, -80.583056 );
		return pos;
	}

	public int getLocationId()
	{
		int locationId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_ITEM_ID ) )
		{
			locationId = arguments.getInt( ARG_ITEM_ID );
		}

		return locationId;
	}

	private class LocationLoader extends AsyncTask<Integer, Void, Location>
	{
		@Override
		protected Location doInBackground( Integer... ids )
		{
			Location location = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Location, Integer> locationDao = databaseHelper.getLocationDao();
						location = locationDao.queryForId( ids[ 0 ] );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return location;
		}

		@Override
		protected void onPostExecute( Location result )
		{
			Log.i( TAG, "Location loaded." );
			m_location = result;

			updateViews();
		}
	}
}
