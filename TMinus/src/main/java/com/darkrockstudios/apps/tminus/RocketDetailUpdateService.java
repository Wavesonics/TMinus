package com.darkrockstudios.apps.tminus;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailFetcher;
import com.darkrockstudios.apps.tminus.loaders.RocketDetailFetcher.RocketDetailFetchListener;
import com.darkrockstudios.apps.tminus.loaders.RocketLoader;
import com.darkrockstudios.apps.tminus.loaders.RocketLoader.RocketLoadListener;

import java.util.Vector;

/**
 * Created by Adam on 7/28/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailUpdateService extends Service implements RocketDetailFetchListener, RocketLoadListener
{
	public static final  String ACTION_ROCKET_DETAIL_UPDATE_FAILED = "com.darkrockstudios.apps.tminus.ROCKET_DETAIL_UPDATE_FAILED";
	public static final  String ACTION_ROCKET_DETAIL_UPDATED       = "com.darkrockstudios.apps.tminus.ROCKET_DETAIL_UPDATED";
	public static final  String EXTRA_ROCKET_ID                    = "rocket_id";
	private static final String TAG                                = RocketDetailUpdateService.class.getSimpleName();
	private Vector<RocketId> m_inFlightUpdates;

	@Override
	public void onCreate()
	{
		m_inFlightUpdates = new Vector<RocketId>();
	}

	@Override
	public void onDestroy()
	{
		// Fail any in flight updates since it appears we are being killed
		for( RocketId id : m_inFlightUpdates )
		{
			sendFailureBroadcast( id.getValue() );
		}

		Log.d( TAG, "RocketDetailUpdateService finished." );
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		Log.d( TAG, "RocketDetailUpdateService started." );

		if( intent != null )
		{
			int rocketId = intent.getIntExtra( EXTRA_ROCKET_ID, -1 );
			if( rocketId > 0 )
			{
				final RocketId rocketidObj = new RocketId( rocketId );
				// If we don't already have an update in flight for this, kick it off
				if( !m_inFlightUpdates.contains( rocketidObj ) )
				{
					Log.d( TAG, "Starting Rocket Detail update for rocket: " + rocketId );
					m_inFlightUpdates.add( rocketidObj );
					RocketLoader rocketLoader = new RocketLoader( this, this );
					rocketLoader.execute( rocketId );
				}
				else
				{
					Log.d( TAG, "Rocket Detail update already in flight for rocket: " + rocketId );
				}
			}
		}

		return START_NOT_STICKY;
	}

	public IBinder onBind( Intent intent )
	{
		return null;
	}

	@Override
	public void rocketDetailFetchSuccessful( int rocketId )
	{
		sendSuccessBroadcast( rocketId );
	}

	@Override
	public void rocketDetailFetchFailed( int rocketId )
	{
		sendFailureBroadcast( rocketId );
	}

	@Override
	public void rocketLoaded( Rocket rocket )
	{
		RocketDetailFetcher.requestRocketDetails( rocket, this, this );
	}

	@Override
	public void rocketLoadFailed( int rocketId )
	{
		sendFailureBroadcast( rocketId );
	}

	private void sendFailureBroadcast( int rocketId )
	{
		m_inFlightUpdates.remove( new RocketId( rocketId ) );

		if( m_inFlightUpdates.size() == 0 )
		{
			stopSelf();
		}

		Intent failureIntent = new Intent( ACTION_ROCKET_DETAIL_UPDATE_FAILED );
		failureIntent.putExtra( EXTRA_ROCKET_ID, rocketId );

		sendBroadcast( failureIntent );
	}

	private void sendSuccessBroadcast( int rocketId )
	{
		m_inFlightUpdates.remove( new RocketId( rocketId ) );

		if( m_inFlightUpdates.size() == 0 )
		{
			stopSelf();
		}

		Intent failureIntent = new Intent( ACTION_ROCKET_DETAIL_UPDATED );
		failureIntent.putExtra( EXTRA_ROCKET_ID, rocketId );

		sendBroadcast( failureIntent );
	}

	private static class RocketId
	{
		private final int m_value;

		public RocketId( int id )
		{
			m_value = id;
		}

		public int getValue()
		{
			return m_value;
		}

		@Override
		public boolean equals( Object that )
		{
			if( this == that )
				return true;
			if( !(that instanceof RocketId) )
				return false;

			final RocketId thatId = (RocketId)that;
			return getValue() == thatId.getValue();
		}
	}
}
