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
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.misc.Utilities;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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
	public static final  String TAG                     =
			LocationDetailFragment.class.getSimpleName();
	public static final  String ARG_ITEM_ID             = "item_id";
	public static final  String ARG_MAP_CONTROL_ENABLED = "map_control_enabled";
	private static final String MAP_FRAGMENT_TAG        = "MapFragment";
	private static final float  LOCATION_ZOOM           = 15.0f;
	private static final LatLng DEFAULT_LOCATION        = new LatLng( 37.523506, -77.412109 );
	private Pad         m_pad;
	private FrameLayout m_mapContainer;
	private TextView    m_locationName;

	public LocationDetailFragment()
	{
	}

	public static LocationDetailFragment newInstance( int locationId, boolean mapControlsEnabled )
	{
		LocationDetailFragment locationDetailFragment = new LocationDetailFragment();

		Bundle arguments = new Bundle();
		arguments.putInt( ARG_ITEM_ID, locationId );
		arguments.putBoolean( ARG_MAP_CONTROL_ENABLED, mapControlsEnabled );
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
			m_mapContainer =
					(FrameLayout) rootView.findViewById( R.id.LOCATIONDETAIL_map_container );

			m_locationName =
					(TextView) rootView.findViewById( R.id.LOCATIONDETAIL_location_name );


			SupportMapFragment mapFragment = createMap();
			getChildFragmentManager().beginTransaction()
					.add( R.id.LOCATIONDETAIL_map_container, mapFragment, MAP_FRAGMENT_TAG )
					.commit();
		}

		return rootView;
	}

	private SupportMapFragment createMap()
	{
		GoogleMapOptions options = new GoogleMapOptions();

		options.useViewLifecycleInFragment( true );
		options.compassEnabled( false );
		options.zoomControlsEnabled( false );

		final boolean mapControlEnabled = geMapControlsEnabled();
		options.zoomGesturesEnabled( mapControlEnabled );
		options.scrollGesturesEnabled( mapControlEnabled );
		options.rotateGesturesEnabled( mapControlEnabled );
		options.tiltGesturesEnabled( mapControlEnabled );

		options.mapType( GoogleMap.MAP_TYPE_SATELLITE );

		CameraPosition camPos = new CameraPosition( getLocation(), 0.0f, 30f, 0.0f );
		options.camera( camPos );

		SupportMapFragment mapFragment = SupportMapFragment.newInstance( options );

		return mapFragment;
	}

	private void updateMap()
	{
		final LatLng pos = getLocation();

		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
				                                                      .findFragmentByTag( MAP_FRAGMENT_TAG );
		if( mapFragment != null )
		{
			GoogleMap map = mapFragment.getMap();
			if( map != null )
			{
				CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( pos, 0.0f );
				map.moveCamera( cameraUpdate );

				if( pos != DEFAULT_LOCATION )
				{
					MarkerOptions marker = new MarkerOptions();
					marker.position( pos );
					map.addMarker( marker );
				}
			}
		}
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		if( activity != null )
		{
			final int googlePlayServicesAvailable =
					GooglePlayServicesUtil.isGooglePlayServicesAvailable( activity );
			if( googlePlayServicesAvailable != ConnectionResult.SUCCESS )
			{
				GooglePlayServicesUtil.getErrorDialog( googlePlayServicesAvailable, activity, 0 )
				                      .show();
			}

			loadLocation();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();

		updateMap();
	}

	public void updateViews()
	{
		if( m_pad != null )
		{
			updateMap();

			if( m_locationName != null )
			{
				int flagResourceId = Utilities
						                     .getFlagResource( m_pad.location.countrycode );
				m_locationName.setCompoundDrawablesWithIntrinsicBounds( flagResourceId, 0, 0, 0 );
			}
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

	private float getLocationZoom()
	{
		LatLng pos = getLocation();
		final float zoom;
		if( pos == DEFAULT_LOCATION )
		{
			zoom = 0.0f;
		}
		else
		{
			zoom = LOCATION_ZOOM;
		}

		return zoom;
	}

	private LatLng getLocation()
	{
		final LatLng pos;

		if( m_pad != null && m_pad.longitude != null && m_pad.latitude != null )
		{
			pos = new LatLng( m_pad.latitude, m_pad.longitude );
		}
		else
		{
			pos = DEFAULT_LOCATION;
		}

		return pos;
	}

	public boolean geMapControlsEnabled()
	{
		boolean mapControlsEnabled = false;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_MAP_CONTROL_ENABLED ) )
		{
			mapControlsEnabled = arguments.getBoolean( ARG_MAP_CONTROL_ENABLED );
		}

		return mapControlsEnabled;
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

	private class LocationLoader extends AsyncTask<Integer, Void, Pad>
	{
		@Override
		protected Pad doInBackground( Integer... ids )
		{
			Pad pad = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper =
						OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Pad, Integer> locationDao = databaseHelper.getPadDao();
						pad = locationDao.queryForId( ids[ 0 ] );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return pad;
		}

		@Override
		protected void onPostExecute( Pad result )
		{
			Log.i( TAG, "Pad loaded." );
			m_pad = result;

			updateViews();
		}
	}
}
