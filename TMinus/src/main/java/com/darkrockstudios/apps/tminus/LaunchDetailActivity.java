package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class LaunchDetailActivity extends FragmentActivity
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_launch_detail );


		if( savedInstanceState == null )
		{
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable( LaunchDetailFragment.ARG_ITEM_ID,
			                           getIntent().getSerializableExtra( LaunchDetailFragment.ARG_ITEM_ID ) );
			LaunchDetailFragment fragment = new LaunchDetailFragment();
			fragment.setArguments( arguments );
			getSupportFragmentManager().beginTransaction()
					.add( R.id.launch_detail_container, fragment )
					.commit();
		}
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch( item.getItemId() )
		{
			case android.R.id.home:
				NavUtils.navigateUpTo( this, new Intent( this, LaunchListActivity.class ) );
				return true;
		}
		return super.onOptionsItemSelected( item );
	}
}
