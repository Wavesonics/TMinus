package com.darkrockstudios.apps.tminus.experiences.rocket.browse;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.NavigationDatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.rocket.browse.fragments.RocketBrowserFragment;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.RocketDetailActivity;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;

/**
 * Created by Adam on 10/13/13.
 */
public class RocketBrowserActivity extends NavigationDatabaseActivity implements RocketBrowserFragment.Callbacks
{
	private static final String TAG_ROCKET_LIST = "RocketList";

	private boolean m_twoPane;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_common_list );

		FragmentManager fragmentManager = getFragmentManager();

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
					(RocketBrowserFragment) getFragmentManager().findFragmentByTag( TAG_ROCKET_LIST );
			rocketBrowserFragment.setActivateOnItemClick( true );
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
					refreshRocketList();
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

	private void refreshRocketList()
	{
		RocketBrowserFragment fragment = (RocketBrowserFragment) getFragmentManager()
				                                                         .findFragmentById( R.id.COMMON_list_fragment_container );
		fragment.refresh();
	}

	public void rocketImageClicked( final View v )
	{
		RocketDetailFragment fragment = (RocketDetailFragment) getFragmentManager()
				                                                       .findFragmentById( R.id.COMMON_detail_fragment_container );

		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}

	@Override
	public void onItemSelected( final Rocket rocket )
	{
		if( m_twoPane )
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			RocketDetailFragment fragment = RocketDetailFragment.newInstance( rocket.id, false );
			getFragmentManager().beginTransaction()
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
