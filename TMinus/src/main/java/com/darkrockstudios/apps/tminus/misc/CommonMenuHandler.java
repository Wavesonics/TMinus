package com.darkrockstudios.apps.tminus.misc;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.experiences.about.AboutFragment;
import com.darkrockstudios.apps.tminus.experiences.settings.SettingsActivity;

/**
 * Created by Adam on 2/9/14.
 */
public class CommonMenuHandler
{
	public static boolean onOptionsItemSelected( final MenuItem item, final Activity activity )
	{
		final boolean handled;

		switch( item.getItemId() )
		{
			case R.id.action_settings:
			{
				Intent intent = new Intent( activity, SettingsActivity.class );
				activity.startActivity( intent );
				handled = true;
			}
			break;
			case R.id.action_about:
			{
				AboutFragment aboutFragment = AboutFragment.newInstance();
				aboutFragment.show( activity.getFragmentManager(), AboutFragment.FRAGMENT_TAG );
				handled = true;
			}
			break;
			default:
				handled = false;
				break;
		}

		return handled;
	}
}
