package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

public class LaunchDetailActivity extends DatabaseActivity
{
	private static String FRAGMENT_TAG = "LaunchDetailFragment";

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_launch_detail );

		final Intent intent = getIntent();
		if( intent != null )
		{
			final int launchId = intent.getIntExtra( LaunchDetailFragment.ARG_ITEM_ID, -1 );
			if( launchId > 0 )
			{
				if( savedInstanceState == null )
				{
					// Create the detail fragment and add it to the activity
					// using a fragment transaction.
					Bundle arguments = new Bundle();
					arguments.putInt( LaunchDetailFragment.ARG_ITEM_ID, launchId );
					LaunchDetailFragment fragment = new LaunchDetailFragment();
					fragment.setArguments( arguments );
					getSupportFragmentManager().beginTransaction()
							.add( R.id.launch_detail_container, fragment, FRAGMENT_TAG )
							.commit();
				}
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
				NavUtils.navigateUpTo( this, new Intent( this, LaunchListActivity.class ) );
				handled = true;
				break;
			case R.id.action_settings:
				handled = true;
				break;
			default:
				handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}

	public void countDownClicked( View v )
	{
		Intent intent = new Intent( this, CountDownActivity.class );
		startActivity( intent );
	}
}
