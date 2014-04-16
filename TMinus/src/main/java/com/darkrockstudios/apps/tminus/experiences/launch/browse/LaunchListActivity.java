package com.darkrockstudios.apps.tminus.experiences.launch.browse;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.activities.NavigationDatabaseActivity;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.AgencyBrowserActivity;
import com.darkrockstudios.apps.tminus.experiences.countdown.CountDownActivity;
import com.darkrockstudios.apps.tminus.experiences.launch.browse.fragments.LaunchListFragment;
import com.darkrockstudios.apps.tminus.experiences.launch.browse.fragments.LaunchListFragment.Callbacks;
import com.darkrockstudios.apps.tminus.experiences.launch.detail.LaunchDetailActivity;
import com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments.LaunchDetailFragment;
import com.darkrockstudios.apps.tminus.experiences.location.detail.fragments.LocationDetailFragment;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.AgencyListDialog;
import com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments.RocketDetailFragment;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.darkrockstudios.apps.tminus.launchlibrary.Pad;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.CommonMenuHandler;
import com.darkrockstudios.apps.tminus.misc.TminusUri;

public class LaunchListActivity extends NavigationDatabaseActivity
		implements Callbacks, ActionBar.OnNavigationListener, AgencyListDialog.AgencyListDialogClickListener
{
	private static final String TAG                            = LaunchListActivity.class.getSimpleName();
	private static final String TAG_LAUNCH_LIST                = "LaunchList";
	private static final String STATE_SELECTED_NAVIGATION_ITEM =
			LaunchListActivity.class.getPackage() + ".STATE_SELECTED_NAVIGATION_ITEM";

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean m_twoPane;

	private boolean m_navigationSpinnerInitialized;

	private Dialog m_betaDialog;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_common_list );
		setTitle( "" );

		m_navigationSpinnerInitialized = false;

		setupNavigationSpinner();

		setUpcomingLaunchesFragment();

		initNavDrawer();
	}

	private void setUpcomingLaunchesFragment()
	{
		FragmentManager fragmentManager = getFragmentManager();
		LaunchListFragment launchListFragment = LaunchListFragment.newInstance( false );
		fragmentManager.beginTransaction().replace( R.id.COMMON_list_fragment_container, launchListFragment,
		                                            TAG_LAUNCH_LIST ).commit();
	}

	private void setPreviousLaunchesFragment()
	{
		FragmentManager fragmentManager = getFragmentManager();
		LaunchListFragment launchListFragment = LaunchListFragment.newInstance( true );
		fragmentManager.beginTransaction().replace( R.id.COMMON_list_fragment_container, launchListFragment,
		                                            TAG_LAUNCH_LIST ).commit();
	}

	@Override
	public void onResume()
	{
		super.onResume();

		handleBetaDialog();

		if( findViewById( R.id.COMMON_detail_fragment_container ) != null )
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			m_twoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			LaunchListFragment launchListFragment =
					(LaunchListFragment) getFragmentManager().findFragmentByTag( TAG_LAUNCH_LIST );
			launchListFragment.setActivateOnItemClick( true );
		}
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if( m_betaDialog != null )
		{
			m_betaDialog.dismiss();
			m_betaDialog = null;
		}
	}

	private void handleBetaDialog()
	{
		final String BETA_KEY = "beta_dialog_shown";
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( this );
		if( !prefs.getBoolean( BETA_KEY, false ) )
		{
			displayBetaDialog();
			prefs.edit().putBoolean( BETA_KEY, true ).apply();
		}
	}

	private void displayBetaDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setTitle( "Beta!" );
		builder.setMessage( Html.fromHtml( "Welcome to the TMinus Beta!<br/>" +
		                                   "TMinus is in it's very early stages.<br/><br/>" +
		                                   "<b>Here's a few things to note:</b><br/>" +
		                                   "- The UI is temporary and being redesigned right now.<br/>" +
		                                   "- The data will improve over time<br/>" +
		                                   "<br/><br/>There are many features we are looking to add in the future, such as live launch info, and Chromecast support for the launch countdown. So stick with us!" ) );

		m_betaDialog = builder.create();
		m_betaDialog.show();
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
					refreshLaunchList();
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

	/**
	 * Callback method from {@link LaunchListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected( final Launch launch )
	{
		if( launch != null )
		{
			if( m_twoPane )
			{
				// In two-pane mode, show the detail view in this activity by
				// adding or replacing the detail fragment using a
				// fragment transaction.
				Bundle arguments = new Bundle();
				arguments.putInt( LaunchDetailFragment.ARG_ITEM_ID, launch.id );
				LaunchDetailFragment fragment = new LaunchDetailFragment();
				fragment.setArguments( arguments );
				getFragmentManager().beginTransaction()
				                    .replace( R.id.COMMON_detail_fragment_container, fragment )
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
	}

	@Override
	public void onRestoreInstanceState( final Bundle savedInstanceState )
	{
		final ActionBar actionBar = getActionBar();
		if( savedInstanceState.containsKey( STATE_SELECTED_NAVIGATION_ITEM ) && actionBar != null )
		{
			actionBar.setSelectedNavigationItem( savedInstanceState.getInt( STATE_SELECTED_NAVIGATION_ITEM ) );
			m_navigationSpinnerInitialized = true;
		}
	}

	@Override
	public void onSaveInstanceState( final Bundle outState )
	{
		final ActionBar actionBar = getActionBar();
		if( actionBar != null )
		{
			outState.putInt( STATE_SELECTED_NAVIGATION_ITEM, actionBar.getSelectedNavigationIndex() );
		}
	}

	private void setupNavigationSpinner()
	{
		ActionBar actionBar = getActionBar();
		if( actionBar != null )
		{
			actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );

			final String[] navigationValues = getResources().getStringArray( R.array.LAUNCHLIST_navigation_options );

			ArrayAdapter<String> adapter = new ArrayAdapter<>( actionBar.getThemedContext(),
			                                                   android.R.layout.simple_spinner_item, android.R.id.text1,
			                                                   navigationValues );
			adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

			actionBar.setListNavigationCallbacks( adapter, this );
		}
		else
		{
			Log.w( TAG, "Failed to setup navigation spinner: Could not get Actionbar." );
		}
	}

	private void refreshLaunchList()
	{
		LaunchListFragment launchListFragment = (LaunchListFragment) getFragmentManager()
				                                                             .findFragmentById( R.id.COMMON_list_fragment_container );
		launchListFragment.refresh();
	}

	public void countDownClicked( final View v )
	{
		LaunchDetailFragment launchDetailFragment = (LaunchDetailFragment) getFragmentManager()
				                                                                   .findFragmentById( R.id.COMMON_detail_fragment_container );
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

	public void rocketDetailsClicked( final View v )
	{
		Rocket rocket = (Rocket) v.getTag();

		RocketDetailFragment rocketDetailFragment = RocketDetailFragment.newInstance( rocket.id, true );
		rocketDetailFragment.show( getFragmentManager(), "dialog" );
	}

	public void locationDetailsClicked( final View v )
	{
		Pad pad = (Pad) v.getTag();

		LocationDetailFragment locationDetailFragment =
				LocationDetailFragment.newInstance( pad.location.id, pad.id, true, true );
		locationDetailFragment.show( getFragmentManager(), "dialog" );
	}

	public void rocketImageClicked( final View v )
	{
		LaunchDetailFragment fragment = (LaunchDetailFragment) getFragmentManager()
				                                                       .findFragmentById( R.id.COMMON_detail_fragment_container );

		if( fragment != null )
		{
			fragment.zoomRocketImage();
		}
	}

	@Override
	public boolean onNavigationItemSelected( final int itemPosition, final long itemId )
	{
		final boolean handled;

		if( m_navigationSpinnerInitialized )
		{
			switch( itemPosition )
			{
				case 0:
					Log.d( TAG, "Upcoming selected" );
					setUpcomingLaunchesFragment();
					handled = true;
					break;
				case 1:
					Log.d( TAG, "Previous selected" );
					setPreviousLaunchesFragment();
					handled = true;
					break;
				default:
					handled = false;
					break;
			}
		}
		else
		{
			handled = true;
			m_navigationSpinnerInitialized = true;
		}

		return handled;
	}

	@Override
	public void onAgencyListDialogClick( final Agency agency )
	{
		Intent intent = new Intent( this, AgencyBrowserActivity.class );
		intent.setData( TminusUri.buildAgencyUri( agency.id ) );
		startActivity( intent );
	}
}
