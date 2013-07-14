package com.darkrockstudios.apps.tminus;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R.id;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class CountDownActivity extends Activity implements LaunchLoader.Listener
{
	private static final String TAG            = CountDownActivity.class.getSimpleName();
	private static final String FONT_PATH      = "fonts/digital_7_mono.ttf";
	public static final  String ARG_ITEM_ID    = "item_id";
	private static final long   INTERVAL_IN_MS = 10;
	private long      m_endTime;
	private boolean   m_launched;
	private TextView  m_timerView;
	private TextView  m_statusView;
	private Handler   m_handler;
	private TickTimer m_timeTicker;

	private Launch m_launch;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		getWindow().addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
		setContentView( R.layout.activity_count_down );

		m_handler = new Handler();
		m_timeTicker = new TickTimer();

		final Typeface typeface = Typeface.createFromAsset( getAssets(), FONT_PATH );
		m_timerView = (TextView)findViewById( R.id.COUNTDOWN_timer );
		m_timerView.setTypeface( typeface );

		m_statusView = (TextView)findViewById( id.COUNTDOWN_launch_status );
		m_statusView.setTypeface( typeface );

		loadLaunch();
	}

	@Override
	public void onStart()
	{
		super.onStart();

		if( m_launch != null )
		{
			updateTimer();
			startTimer();
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();

		stopTimer();
	}

	private void loadLaunch()
	{
		final Intent intent = getIntent();
		if( intent != null )
		{
			final int launchId = intent.getIntExtra( ARG_ITEM_ID, -1 );
			if( launchId > 0 )
			{
				LaunchLoader launchLoader = new LaunchLoader( this, this );
				launchLoader.execute( launchId );
			}
		}
	}

	private void updateStatus()
	{
		if( m_launch != null )
		{
			final String status;
			switch( m_launch.status )
			{
				case 1:
					status = getString( R.string.COUNTDOWN_launch_status_green );
					break;
				case 2:
					status = getString( R.string.COUNTDOWN_launch_status_red );
					break;
				case 3:
					status = getString( R.string.COUNTDOWN_launch_status_success );
					break;
				case 4:
					status = getString( R.string.COUNTDOWN_launch_status_fail );
					break;
				default:
					status = "";
			}

			final String statusText = String.format( getString( R.string.COUNTDOWN_launch_status ), status );
			m_statusView.setText( statusText );
		}
	}

	private void updateTimer()
	{
		final long millis = m_endTime - System.currentTimeMillis();

		final long hr = TimeUnit.MILLISECONDS.toHours( millis );

		final long min = TimeUnit.MILLISECONDS.toMinutes( millis - TimeUnit.HOURS
		                                                                   .toMillis( hr ) );

		final long sec = TimeUnit.MILLISECONDS.toSeconds( millis - TimeUnit.HOURS
		                                                                   .toMillis( hr ) - TimeUnit.MINUTES
		                                                                                             .toMillis( min ) );

		final long centisec = (millis - TimeUnit.HOURS
		                                        .toMillis( hr ) - TimeUnit.MINUTES
		                                                                  .toMillis( min ) - TimeUnit.SECONDS
		                                                                                             .toMillis( sec )) / 10;

		DecimalFormat twoDigit = new DecimalFormat( "00" );
		twoDigit.setNegativePrefix( "" );

		char sign = '-';
		if( millis < 0 )
		{
			sign = '+';

			if( !m_launched )
			{
				m_launched = true;
				blinkTimer();
			}
		}

		m_timerView
				.setText( sign + twoDigit.format( hr ) + ":" + twoDigit.format( min ) + ":" + twoDigit.format( sec ) + ":" + twoDigit.format( centisec ) );
	}

	private void blinkTimer()
	{
		ValueAnimator blinkAnim = ObjectAnimator.ofFloat( m_timerView, "alpha", 1.0f, 0.0f, 1.0f );
		blinkAnim.setDuration( 400 );
		blinkAnim.setRepeatCount( 24 );
		blinkAnim.start();
	}

	private void startTimer()
	{
		m_handler.removeCallbacks( m_timeTicker );
		m_handler.postDelayed( m_timeTicker, 0 );
	}

	private void stopTimer()
	{
		m_handler.removeCallbacks( m_timeTicker );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		getMenuInflater().inflate( R.menu.count_down, menu );
		return true;
	}

	@Override
	public void launchLoaded( Launch launch )
	{
		m_launch = launch;

		m_endTime = m_launch.windowstart.getTime();
		updateStatus();
		updateTimer();

		startTimer();
	}

	private class TickTimer implements Runnable
	{
		@Override
		public void run()
		{
			updateTimer();
			m_handler.postDelayed( this, INTERVAL_IN_MS );
		}
	}
}
