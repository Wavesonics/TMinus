package com.darkrockstudios.apps.tminus.base.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.AgencyBrowserActivity;
import com.darkrockstudios.apps.tminus.experiences.launch.browse.LaunchListActivity;
import com.darkrockstudios.apps.tminus.experiences.location.browse.LocationBrowserActivity;
import com.darkrockstudios.apps.tminus.experiences.rocket.browse.RocketBrowserActivity;
import com.darkrockstudios.apps.tminus.experiences.settings.SettingsActivity;

/**
 * Created by Adam on 10/6/13.
 * A convenience class for Activities need a Nav Drawer and
 * will be handling Database actions.
 */
public abstract class NavigationDatabaseActivity extends DatabaseActivity
{
	protected DrawerLayout          m_drawerLayout;
	protected ActionBarDrawerToggle m_drawerToggle;
	protected ListView              m_navigationList;

	protected void initNavDrawer()
	{
		m_drawerLayout = (DrawerLayout) findViewById( R.id.NAVDRAWER_drawer_layout );
		m_drawerToggle =
				new NavigationDrawerToggle( this, m_drawerLayout, R.drawable.ic_drawer, R.string.NAVDRAWER_open_description,
				                            R.string.NAVDRAWER_close_description );

		// Set the drawer toggle as the DrawerListener
		m_drawerLayout.setDrawerListener( m_drawerToggle );

		m_navigationList = (ListView) findViewById( R.id.NAVDRAWER_left_drawer );
		NavigationListAdapter navListAdapter = new NavigationListAdapter( this );
		m_navigationList.setOnItemClickListener( new NavigationItemClickListener() );
		m_navigationList.setAdapter( navListAdapter );

		ActionBar actionBar = getActionBar();
		if( actionBar != null )
		{
			actionBar.setDisplayHomeAsUpEnabled( true );
			actionBar.setHomeButtonEnabled( true );
		}
	}

	private class NavigationItemClickListener implements AdapterView.OnItemClickListener
	{
		@Override
		public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
		{
			final Intent intent;

			NavigationItem item = (NavigationItem) parent.getItemAtPosition( position );
			// Don't launch a new Activity if is the same as the current one
			if( (item != null) && (item.m_activityClass != NavigationDatabaseActivity.this.getClass()) )
			{
				intent = new Intent( NavigationDatabaseActivity.this, item.m_activityClass );
				intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
			}
			else
			{
				intent = null;
			}

			if( intent != null )
			{
				startActivity( intent );
				finish();
			}
			// If we aren't moving to a new Activity, close the drawer
			else
			{
				m_drawerLayout.closeDrawers();
			}
		}
	}

	private static class NavigationItem
	{
		public String   m_title;
		public Drawable m_iconResource;
		public Class    m_activityClass;

		public NavigationItem( final Context context, final Class activityClass, final int titleResourceId, final int iconResourceId )
		{
			m_activityClass = activityClass;
			m_title = context.getString( titleResourceId );
			m_iconResource = context.getResources().getDrawable( iconResourceId );
		}
	}

	private class NavigationListAdapter extends ArrayAdapter<NavigationItem>
	{
		public NavigationListAdapter( final Context context )
		{
			super( context, R.layout.row_navigation_item, R.id.NAVDRAWER_nav_item_title );

			NavigationItem launchListItem = new NavigationItem( context,
			                                                    LaunchListActivity.class,
			                                                    R.string.NAVDRAWER_item_title_launch_list,
			                                                    R.drawable.ic_navdrawer_launch_list );
			add( launchListItem );

			NavigationItem rocketListItem = new NavigationItem( context,
			                                                    RocketBrowserActivity.class,
			                                                    R.string.NAVDRAWER_item_title_rocket_list,
			                                                    R.drawable.ic_navdrawer_rocket_list );
			add( rocketListItem );

			NavigationItem locationListItem = new NavigationItem( context,
			                                                      LocationBrowserActivity.class,
			                                                      R.string.NAVDRAWER_item_title_location_list,
			                                                      R.drawable.ic_navdrawer_location_list );
			add( locationListItem );

			NavigationItem agencyListItem = new NavigationItem( context,
			                                                    AgencyBrowserActivity.class,
			                                                    R.string.NAVDRAWER_item_title_agency_list,
			                                                    R.drawable.ic_navdrawer_location_list );
			add( agencyListItem );
		}

		public View getView( final int position, final View convertView, final ViewGroup parent )
		{
			final View row;
			if( convertView == null )
			{
				row = getLayoutInflater().inflate( R.layout.row_navigation_item, parent, false );
			}
			else
			{
				row = convertView;
			}

			NavigationItem item = getItem( position );

			TextView titleView = (TextView) row.findViewById( R.id.NAVDRAWER_nav_item_title );
			titleView.setText( item.m_title );

			ImageView iconView = (ImageView) row.findViewById( R.id.NAVDRAWER_nav_item_icon );
			iconView.setImageDrawable( item.m_iconResource );

			return row;
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}
	}

	private class NavigationDrawerToggle extends ActionBarDrawerToggle
	{
		public NavigationDrawerToggle( final Activity activity, final DrawerLayout drawerLayout, final int drawerImageRes, final int openDrawerContentDescRes, final int closeDrawerContentDescRes )
		{
			super( activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes );
		}

		/**
		 * Called when a drawer has settled in a completely closed state.
		 */
		public void onDrawerClosed( final View view )
		{
			ActionBar actionBar = getActionBar();
			if( actionBar != null )
			{
				actionBar.setTitle( getTitle() );
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		}

		/**
		 * Called when a drawer has settled in a completely open state.
		 */
		public void onDrawerOpened( final View drawerView )
		{
			ActionBar actionBar = getActionBar();
			if( actionBar != null )
			{
				actionBar.setTitle( getTitle() );
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item )
	{
		boolean handled = m_drawerToggle.onOptionsItemSelected( item );
		if( !handled )
		{
			// Handle item selection
			switch( item.getItemId() )
			{
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
		}

		return handled;
	}

	@Override
	protected void onPostCreate( final Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		// Sync the toggle state after onRestoreInstanceState has occurred.
		m_drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged( final Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		m_drawerToggle.onConfigurationChanged( newConfig );
	}
}
