package com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Adam on 2/11/14.
 */
public class AgencyDetailFragment extends DialogFragment
{
	public static final String ARG_ITEM_ID = "item_id";

	@InjectView(R.id.AGENCYDETAIL_name)
	TextView m_name;

	private int m_agencyId;

	public static AgencyDetailFragment newInstance( final int agencyId )
	{
		Bundle args = new Bundle();
		args.putInt( ARG_ITEM_ID, agencyId );

		AgencyDetailFragment fragment = new AgencyDetailFragment();
		fragment.setArguments( args );

		return fragment;
	}

	@Override
	public void onAttach( final Activity activity )
	{
		super.onAttach( activity );
	}

	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Bundle args = getArguments();
		if( args != null )
		{
			m_agencyId = args.getInt( ARG_ITEM_ID, -1 );
		}
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState )
	{
		View rootView = inflater.inflate( R.layout.fragment_agency_detail, container, false );
		ButterKnife.inject( this, rootView );

		return rootView;
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		reloadData();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	private void reloadData()
	{
		Agency agency = getAgency();
		if( agency != null )
		{
			m_name.setText( agency.name );
		}
	}

	private Agency getAgency()
	{
		Agency agency = null;

		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
				agency = agencyDao.queryForId( m_agencyId );
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}
		}

		return agency;
	}
}
