package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.RocketBrowserFragment;
import com.darkrockstudios.apps.tminus.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;

import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshAttacher;

/**
 * Created by Adam on 10/13/13.
 */
public class RocketBrowserActivity extends NavigationDatabaseActivity implements RocketBrowserFragment.Callbacks, PullToRefreshProvider
{
	private static final String TAG_ROCKET_LIST = "RocketList";

	private boolean               m_twoPane;
	private PullToRefreshAttacher m_pullToRefreshAttacher;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_common_list );

		m_pullToRefreshAttacher = PullToRefreshAttacher.get( this );

		FragmentManager fragmentManager = getSupportFragmentManager();

		RocketBrowserFragment rocketBrowserFragment = RocketBrowserFragment.newInstance();
		fragmentManager.beginTransaction()
		               .replace( R.id.COMMON_list_fragment_container, rocketBrowserFragment, TAG_ROCKET_LIST )
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
			RocketBrowserFragment rocketBrowserFragment =
					(RocketBrowserFragment) getSupportFragmentManager().findFragmentByTag( TAG_ROCKET_LIST );
			rocketBrowserFragment.setActivateOnItemClick( true );
		}
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
				refreshRocketList();
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

	private void refreshRocketList()
	{
		RocketBrowserFragment fragment = (RocketBrowserFragment) getSupportFragmentManager()
				                                                         .findFragmentById( R.id.COMMON_list_fragment_container );
		fragment.refresh();
	}

	public void rocketImageClicked( View v )
	{
		RocketDetailFragment fragment = (RocketDetailFragment) getSupportFragmentManager()
				                                                       .findFragmentById( R.id.COMMON_detail_fragment_container );

		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}

	@Override
	public void onItemSelected( Rocket rocket )
	{
		if( m_twoPane )
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			RocketDetailFragment fragment = RocketDetailFragment.newInstance( rocket.id, false );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.COMMON_detail_fragment_container, fragment )
					.commit();
		}
		else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent( this, RocketDetailActivity.class );
			detailIntent.putExtra( RocketDetailActivity.ARG_ITEM_ID, rocket.id );
			startActivity( detailIntent );
		}
	}

	@Override
	public PullToRefreshAttacher getPullToRefreshAttacher()
	{
		return m_pullToRefreshAttacher;
	}
}
