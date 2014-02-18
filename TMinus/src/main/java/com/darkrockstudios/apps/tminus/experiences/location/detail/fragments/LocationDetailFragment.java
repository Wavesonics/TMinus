package com.darkrockstudios.apps.tminus.experiences.location.detail.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.misc.FlagResourceUtility;
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
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 7/24/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LocationDetailFragment extends DialogFragment implements AdapterView.OnItemClickListener
{
	public static final String TAG =
			LocationDetailFragment.class.getSimpleName();

	public static final  int    ARG_UNSET_ID            = -1;
	public static final  String ARG_LOCATION_ID         = "location_id";
	public static final  String ARG_PAD_ID              = "pad_id";
	public static final  String ARG_MAP_CONTROL_ENABLED = "map_control_enabled";
	public static final  String ARG_ONLY_DISPLAY_MAP    = "only_display_map";
	private static final String MAP_FRAGMENT_TAG        = "MapFragment";
	private static final float  PAD_ZOOM                = 16.0f;

	private static final LatLng DEFAULT_LOCATION = new LatLng( 37.523506, -77.412109 );
	private Location       m_location;
	private List<Pad>      m_pads;
	private FrameLayout    m_mapContainer;
	private TextView       m_locationName;
	private PadListAdapter m_listAdapter;

	private static final String UNKNOWN_PAD_NAME = "Unknown Pad";

	public LocationDetailFragment()
	{
	}

	public static LocationDetailFragment newInstance( int locationId, int padId, boolean mapControlsEnabled, boolean onlyDisplayMap )
	{
		LocationDetailFragment locationDetailFragment = new LocationDetailFragment();

		Bundle arguments = new Bundle();
		if( locationId > ARG_UNSET_ID )
		{
			arguments.putInt( ARG_LOCATION_ID, locationId );
		}

		if( padId > ARG_UNSET_ID )
		{
			arguments.putInt( ARG_PAD_ID, padId );
		}
		arguments.putBoolean( ARG_MAP_CONTROL_ENABLED, mapControlsEnabled );
		arguments.putBoolean( ARG_ONLY_DISPLAY_MAP, onlyDisplayMap );

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

		final View rootView;
		if( shouldOnlyDisplayMap() )
		{
			rootView = inflater.inflate( R.layout.fragment_location_detail_map_only, container, false );
		}
		else
		{
			rootView = inflater.inflate( R.layout.fragment_location_detail, container, false );
		}

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

			if( !shouldOnlyDisplayMap() )
			{
				m_listAdapter = new PadListAdapter( getActivity() );
				ListView padList =
						(ListView) rootView.findViewById( R.id.LOCATIONDETAIL_pad_list );
				padList.setAdapter( m_listAdapter );
				padList.setOnItemClickListener( this );
			}
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

				// Don't add pad markers in map only map
				if( m_pads != null && !m_pads.isEmpty() && !shouldOnlyDisplayMap() )
				{
					for( Pad pad : m_pads )
					{
						if( pad.latitude != null && pad.longitude != null )
						{
							LatLng padPos = new LatLng( pad.latitude, pad.longitude );

							MarkerOptions marker = new MarkerOptions();
							marker.position( padPos );
							map.addMarker( marker );
						}
					}
				}
			}
		}
	}

	private void zoomToPad( Pad pad )
	{
		if( pad != null && pad.latitude != null && pad.longitude != null )
		{
			LatLng pos = new LatLng( pad.latitude, pad.longitude );

			SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
					                                                      .findFragmentByTag( MAP_FRAGMENT_TAG );
			if( mapFragment != null )
			{
				GoogleMap map = mapFragment.getMap();
				if( map != null )
				{
					CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom( pos, PAD_ZOOM );
					map.animateCamera( cameraUpdate );
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
		if( m_location != null )
		{
			updateMap();

			if( m_locationName != null )
			{
				m_locationName.setText( m_location.name );

				Drawable flagDrawable = FlagResourceUtility.getFlagDrawable( m_location.countryCode, getActivity() );
				m_locationName.setCompoundDrawablesWithIntrinsicBounds( flagDrawable, null, null, null );
			}

			if( m_pads != null )
			{

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

	private void loadPads()
	{
		if( m_location != null )
		{
			PadLoader padLoader = new PadLoader();
			padLoader.execute( m_location.id );
		}
	}

	private LatLng getLocation()
	{
		final LatLng pos;

		if( m_pads != null && !m_pads.isEmpty() )
		{
			Pad pad = m_pads.get( 0 );
			if( pad != null && pad.longitude != null && pad.latitude != null )
			{
				pos = new LatLng( pad.latitude, pad.longitude );
			}
			else
			{
				pos = DEFAULT_LOCATION;
			}
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

	private boolean shouldOnlyDisplayMap()
	{
		boolean onlyDisplayMap = false;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_ONLY_DISPLAY_MAP ) )
		{
			onlyDisplayMap = arguments.getBoolean( ARG_ONLY_DISPLAY_MAP );
		}

		return onlyDisplayMap;
	}

	public int getLocationId()
	{
		int locationId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_LOCATION_ID ) )
		{
			locationId = arguments.getInt( ARG_LOCATION_ID );
		}

		return locationId;
	}

	public int getPadId()
	{
		int padId = -1;

		final Bundle arguments = getArguments();
		if( arguments != null && arguments.containsKey( ARG_PAD_ID ) )
		{
			padId = arguments.getInt( ARG_PAD_ID );
		}

		return padId;
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View view, int position, long id )
	{
		final Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			if( m_pads != null && !m_pads.isEmpty() && position < m_pads.size() )
			{
				Pad pad = m_pads.get( position );

				if( pad.latitude != null && pad.longitude != null )
				{
					zoomToPad( pad );
				}
				else
				{
					Crouton.makeText( activity, R.string.TOAST_location_detail_pad_no_coordinates, Style.INFO ).show();
				}
			}
		}
	}

	private class PadListAdapter extends ArrayAdapter<Pad>
	{
		public PadListAdapter( Context context )
		{
			super( context, R.layout.row_pad_list_item );
		}

		public View getView( int position, View convertView, ViewGroup parent )
		{
			final View view;
			if( convertView == null )
			{
				LayoutInflater inflater = LayoutInflater.from( getContext() );
				view = inflater.inflate( R.layout.row_pad_list_item, parent, false );
			}
			else
			{
				view = convertView;
			}

			Pad pad = getItem( position );

			TextView padName = (TextView) view.findViewById( R.id.LOCATIONDETAIL_pad_list_item_name );
			padName.setText( pad.name );

			return view;
		}
	}

	private class PadLoader extends AsyncTask<Integer, Void, List<Pad>>
	{
		@Override
		protected List<Pad> doInBackground( Integer... ids )
		{
			final int locationId = ids[ 0 ];

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
						Dao<Pad, Integer> padDao = databaseHelper.getDao( Pad.class );
						QueryBuilder<Pad, Integer> queryBuilder = padDao.queryBuilder();
						queryBuilder.where().eq( "location_id", locationId );

						pads = padDao.query( queryBuilder.prepare() );
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
			Log.i( TAG, "Pads loaded." );

			if( result != null )
			{
				for( int ii = 0; ii < result.size(); ++ii )
				{
					Pad pad = result.get( ii );
					if( UNKNOWN_PAD_NAME.equalsIgnoreCase( pad.name.trim() ) )
					{
						result.remove( ii );
						break;
					}
				}
			}

			m_pads = result;

			if( m_listAdapter != null )
			{
				m_listAdapter.clear();
				m_listAdapter.addAll( m_pads );
				m_listAdapter.notifyDataSetChanged();
			}

			updateViews();
		}
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
				final DatabaseHelper databaseHelper =
						OpenHelperManager.getHelper( activity, DatabaseHelper.class );
				if( databaseHelper != null )
				{
					try
					{
						Dao<Location, Integer> locationDao = databaseHelper.getDao( Location.class );
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
			loadPads();

			updateViews();
		}
	}
}
