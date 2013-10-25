package com.darkrockstudios.apps.tminus.updatetasks;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Adam on 10/23/13.
 */
public class DataUpdaterService extends IntentService
{
	private static final String TAG = DataUpdaterService.class.getSimpleName();

	public static final String EXTRA_UPDATE_TYPE = DataUpdaterService.class.getPackage() + ".UPDATE_TYPE";

	public DataUpdaterService()
	{
		super( TAG );
	}

	@Override
	protected void onHandleIntent( Intent intent )
	{
		final UpdateTask updateTask;
		final String updateType = intent.getStringExtra( EXTRA_UPDATE_TYPE );
		if( updateType != null )
		{
			if( updateType.equals( RocketUpdateTask.UPDATE_TYPE ) )
			{
				updateTask = new RocketUpdateTask( this );
			}
			else if( updateType.equals( LocationUpdateTask.UPDATE_TYPE ) )
			{
				updateTask = new LocationUpdateTask( this );
			}
			else
			{
				updateTask = null;
			}

			if( updateTask != null )
			{
				updateTask.run();
			}
			else
			{
				Log.w( TAG, "No update task registered for: " + updateType );
			}
		}
		else
		{
			Log.w( TAG, "No update task type specified." );
		}
	}
}
