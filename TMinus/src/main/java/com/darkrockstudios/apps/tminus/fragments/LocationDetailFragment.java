package com.darkrockstudios.apps.tminus.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkrockstudios.apps.tminus.R;

/**
 * Created by Adam on 7/24/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LocationDetailFragment extends DialogFragment
{
	public static final String TAG         = LocationDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";

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

		}

		return rootView;
	}
}
