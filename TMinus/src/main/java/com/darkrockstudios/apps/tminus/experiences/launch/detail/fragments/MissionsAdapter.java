package com.darkrockstudios.apps.tminus.experiences.launch.detail.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.launchlibrary.Mission;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by adam on 3/13/14.
 */
public class MissionsAdapter extends BaseExpandableListAdapter
{
	private LayoutInflater m_inflater;

	private List<Mission> m_missions;

	public MissionsAdapter( Context context )
	{
		super();

		m_inflater = LayoutInflater.from( context );
		m_missions = new ArrayList<>();
	}

	public void addAll( final Collection<Mission> missions )
	{
		m_missions.addAll( missions );
	}

	public void add( final Mission mission )
	{
		m_missions.add( mission );
	}

	public void clear()
	{
		m_missions.clear();
	}

	@Override
	public int getGroupCount()
	{
		return m_missions.size();
	}

	@Override
	public int getChildrenCount( int groupPosition )
	{
		return 1;
	}

	@Override
	public Object getGroup( int groupPosition )
	{
		return m_missions.get( groupPosition );
	}

	@Override
	public Object getChild( int groupPosition, int childPosition )
	{
		return m_missions.get( groupPosition );
	}

	@Override
	public long getGroupId( int groupPosition )
	{
		return 0;
	}

	@Override
	public long getChildId( int groupPosition, int childPosition )
	{
		return 0;
	}

	@Override
	public boolean hasStableIds()
	{
		return false;
	}

	@Override
	public View getGroupView( int groupPosition, boolean isExpanded, View convertView, ViewGroup parent )
	{
		final View view;
		if( convertView == null )
		{
			view = m_inflater.inflate( R.layout.row_mission_list_header, parent, false );
			MissionHeaderViewHolder holder = new MissionHeaderViewHolder( view );
			view.setTag( holder );
		}
		else
		{
			view = convertView;
		}

		Mission mission = (Mission) getGroup( groupPosition );

		MissionHeaderViewHolder holder = (MissionHeaderViewHolder) view.getTag();
		holder.m_nameView.setText( mission.name );

		return view;
	}

	@Override
	public View getChildView( int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent )
	{
		final View view;
		if( convertView == null )
		{
			view = m_inflater.inflate( R.layout.row_mission_list_item, parent, false );
			MissionItemViewHolder holder = new MissionItemViewHolder( view );
			view.setTag( holder );
		}
		else
		{
			view = convertView;
		}

		Mission mission = (Mission) getChild( groupPosition, childPosition );

		MissionItemViewHolder holder = (MissionItemViewHolder) view.getTag();
		holder.m_descriptionView.setText( mission.description );

		return view;
	}

	@Override
	public boolean isChildSelectable( int groupPosition, int childPosition )
	{
		return false;
	}
}
