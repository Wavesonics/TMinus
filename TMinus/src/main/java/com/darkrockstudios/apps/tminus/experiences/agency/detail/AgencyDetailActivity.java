package com.darkrockstudios.apps.tminus.experiences.agency.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.DatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments.AgencyDetailFragment;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.darkrockstudios.apps.tminus.misc.UpNavUtil;

/**
 * Created by Adam on 2/11/14.
 */
public class AgencyDetailActivity extends DatabaseActivity
{
	private static final String FRAGMENT_TAG = "AgencyDetailFragment";

	private int m_agencyId;

	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_agency_detail );

		m_agencyId = getAgencyId();
		if( m_agencyId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				AgencyDetailFragment rocketDetailFragment = AgencyDetailFragment.newInstance( m_agencyId );
				getFragmentManager().beginTransaction()
				                    .add( R.id.agency_detail_container, rocketDetailFragment, FRAGMENT_TAG )
				                    .commit();
			}
		}
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

	private int getAgencyId()
	{
		int agencyId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			agencyId = TminusUri.extractAgencyId( intent.getData() );
		}

		return agencyId;
	}

	private void refresh()
	{
		AgencyDetailFragment fragment =
				(AgencyDetailFragment) getFragmentManager()
						                       .findFragmentByTag( FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.refresh();
		}
	}

	public void agencyImageClicked( final View view )
	{
		AgencyDetailFragment fragment =
				(AgencyDetailFragment) getFragmentManager()
						                       .findFragmentByTag( FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.zoomAgencyImage();
		}
	}
}
