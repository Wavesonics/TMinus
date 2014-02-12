package com.darkrockstudios.apps.tminus.experiences.agency.browse.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.fragments.BaseBrowserFragment;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Adam on 2/10/14.
 */
public class AgencyBrowserFragment extends BaseBrowserFragment implements AdapterView.OnItemClickListener
{
	private static final String ARG_AGENCY_ID = "AgencyId";

	public interface Callbacks
	{
		public void onItemSelected( Agency agency );
	}

	private Callbacks         m_callbacks;
	private AgencyListAdapter m_adapter;

	public static AgencyBrowserFragment newInstance()
	{
		return new AgencyBrowserFragment();
	}

	@Override
	public void onAttach( final Activity activity )
	{
		super.onAttach( activity );

		if( activity instanceof Callbacks )
		{
			m_callbacks = (Callbacks) activity;
		}
	}

	@Override
	public View onCreateView( final LayoutInflater inflater,
	                          final ViewGroup container,
	                          final Bundle savedInstanceState )
	{
		View view = super.onCreateView( inflater, container, savedInstanceState );

		m_adapter = new AgencyListAdapter( getActivity() );
		setListAdapter( m_adapter );

		return view;
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		ListView listView = getListView();
		listView.setOnItemClickListener( this );
	}

	@Override
	public void onResume()
	{
		super.onResume();

		refresh();
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		m_callbacks = null;
	}

	public void refresh()
	{
		m_adapter.clear();

		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
				List<Agency> agencies = agencyDao.queryForAll();
				m_adapter.addAll( agencies );
				m_adapter.notifyDataSetChanged();
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
	}

	@Override
	public void onItemClick( final AdapterView<?> parent, final View view, final int position, final long id )
	{
		if( m_callbacks != null )
		{
			Agency agency = m_adapter.getItem( position );
			m_callbacks.onItemSelected( agency );
		}
	}

	static class ViewHolder
	{
		@InjectView(R.id.AGENCYLIST_name)
		TextView m_name;

		public ViewHolder( final View view )
		{
			ButterKnife.inject( this, view );
		}
	}

	private static class AgencyListAdapter extends ArrayAdapter<Agency>
	{
		public AgencyListAdapter( final Context context )
		{
			super( context, 0 );
		}

		@Override
		public View getView( final int pos, final View convertView, final ViewGroup parent )
		{
			final View view;
			if( convertView != null )
			{
				view = convertView;
			}
			else
			{
				LayoutInflater inflater = LayoutInflater.from( getContext() );
				view = inflater.inflate( R.layout.row_agency_list_item, parent, false );

				ViewHolder viewHolder = new ViewHolder( view );
				view.setTag( viewHolder );
			}

			ViewHolder viewHolder = (ViewHolder) view.getTag();

			Agency agency = getItem( pos );
			viewHolder.m_name.setText( agency.name );

			return view;
		}
	}
}
