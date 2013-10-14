package com.darkrockstudios.apps.tminus;

import android.os.Bundle;
import android.view.Window;

/**
 * Created by Adam on 10/13/13.
 */
public class LocationBrowserActivity extends NavigationDatabaseActivity
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
}
