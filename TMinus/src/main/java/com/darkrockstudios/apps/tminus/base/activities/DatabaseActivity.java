package com.darkrockstudios.apps.tminus.base.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

/**
 * Created by Adam on 6/29/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public abstract class DatabaseActivity extends FragmentActivity
{
	private DatabaseHelper m_databaseHelper = null;

	@Override
	protected void onCreate( Bundle savedInstance )
	{
		super.onCreate( savedInstance );

		// Create and hold onto a DB helper for the life of this Activity
		getHelper();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if( m_databaseHelper != null )
		{
			OpenHelperManager.releaseHelper();
			m_databaseHelper = null;
		}
	}

	public DatabaseHelper getHelper()
	{
		if( m_databaseHelper == null )
		{
			m_databaseHelper = OpenHelperManager.getHelper( this, DatabaseHelper.class );
		}
		return m_databaseHelper;
	}
}
