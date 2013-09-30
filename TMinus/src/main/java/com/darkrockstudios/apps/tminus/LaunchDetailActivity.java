package com.darkrockstudios.apps.tminus;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LaunchDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;

public class LaunchDetailActivity extends DatabaseActivity
{
	private static final String FRAGMENT_TAG = "LaunchDetailFragment";
	private int m_launchId;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_launch_detail );

		m_launchId = getLaunchId();
		if( m_launchId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				LaunchDetailFragment fragment = LaunchDetailFragment.newInstance( m_launchId );
				getSupportFragmentManager().beginTransaction()
						.add( R.id.launch_detail_container, fragment, FRAGMENT_TAG )
						.commit();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.settings, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		boolean handled;

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

	private int getLaunchId()
	{
		int launchId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			launchId = intent.getIntExtra( LaunchDetailFragment.ARG_ITEM_ID, -1 );
		}

		return launchId;
	}

	public void countDownClicked( View v )
	{
		if( m_launchId >= 0 )
		{
			Intent countDownIntent = new Intent( this, CountDownActivity.class );
			countDownIntent.putExtra( CountDownActivity.ARG_ITEM_ID, m_launchId );
			startActivity( countDownIntent );
		}
	}

	public void rocketDetailsClicked( View v )
	{
		Rocket rocket = (Rocket) v.getTag();

		final Context context = v.getContext();
		Intent intent = new Intent( context, RocketDetailActivity.class );
		intent.putExtra( RocketDetailActivity.ARG_ITEM_ID, rocket.id );
		context.startActivity( intent );
	}

	public void locationDetailsClicked( View v )
	{
		Location location = (Location) v.getTag();

		final Context context = v.getContext();
		Intent intent = new Intent( context, LocationDetailActivity.class );
		intent.putExtra( RocketDetailActivity.ARG_ITEM_ID, location.id );
		context.startActivity( intent );
	}

	public void rocketImageClicked( View v )
	{
		LaunchDetailFragment fragment =
				(LaunchDetailFragment) getSupportFragmentManager()
						.findFragmentByTag( FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}
}
