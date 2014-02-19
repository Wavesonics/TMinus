package com.darkrockstudios.apps.tminus.experiences.rocket.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.DatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.AgencyDetailActivity;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.AgencyListDialog;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.darkrockstudios.apps.tminus.misc.UpNavUtil;

/**
 * Created by Adam on 7/14/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailActivity extends DatabaseActivity implements AgencyListDialog.AgencyListDialogClickListener
{
	private static final String FRAGMENT_TAG = "RocketDetailFragment";
	public static final  String ARG_ITEM_ID  = "item_id";
	private int m_rocketId;

	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_rocket_detail );

		m_rocketId = getRocketId();
		if( m_rocketId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				RocketDetailFragment rocketDetailFragment = RocketDetailFragment.newInstance( m_rocketId, false );
				getSupportFragmentManager().beginTransaction()
				                           .add( R.id.rocket_detail_container, rocketDetailFragment, FRAGMENT_TAG )
				                           .commit();
			}
		}
	}

	private int getRocketId()
	{
		int rocketId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			rocketId = intent.getIntExtra( RocketDetailActivity.ARG_ITEM_ID, -1 );
		}

		return rocketId;
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		getMenuInflater().inflate( R.menu.refresh, menu );
		getMenuInflater().inflate( R.menu.settings, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		final boolean handled;

		if( !CommonMenuHandler.onOptionsItemSelected( item, this ) )
		{
			switch( item.getItemId() )
			{
				case android.R.id.home:
					UpNavUtil.standardUp( this );
					handled = true;
					break;
				case R.id.action_refresh:
					refresh();
					handled = true;
					break;
				default:
					handled = super.onOptionsItemSelected( item );
					break;
			}
		}
		else
		{
			handled = true;
		}

		return handled;
	}

	private void refresh()
	{
		RocketDetailFragment fragment =
				(RocketDetailFragment) getSupportFragmentManager()
						                       .findFragmentByTag( FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.refresh();
		}
	}

	public void rocketImageClicked( final View v )
	{
		RocketDetailFragment fragment =
				(RocketDetailFragment) getSupportFragmentManager()
						                       .findFragmentByTag( FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}

	@Override
	public void onAgencyListDialogClick( final Agency agency )
	{
		Intent intent = new Intent( this, AgencyDetailActivity.class );
		intent.setData( TminusUri.buildAgencyUri( agency.id ) );
		startActivity( intent );
	}
}