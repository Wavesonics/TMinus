package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LocationDetailFragment;

/**
 * Created by Adam on 7/24/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LocationDetailActivity extends DatabaseActivity
{
	private static final String FRAGMENT_TAG = "LocationDetailFragment";
	public static final  String ARG_ITEM_ID  = "item_id";
	private int m_locationId;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_location_detail );

		m_locationId = getLocationId();
		if( m_locationId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				LocationDetailFragment locationDetailFragment = LocationDetailFragment.newInstance( m_locationId, true );
				getSupportFragmentManager().beginTransaction()
						.add( R.id.location_detail_container, locationDetailFragment, FRAGMENT_TAG )
						.commit();
			}
		}
	}

	private int getLocationId()
	{
		int locationId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			locationId = intent.getIntExtra( LocationDetailActivity.ARG_ITEM_ID, -1 );
		}

		return locationId;
	}
}