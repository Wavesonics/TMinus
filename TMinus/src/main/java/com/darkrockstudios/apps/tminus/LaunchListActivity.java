package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.LaunchDetailFragment;
import com.darkrockstudios.apps.tminus.fragments.LaunchListFragment;
import com.darkrockstudios.apps.tminus.fragments.LaunchListFragment.Callbacks;
import com.darkrockstudios.apps.tminus.fragments.LocationDetailFragment;
import com.darkrockstudios.apps.tminus.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Location;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;


/**
 * An activity representing a list of Launches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link LaunchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link com.darkrockstudios.apps.tminus.fragments.LaunchListFragment} and the item details
 * (if present) is a {@link com.darkrockstudios.apps.tminus.fragments.LaunchDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link com.darkrockstudios.apps.tminus.fragments.LaunchListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class LaunchListActivity extends DatabaseActivity
		implements Callbacks
{
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_launch_list );

		if( findViewById( R.id.launch_detail_container ) != null )
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((LaunchListFragment) getSupportFragmentManager()
					                      .findFragmentById( R.id.launch_list ))
					.setActivateOnItemClick( true );
		}

		// TODO: If exposing deep links into your app, handle intents here.
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
				refreshLaunchList();
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

	/**
	 * Callback method from {@link LaunchListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected( Launch launch )
	{
		if( m_twoPane )
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable( LaunchDetailFragment.ARG_ITEM_ID, launch.id );
			LaunchDetailFragment fragment = new LaunchDetailFragment();
			fragment.setArguments( arguments );
			getSupportFragmentManager().beginTransaction()
					.replace( R.id.launch_detail_container, fragment )
					.commit();
		}
		else
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent( this, LaunchDetailActivity.class );
			detailIntent.putExtra( LaunchDetailFragment.ARG_ITEM_ID, launch.id );
			startActivity( detailIntent );
		}
	}

	private void refreshLaunchList()
	{
		LaunchListFragment launchListFragment = (LaunchListFragment) getSupportFragmentManager()
				                                                             .findFragmentById( R.id.launch_list );
		launchListFragment.refresh();
	}

	public void countDownClicked( View v )
	{
		LaunchDetailFragment launchDetailFragment = (LaunchDetailFragment) getSupportFragmentManager()
				                                                                   .findFragmentById( R.id.launch_detail_container );
		if( launchDetailFragment != null )
		{
			final int launchId = launchDetailFragment.getLaunchId();
			if( launchId >= 0 )
			{
				Intent intent = new Intent( this, CountDownActivity.class );
				intent.putExtra( CountDownActivity.ARG_ITEM_ID, launchId );
				startActivity( intent );
			}
		}
	}

	public void rocketDetailsClicked( View v )
	{
		Rocket rocket = (Rocket) v.getTag();

		RocketDetailFragment rocketDetailFragment = RocketDetailFragment.newInstance( rocket.id );
		rocketDetailFragment.show( getSupportFragmentManager(), "dialog" );
	}

	public void locationDetailsClicked( View v )
	{
		Location location = (Location) v.getTag();

		LocationDetailFragment locationDetailFragment = LocationDetailFragment.newInstance( location.id, true );
		locationDetailFragment.show( getSupportFragmentManager(), "dialog" );
	}

	public void rocketImageClicked( View v )
	{
		LaunchDetailFragment fragment = (LaunchDetailFragment) getSupportFragmentManager()
				                                                       .findFragmentById( R.id.launch_detail_container );

		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}
}
