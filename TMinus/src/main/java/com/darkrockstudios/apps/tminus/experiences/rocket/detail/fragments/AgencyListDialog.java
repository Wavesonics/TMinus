package com.darkrockstudios.apps.tminus.experiences.rocket.detail.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.ListDialog;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.adapters.AgencyListAdapter;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adam on 2/18/14.
 */
public class AgencyListDialog extends ListDialog implements AdapterView.OnItemClickListener
{
	private static final String ARG_AGENCIES = AgencyListDialog.class.getPackage() + ".AGENCIES";

	private int[]             m_agencyIds;
	private AgencyListAdapter m_adapter;

	private AgencyListDialogClickListener m_listener;

	public static interface AgencyListDialogClickListener
	{
		public void onAgencyListDialogClick( final Agency agency );
	}

	public static AgencyListDialog newInstance( final List<Agency> agencies )
	{
		AgencyListDialog fragment = new AgencyListDialog();

		int[] agencyIds = new int[ agencies.size() ];
		for( int ii = 0; ii < agencies.size(); ++ii )
		{
			agencyIds[ ii ] = agencies.get( ii ).id;
		}

		Bundle bundle = new Bundle();
		bundle.putIntArray( ARG_AGENCIES, agencyIds );
		fragment.setArguments( bundle );

		return fragment;
	}

	@Override
	public void onAttach( final Activity activity )
	{
		super.onAttach( activity );

		if( activity instanceof AgencyListDialogClickListener )
		{
			m_listener = (AgencyListDialogClickListener) activity;
		}
	}

	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_adapter = new AgencyListAdapter( getActivity() );

		Bundle args = getArguments();
		if( args != null )
		{
			m_agencyIds = args.getIntArray( ARG_AGENCIES );
			m_adapter.addAll( getAgencies() );
		}
	}

	private List<Agency> getAgencies()
	{
		List<Agency> agencies = new ArrayList<>( m_agencyIds.length );

		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );

				for( final int id : m_agencyIds )
				{
					agencies.add( agencyDao.queryForId( id ) );
				}
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}
			finally
			{
				databaseHelper.close();
			}
		}

		return agencies;
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		m_listView.setAdapter( m_adapter );
		m_listView.setOnItemClickListener( this );

		getDialog().setTitle( R.string.AGENCYLISTDIALOG_title );
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		m_listener = null;
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		Agency agency = m_adapter.getItem( position );
		if( m_listener != null )
		{
			m_listener.onAgencyListDialogClick( agency );
		}
	}
}
