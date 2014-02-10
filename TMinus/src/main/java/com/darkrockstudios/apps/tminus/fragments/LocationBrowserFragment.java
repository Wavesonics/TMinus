package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.darkrockstudios.apps.tminus.updatetasks.DataUpdaterService;
import com.darkrockstudios.apps.tminus.updatetasks.LocationUpdateTask;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 10/13/13.
 */
public class LocationBrowserFragment extends Fragment implements GoogleMap.OnInfoWindowClickListener
{
	private static final String TAG              = LocationBrowserFragment.class.getSimpleName();
	private static final String MAP_FRAGMENT_TAG = "LocationBrowserMapFragment";
	private static final LatLng DEFAULT_LOCATION = new LatLng( 37.523506, -77.412109 );

	private LocInfoLoader    m_locationsLoader;
	private List<Pad>        m_pads;
	private Map<Marker, Pad> m_locationLookup;

	private LocationClickListener  m_locationClickListener;
	private LocationUpdateReceiver m_updateReceiver;

	private static final long UPDATE_THRESHOLD = TimeUnit.DAYS.toMillis( 7 );

	public static interface LocationClickListener
	{
		public void onLocationClicked( Pad pad );
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_locationLookup = new HashMap<Marker, Pad>();
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

		boolean shouldRefresh = false;
		final SharedPreferences preferences = PreferenceManager
				                                      .getDefaultSharedPreferences( getActivity() );
		if( preferences.contains( Preferences.KEY_LAST_LOCATION_LIST_UPDATE ) )
		{
			long lastUpdated = preferences.getLong( Preferences.KEY_LAST_LOCATION_LIST_UPDATE, -1 );
			Date now = new Date();
			if( lastUpdated < now.getTime() - UPDATE_THRESHOLD )
			{
				shouldRefresh = true;
			}
		}
		else
		{
			shouldRefresh = true;
		}

		if( shouldRefresh )
		{
			refresh();
		}

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

			if( activity instanceof LocationClickListener )
			{
				m_locationClickListener = (LocationClickListener) activity;
			}

			m_updateReceiver = new LocationUpdateReceiver();

			IntentFilter m_updateIntentFilter = new IntentFilter();
			m_updateIntentFilter.addAction( LocationUpdateTask.ACTION_LOCATION_LIST_UPDATED );
			m_updateIntentFilter.addAction( LocationUpdateTask.ACTION_LOCATION_LIST_UPDATE_FAILED );
			activity.registerReceiver( m_updateReceiver, m_updateIntentFilter );

			reloadData();
		}
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		m_locationClickListener = null;

		Activity activity = getActivity();
		if( m_updateReceiver != null && activity != null )
		{
			activity.unregisterReceiver( m_updateReceiver );
			m_updateReceiver = null;
		}
	}

	public void refresh()
	{
		requestLocations();
	}

	private void requestLocations()
	{
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			Log.d( TAG, "Requesting locations..." );

			activity.setProgressBarIndeterminateVisibility( true );

			Intent locationUpdate = new Intent( activity, DataUpdaterService.class );
			locationUpdate.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, LocationUpdateTask.UPDATE_TYPE );
			activity.startService( locationUpdate );
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
		if( m_pads != null )
		{
			SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
					                                                      .findFragmentByTag( MAP_FRAGMENT_TAG );
			if( mapFragment != null )
			{
				GoogleMap map = mapFragment.getMap();
				if( map != null )
				{
					map.setOnInfoWindowClickListener( this );
					map.clear();

					m_locationLookup.clear();

					for( Pad pad : m_pads )
					{
						if( pad.latitude != null && pad.longitude != null )
						{
							LatLng pos = new LatLng( pad.latitude, pad.longitude );

							MarkerOptions markerOptions = new MarkerOptions();
							markerOptions.position( pos );
							markerOptions.title( pad.location.name );
							Marker marker = map.addMarker( markerOptions );

							m_locationLookup.put( marker, pad );
						}
					}
				}
			}
		}
	}

	@Override
	public void onInfoWindowClick( Marker marker )
	{
		Pad pad = m_locationLookup.get( marker );
		if( m_locationClickListener != null && pad != null )
		{
			m_locationClickListener.onLocationClicked( pad );
		}
	}

	private class LocInfoLoader extends AsyncTask<Integer, Void, List<Pad>>
	{
		@Override
		protected List<Pad> doInBackground( Integer... ids )
		{
			List<Pad> pads = null;

			Activity activity = getActivity();
			if( activity != null )
			{
				final DatabaseHelper databaseHelper =
						OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Pad, Integer> locationDao = databaseHelper.getDao( Pad.class );
						QueryBuilder<Pad, Integer> builder = locationDao.queryBuilder();
						builder.groupBy( "location_id" );
						builder.orderBy( "name", true );

						pads = locationDao.query( builder.prepare() );
					}
					catch( SQLException e )
					{
						e.printStackTrace();
					}

					OpenHelperManager.releaseHelper();
				}
			}

			return pads;
		}

		@Override
		protected void onPostExecute( List<Pad> result )
		{
			Log.i( TAG, "Pad loaded." );
			m_pads = result;
			m_locationLookup.clear();

			m_locationsLoader = null;

			updateMap();
		}
	}

	private class LocationUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( Context context, Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( LocationUpdateTask.ACTION_LOCATION_LIST_UPDATED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Pad List update SUCCESS broadcast, will update the UI now." );

					reloadData();

					activity.setProgressBarIndeterminateVisibility( false );
					Crouton.makeText( activity, R.string.TOAST_location_list_update_complete, Style.CONFIRM ).show();
				}
				else if( LocationUpdateTask.ACTION_LOCATION_LIST_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Pad List update FAILURE broadcast." );

					Crouton.makeText( activity, R.string.TOAST_location_list_update_failed, Style.ALERT ).show();
					activity.setProgressBarIndeterminateVisibility( false );
				}
			}
		}
	}
}
