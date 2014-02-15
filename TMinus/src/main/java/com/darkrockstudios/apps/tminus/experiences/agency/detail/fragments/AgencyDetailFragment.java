package com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.AgencyDetail;
import com.darkrockstudios.apps.tminus.dataupdate.DataUpdaterService;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.dataupdate.AgencyDetailUpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 2/11/14.
 */
public class AgencyDetailFragment extends DialogFragment
{
	private static final String TAG = AgencyDetailFragment.class.getSimpleName();
	public static final String ARG_ITEM_ID = "item_id";

	@InjectView(R.id.AGENCYDETAIL_name)
	TextView m_name;

	private int m_agencyId;
	private Agency       m_agency;
	private AgencyDetail m_agencyDetail;

	private AgencyDetailUpdateReceiver m_updateReceiver;

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

		m_updateReceiver = new AgencyDetailUpdateReceiver();

		IntentFilter filter = new IntentFilter();
		filter.addAction( AgencyDetailUpdateTask.ACTION_AGENCY_DETAILS_UPDATED );
		filter.addAction( AgencyDetailUpdateTask.ACTION_AGENCY_DETAILS_UPDATE_FAILED );
		filter.addDataScheme( TminusUri.SCHEME );
		activity.registerReceiver( m_updateReceiver, filter );
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

	@Override
	public void onDetach()
	{
		super.onDetach();

		Activity activity = getActivity();
		if( m_updateReceiver != null && activity != null )
		{
			activity.unregisterReceiver( m_updateReceiver );
			m_updateReceiver = null;
		}
	}

	private void reloadData()
	{
		m_agency = getAgency();
		if( m_agency != null )
		{
			m_agencyDetail = getAgencyDetail( m_agency );
			if( m_agencyDetail == null )
			{
				requestAgencyDetails();
			}
			m_name.setText( m_agency.name );
		}
	}

	private void requestAgencyDetails()
	{
		Activity activity = getActivity();
		if( m_agency != null && activity != null && isAdded() )
		{
			activity.setProgressBarIndeterminateVisibility( true );

			Intent intent = new Intent( activity, DataUpdaterService.class );
			intent.setData( TminusUri.buildAgencyUri( m_agency.id ) );
			intent.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, AgencyDetailUpdateTask.UPDATE_TYPE );

			activity.startService( intent );
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

	private AgencyDetail getAgencyDetail( final Agency agency )
	{
		AgencyDetail agencyDetail = null;

		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<AgencyDetail, Integer> agencyDetailDao = databaseHelper.getDao( AgencyDetail.class );
				agencyDetail = agencyDetailDao.queryForId( m_agencyId );
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}
		}

		return agencyDetail;
	}

	private class AgencyDetailUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( AgencyDetailUpdateTask.ACTION_AGENCY_DETAILS_UPDATED.equals( intent.getAction() ) )
				{
					Log.i( TAG, "Received Agency Detail update SUCCESS broadcast, will update the UI now." );

					final int agencyId = TminusUri.extractAgencyId( intent.getData() );
					if( agencyId > 0 )
					{
						Log.i( TAG, "Agency Detail fetch completely successfully for rocket id: " + agencyId );


						activity.setProgressBarIndeterminateVisibility( false );
						Crouton.makeText( activity, R.string.TOAST_agency_detail_update_complete, Style.CONFIRM )
						       .show();
					}
				}
				else if( AgencyDetailUpdateTask.ACTION_AGENCY_DETAILS_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.w( TAG, "Received Agency Detail update FAILURE broadcast." );

					final int agencyId = TminusUri.extractAgencyId( intent.getData() );
					if( agencyId > 0 )
					{
						Log.w( TAG, "Agency Detail fetch completely failed for agency id: " + agencyId );
					}

					//m_rocketSummary.setText( R.string.AGENCYDETAIL_no_summary );

					Crouton.makeText( activity, R.string.TOAST_agency_detail_update_failed, Style.ALERT ).show();
					activity.setProgressBarIndeterminateVisibility( false );
				}
			}
		}
	}
}
