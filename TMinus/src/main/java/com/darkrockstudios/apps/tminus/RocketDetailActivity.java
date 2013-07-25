package com.darkrockstudios.apps.tminus;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.darkrockstudios.apps.tminus.fragments.RocketDetailFragment;

/**
 * Created by Adam on 7/14/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class RocketDetailActivity extends DatabaseActivity
{
	private static final String FRAGMENT_TAG = "RocketDetailFragment";
	public static final  String ARG_ITEM_ID  = "item_id";
	private int m_rocketId;

	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		requestWindowFeature( Window.FEATURE_INDETERMINATE_PROGRESS );
		setContentView( R.layout.activity_rocket_detail );

		m_rocketId = getRocketId();
		if( m_rocketId >= 0 )
		{
			if( savedInstanceState == null )
			{
				// Create the detail fragment and add it to the activity
				// using a fragment transaction.
				RocketDetailFragment rocketDetailFragment = RocketDetailFragment.newInstance( m_rocketId );
				getSupportFragmentManager().beginTransaction()
						.add( R.id.rocket_detail_container, rocketDetailFragment, FRAGMENT_TAG )
						.commit();
			}
		}
	}

	private int getRocketId()
	{
		int rocketId = -1;

		final Intent intent = getIntent();
		if( intent != null )
		{
			rocketId = intent.getIntExtra( RocketDetailActivity.ARG_ITEM_ID, -1 );
		}

		return rocketId;
	}
}