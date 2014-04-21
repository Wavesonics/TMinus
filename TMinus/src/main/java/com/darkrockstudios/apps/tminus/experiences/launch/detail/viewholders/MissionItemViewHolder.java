package com.darkrockstudios.apps.tminus.experiences.launch.detail.viewholders;

import android.view.View;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by adam on 3/13/14.
 */
public class MissionItemViewHolder
{
	@InjectView(R.id.LAUNCHDETAIL_mission_description)
	public TextView m_descriptionView;

	public MissionItemViewHolder( final View view )
	{
		ButterKnife.inject( this, view );
	}
}
