package com.darkrockstudios.apps.tminus.experiences.launch.detail.viewholders;

import android.view.View;
import android.widget.ImageView;
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

	@InjectView(R.id.LAUNCHDETAIL_mission_type_name)
	public TextView m_typeNameView;

	@InjectView(R.id.LAUNCHDETAIL_mission_index)
	public TextView m_indexView;

	@InjectView(R.id.LAUNCHDETAIL_mission_type)
	public ImageView m_typeView;

	public MissionHeaderViewHolder( final View view )
	{
		ButterKnife.inject( this, view );
	}
}
