package com.darkrockstudios.apps.tminus.experiences.agency.browse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.NavigationDatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.fragments.AgencyBrowserFragment;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.AgencyDetailActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments.AgencyDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;
import com.darkrockstudios.apps.tminus.misc.TminusUri;

/**
 * Created by Adam on 2/10/14.
 */
public class AgencyBrowserActivity extends NavigationDatabaseActivity implements AgencyBrowserFragment.Callbacks
{
	private static final String FRAGMENT_TAG        = "AgencyBrowser";
	private static final String DETAIL_FRAGMENT_TAG = "AgencyDetail";

	private boolean m_twoPane;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_common_list );


		FragmentManager fragmentManager = getSupportFragmentManager();

		AgencyBrowserFragment agencyBrowserFragment = AgencyBrowserFragment.newInstance();
		fragmentManager.beginTransaction()
		               .replace( R.id.COMMON_list_fragment_container, agencyBrowserFragment, FRAGMENT_TAG )
		               .commit();

		initNavDrawer();
	}

	private int getAgencyDetailId()
	{
		final int agencyId;

		Intent intent = getIntent();
		if( intent != null && intent.getData() != null )
		{
			agencyId = TminusUri.extractAgencyId( intent.getData() );
		}
		else
		{
			agencyId = -1;
		}

		return agencyId;
	}

	@Override
	public void onResume()
	{
		super.onResume();

		if( findViewById( R.id.COMMON_detail_fragment_container ) != null )
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			AgencyBrowserFragment agencyBrowserFragment =
					(AgencyBrowserFragment) getSupportFragmentManager().findFragmentByTag( FRAGMENT_TAG );
			agencyBrowserFragment.setActivateOnItemClick( true );
		}

		final int agencyId = getAgencyDetailId();
		if( agencyId >= 0 )
		{
			selectAgency( agencyId );
		}
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu )
	{
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate( R.menu.settings, menu );
		inflater.inflate( R.menu.refresh, menu );

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
				case R.id.action_refresh:
					refreshAgencyList();
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

	private void refreshAgencyList()
	{
		AgencyBrowserFragment fragment =
				(AgencyBrowserFragment) getSupportFragmentManager().findFragmentByTag( FRAGMENT_TAG );
		fragment.refresh();
	}

	public void agencyImageClicked( final View view )
	{
		AgencyDetailFragment fragment =
				(AgencyDetailFragment) getSupportFragmentManager()
						                       .findFragmentByTag( DETAIL_FRAGMENT_TAG );
		if( fragment != null )
		{
			fragment.zoomAgencyImage();
		}
	}

	@Override
	public void onItemSelected( final Agency agency )
	{
		selectAgency( agency.id );
	}

	private void selectAgency( final int agencyId )
	{
		if( m_twoPane )
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			AgencyDetailFragment fragment = AgencyDetailFragment.newInstance( agencyId );
			getSupportFragmentManager().beginTransaction()
			                           .replace( R.id.COMMON_detail_fragment_container, fragment, DETAIL_FRAGMENT_TAG )
			                           .commit();
		}
		else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent intent = new Intent( this, AgencyDetailActivity.class );
			intent.setData( TminusUri.buildAgencyUri( agencyId ) );
			startActivity( intent );
		}
	}
}
