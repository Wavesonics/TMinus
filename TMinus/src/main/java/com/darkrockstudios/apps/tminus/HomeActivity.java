package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.darkrockstudios.apps.tminus.R.id;

public class HomeActivity extends Activity
{
	private static final String TAG = HomeActivity.class.getSimpleName();
	private RequestQueue m_requestQueue;
	private TextView     m_testText;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_home );

		m_requestQueue = Volley.newRequestQueue( this );

		LaunchListFragment launchListFragment = (LaunchListFragment)getFragmentManager()
				                                                            .findFragmentById( R.id.launch_list_fragment );
		launchListFragment.setRequestQueue( m_requestQueue );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate( R.menu.home, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		final boolean handled;
		// Handle item selection
		switch( item.getItemId() )
		{
			case id.action_refresh:
				refreshLaunchList();
				handled = true;
				break;
			case id.action_settings:
				handled = true;
				break;
			default:
				handled = super.onOptionsItemSelected( item );
		}

		return handled;
	}

	private void refreshLaunchList()
	{
		LaunchListFragment launchListFragment = (LaunchListFragment)getFragmentManager()
				                                                            .findFragmentById( R.id.launch_list_fragment );
		launchListFragment.refresh();
	}
}
