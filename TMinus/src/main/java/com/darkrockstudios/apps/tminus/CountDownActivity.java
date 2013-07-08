package com.darkrockstudios.apps.tminus;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Menu;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R.id;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class CountDownActivity extends Activity
{
	private static final String TAG            = CountDownActivity.class.getSimpleName();
	public static final  String ARG_ITEM_ID    = "item_id";
	private static final long   INTERVAL_IN_MS = 10;
	private long      m_endTime;
	private boolean   m_launched;
	private TextView  m_timerView;
	private TextView  m_statusView;
	private Handler   m_handler;
	private TickTimer m_timeTicker;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_count_down );

		m_handler = new Handler();
		m_timeTicker = new TickTimer();

		final Typeface typeface = Typeface.createFromAsset( getAssets(), "fonts/digital_7_mono.ttf" );
		m_timerView = (TextView)findViewById( R.id.COUNTDOWN_timer );
		m_timerView.setTypeface( typeface );

		m_statusView = (TextView)findViewById( id.COUNTDOWN_launch_status );
		m_statusView.setTypeface( typeface );

		m_endTime = SystemClock.uptimeMillis() + 10000;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		updateTimer();
		startTimer();
	}

	@Override
	public void onStop()
	{
		super.onStop();

		stopTimer();
	}

	private void updateStatus()
	{
		//String.format( getString( string.COUNTDOWN_launch_status ),  )
		//m_statusView.setText(  );
	}

	private void updateTimer()
	{
		final long millis = m_endTime - SystemClock.uptimeMillis();

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
		m_handler.postDelayed( m_timeTicker, INTERVAL_IN_MS );
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
