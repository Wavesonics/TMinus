package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Adam on 7/13/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class SettingsActivity extends Activity
{
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace( android.R.id.content, new SettingsFragment() )
				.commit();
	}
}