package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LocationBrowserFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;

/**
 * Created by Adam on 10/13/13.
 */
public class LocationBrowserActivity extends NavigationDatabaseActivity implements LocationBrowserFragment.LocationClickListener
{
	public LocationBrowserActivity()
	{
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_location_browser );

		initNavDrawer();
	}

	@Override
	public void onLocationClicked( Location location )
	{
		if( location != null )
		{
			Intent intent = new Intent( this, LocationDetailActivity.class );
			intent.putExtra( LocationDetailActivity.EXTRA_ITEM_ID, location.id );
		}
	}
}
