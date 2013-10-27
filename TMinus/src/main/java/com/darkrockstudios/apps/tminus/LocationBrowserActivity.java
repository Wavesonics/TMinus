package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LocationBrowserFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;

/**
 * Created by Adam on 10/13/13.
 */
public class LocationBrowserActivity extends NavigationDatabaseActivity implements LocationBrowserFragment.LocationClickListener
{
	private static final String FRAGMENT_TAG = "LocationBrowser";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_location_browser );

		initNavDrawer();
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.settings, menu );
		inflater.inflate( R.menu.refresh, menu );

		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		final boolean handled;

		// Handle item selection
		switch( item.getItemId() )
		{
			case R.id.action_refresh:
				refreshLocationList();
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

	@Override
	public void onLocationClicked( Pad pad )
	{
		if( pad != null )
		{
			Intent intent = new Intent( this, LocationDetailActivity.class );
			intent.putExtra( LocationDetailActivity.EXTRA_ITEM_ID, pad.id );
			startActivity( intent );
		}
	}

	private void refreshLocationList()
	{
		LocationBrowserFragment fragment =
				(LocationBrowserFragment) getSupportFragmentManager().findFragmentByTag( FRAGMENT_TAG );
		fragment.refresh();
	}
}
