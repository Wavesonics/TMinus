package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.fragments.RocketListFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;

/**
 * Created by Adam on 10/13/13.
 */
public class RocketListActivity extends NavigationDatabaseActivity implements RocketListFragment.Callbacks
{
	private static final String TAG_ROCKET_LIST = "RocketList";

	private boolean m_twoPane;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_common_list );

		FragmentManager fragmentManager = getSupportFragmentManager();

		RocketListFragment rocketListFragment = RocketListFragment.newInstance();
		fragmentManager.beginTransaction().replace( R.id.COMMON_list_fragment_container, rocketListFragment, TAG_ROCKET_LIST )
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
			RocketListFragment rocketListFragment =
					(RocketListFragment) getSupportFragmentManager().findFragmentByTag( TAG_ROCKET_LIST );
			rocketListFragment.setActivateOnItemClick( true );
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
		RocketListFragment launchListFragment = (RocketListFragment) getSupportFragmentManager()
				                                                             .findFragmentById( R.id.COMMON_list_fragment_container );
		launchListFragment.refresh();
	}

	@Override
	public void onItemSelected( Rocket rocket )
	{
		if( m_twoPane )
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable( RocketDetailFragment.ARG_ITEM_ID, rocket.id );
			RocketDetailFragment fragment = new RocketDetailFragment();
			fragment.setArguments( arguments );
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
}
