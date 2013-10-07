package com.darkrockstudios.apps.tminus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Adam on 10/6/13.
 * A convenience class for Activities need a Nav Drawer and
 * will be handling Database actions.
 */
public abstract class NavigationDatabaseActivity extends DatabaseActivity
{
	protected DrawerLayout          m_drawerLayout;
	protected ActionBarDrawerToggle m_drawerToggle;
	protected ListView m_navigationList;

	protected void initNavDrawer()
	{
		m_drawerLayout = (DrawerLayout) findViewById( R.id.NAVDRAWER_drawer_layout );
		m_drawerToggle =
				new NavigationDrawerToggle( this, m_drawerLayout, R.drawable.ic_drawer, R.string.NAVDRAWER_open_description,
				                            R.string.NAVDRAWER_close_description );

		// Set the drawer toggle as the DrawerListener
		m_drawerLayout.setDrawerListener( m_drawerToggle );

		m_navigationList = (ListView) findViewById( R.id.NAVDRAWER_left_drawer );
		m_navigationList.setAdapter( new NavigationListAdapter( this ) );

		ActionBar actionBar = getActionBar();
		if( actionBar != null )
		{
			actionBar.setDisplayHomeAsUpEnabled( true );
			actionBar.setHomeButtonEnabled( true );
		}
	}

	private class NavigationListAdapter extends ArrayAdapter<String>
	{
		public NavigationListAdapter( Context context )
		{
			super( context, R.layout.row_navigation_item, R.id.NAVDRAWER_nav_item_title );
		}

		@Override
		public int getCount()
		{
			return 0;
		}

		@Override
		public String getItem( int position )
		{
			return "";
		}

		@Override
		public int getPosition( String item )
		{
			return super.getPosition( item );
		}

		@Override
		public boolean hasStableIds()
		{
			return true;
		}
	}

	private class NavigationDrawerToggle extends ActionBarDrawerToggle
	{
		public NavigationDrawerToggle( Activity activity, DrawerLayout drawerLayout, int drawerImageRes, int openDrawerContentDescRes, int closeDrawerContentDescRes )
		{
			super( activity, drawerLayout, drawerImageRes, openDrawerContentDescRes, closeDrawerContentDescRes );
		}

		/**
		 * Called when a drawer has settled in a completely closed state.
		 */
		public void onDrawerClosed( View view )
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
		public void onDrawerOpened( View drawerView )
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
	public boolean onOptionsItemSelected( MenuItem item )
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
	protected void onPostCreate( Bundle savedInstanceState )
	{
		super.onPostCreate( savedInstanceState );
		// Sync the toggle state after onRestoreInstanceState has occurred.
		m_drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig )
	{
		super.onConfigurationChanged( newConfig );
		m_drawerToggle.onConfigurationChanged( newConfig );
	}
}
