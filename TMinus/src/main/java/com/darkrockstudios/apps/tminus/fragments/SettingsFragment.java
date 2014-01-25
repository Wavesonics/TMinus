package com.darkrockstudios.apps.tminus.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.UpdateAlarmsService;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * Created by Adam on 7/13/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
	private boolean m_dirty;

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		addPreferencesFromResource( R.xml.general_preferences );
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
		preferences.registerOnSharedPreferenceChangeListener( this );

	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		Activity activity = getActivity();
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences( activity );
		preferences.unregisterOnSharedPreferenceChangeListener( this );

		if( m_dirty )
		{
			ResetAlarms resetAlarms = new ResetAlarms( activity );
			resetAlarms.start();

			m_dirty = false;
		}
	}

	@Override
	public void onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key )
	{
		m_dirty = true;
	}

	private class ResetAlarms extends Thread
	{
		private final Context m_context;

		public ResetAlarms( Context context )
		{
			m_context = context;
		}

		@Override
		public void run()
		{
			cancelAllAlarms();
			setNewAlarms();
		}

		private void cancelAllAlarms()
		{
			UpdateAlarmsService.cancelAutoUpdateAlarm( m_context );

			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( m_context, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				try
				{
					final Dao<Launch, Integer> launchDao = databaseHelper.getDao( Launch.class );
					for( final Launch launch : launchDao )
					{
						UpdateAlarmsService.cancelAlarmsForLaunch( launch, m_context );
					}
				}
				catch( SQLException e )
				{
					e.printStackTrace();
				}

				OpenHelperManager.releaseHelper();
			}
		}

		private void setNewAlarms()
		{
			Intent intent = new Intent( m_context, UpdateAlarmsService.class );
			m_context.startService( intent );
		}
	}
}