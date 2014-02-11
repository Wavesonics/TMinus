package com.darkrockstudios.apps.tminus.experiences.agency.browse;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.NavigationDatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.fragments.AgencyBrowserFragment;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;

/**
 * Created by Adam on 2/10/14.
 */
public class AgencyBrowserActivity extends NavigationDatabaseActivity
{
	private static final String FRAGMENT_TAG = "LocationBrowser";

	private boolean m_twoPane;

	@Override
	protected void onCreate( Bundle savedInstanceState )
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
}
