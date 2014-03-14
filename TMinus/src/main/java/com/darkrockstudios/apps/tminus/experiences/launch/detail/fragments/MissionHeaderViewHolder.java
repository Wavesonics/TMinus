package com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments;

import android.view.View;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adam on 3/13/14.
 */
public class MissionHeaderViewHolder
{
	@InjectView(R.id.LAUNCHDETAIL_mission_name)
	public TextView m_nameView;

	public MissionHeaderViewHolder( final View view )
	{
		ButterKnife.inject( this, view );
	}
}
