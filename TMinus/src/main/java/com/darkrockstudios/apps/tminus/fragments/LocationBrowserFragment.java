package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by Adam on 10/13/13.
 */
public class LocationBrowserFragment extends Fragment implements GoogleMap.OnMarkerClickListener
{
	private static final String TAG              = LocationBrowserFragment.class.getSimpleName();
	private static final String MAP_FRAGMENT_TAG = "LocationBrowserMapFragment";
	private static final LatLng DEFAULT_LOCATION = new LatLng( 37.523506, -77.412109 );

	private LocInfoLoader  m_locationsLoader;
	private List<Location> m_locations;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_location_browser, container, false );

		//View map = view.findViewById( R.id.LOCATIONBROWSER_map_container );
		SupportMapFragment mapFragment = createMap();
		getChildFragmentManager().beginTransaction()
				.add( R.id.LOCATIONBROWSER_map_container, mapFragment, MAP_FRAGMENT_TAG )
				.commit();

		return view;
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

			reloadData();
		}
	}

	private void reloadData()
	{
		if( m_locationsLoader == null )
		{
			m_locationsLoader = new LocInfoLoader();
			m_locationsLoader.execute();
		}
	}

	private SupportMapFragment createMap()
	{
		GoogleMapOptions options = new GoogleMapOptions();

		options.useViewLifecycleInFragment( true );
		options.compassEnabled( false );
		options.zoomControlsEnabled( false );

		final boolean mapControlEnabled = true;
		options.zoomGesturesEnabled( mapControlEnabled );
		options.scrollGesturesEnabled( mapControlEnabled );
		options.rotateGesturesEnabled( mapControlEnabled );
		options.tiltGesturesEnabled( mapControlEnabled );

		options.mapType( GoogleMap.MAP_TYPE_SATELLITE );

		CameraPosition camPos = new CameraPosition( DEFAULT_LOCATION, 0.0f, 30f, 0.0f );
		options.camera( camPos );

		SupportMapFragment mapFragment = SupportMapFragment.newInstance( options );

		return mapFragment;
	}

	private void updateMap()
	{
		if( m_locations != null )
		{
			SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
					                                                      .findFragmentByTag( MAP_FRAGMENT_TAG );
			if( mapFragment != null )
			{
				GoogleMap map = mapFragment.getMap();
				if( map != null )
				{
					map.setOnMarkerClickListener( this );
					map.clear();

					for( Location location : m_locations )
					{
						if( location.latitude != null && location.longitude != null )
						{
							LatLng pos = new LatLng( location.latitude, location.longitude );

							MarkerOptions marker = new MarkerOptions();
							marker.position( pos );
							marker.title( location.locInfo.name );
							map.addMarker( marker );
						}
					}
				}
			}
		}
	}

	@Override
	public boolean onMarkerClick( Marker marker )
	{
		return false;
	}

	private class LocInfoLoader extends AsyncTask<Integer, Void, List<Location>>
	{
		@Override
		protected List<Location> doInBackground( Integer... ids )
		{
			List<Location> locations = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper =
						OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Location, Integer> locationDao = databaseHelper.getLocationDao();
						QueryBuilder<Location, Integer> builder = locationDao.queryBuilder();
						builder.groupBy( "locInfo_id" );
						builder.orderBy( "name", true );

						locations = locationDao.query( builder.prepare() );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return locations;
		}

		@Override
		protected void onPostExecute( List<Location> result )
		{
			Log.i( TAG, "Location loaded." );
			m_locations = result;

			m_locationsLoader = null;

			updateMap();
		}
	}
}
