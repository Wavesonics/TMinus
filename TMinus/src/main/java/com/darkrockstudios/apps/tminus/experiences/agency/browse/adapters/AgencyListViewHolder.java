package com.darkrockstudios.apps.tminus.experiences.agency.browse.adapters;

import android.view.View;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Adam on 2/18/14.
 */
public class AgencyListViewHolder
{
	@InjectView(R.id.AGENCYLIST_abbreviation)
	TextView m_abbreviation;

	@InjectView(R.id.AGENCYLIST_name)
	TextView m_name;

	public AgencyListViewHolder( final View view )
	{
		ButterKnife.inject( this, view );
	}
}
