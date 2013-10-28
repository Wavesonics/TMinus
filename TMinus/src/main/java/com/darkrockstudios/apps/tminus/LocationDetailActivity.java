package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LocationDetailFragment;

/**
 * Created by Adam on 7/24/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LocationDetailActivity extends DatabaseActivity
{
	private static final String FRAGMENT_TAG      = "LocationDetailFragment";
	public static final  String EXTRA_LOCATION_ID = "location_id";
	public static final  String EXTRA_PAD_ID      = "pad_id";

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_location_detail );

		int locationId = getLocationId();
		int padId = getPadId();
		if( locationId >= 0 || padId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				LocationDetailFragment locationDetailFragment =
						LocationDetailFragment.newInstance( locationId, padId, true, false );
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
			locationId = intent.getIntExtra( LocationDetailActivity.EXTRA_LOCATION_ID, -1 );
		}

		return locationId;
	}

	private int getPadId()
	{
		int padId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			padId = intent.getIntExtra( LocationDetailActivity.EXTRA_PAD_ID, -1 );
		}

		return padId;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.settings, menu );

		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		final boolean handled;

		// Handle item selection
		switch( item.getItemId() )
		{
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask( this );
				handled = true;
				break;
			case R.id.action_settings:
			{
				Intent intent = new Intent( this, SettingsActivity.class );
				startActivity( intent );
				handled = true;
			}
			break;
			default:
				handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}
}