package com.darkrockstudios.apps.tminus.experiences.agency.browse.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
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
import com.darkrockstudios.apps.tminus.dataupdate.DataUpdaterService;
import com.darkrockstudios.apps.tminus.experiences.agency.browse.dataupdate.AgencyUpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.j256.ormlite.dao.Dao;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 2/10/14.
 */
public class AgencyBrowserFragment extends BaseBrowserFragment implements AdapterView.OnItemClickListener
{
	private static final String TAG = AgencyBrowserFragment.class.getSimpleName();

	private static final long UPDATE_THRESHOLD = TimeUnit.DAYS.toMillis( 30 );

	private AgencyUpdateReceiver m_updateReceiver;

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

		m_updateReceiver = new AgencyUpdateReceiver();

		IntentFilter m_updateIntentFilter = new IntentFilter();
		m_updateIntentFilter.addAction( AgencyUpdateTask.ACTION_AGENCY_LIST_UPDATED );
		m_updateIntentFilter.addAction( AgencyUpdateTask.ACTION_AGENCY_LIST_UPDATE_FAILED );
		activity.registerReceiver( m_updateReceiver, m_updateIntentFilter );
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

		boolean shouldRefresh = !reloadData();

		final SharedPreferences preferences = PreferenceManager
				                                      .getDefaultSharedPreferences( getActivity() );
		if( preferences.contains( Preferences.KEY_LAST_AGENCY_LIST_UPDATE ) )
		{
			long lastUpdated = preferences.getLong( Preferences.KEY_LAST_AGENCY_LIST_UPDATE, -1 );
			if( DateTime.now().isAfter( new DateTime( lastUpdated ).plus( UPDATE_THRESHOLD ) ) )
			{
				shouldRefresh = true;
			}
		}
		else
		{
			shouldRefresh = true;
		}

		if( shouldRefresh )
		{
			refresh();
		}
	}

	private boolean reloadData()
	{
		boolean dataLoaded = false;

		m_adapter.clear();

		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<Agency, Integer> agencyDao = databaseHelper.getDao( Agency.class );
				List<Agency> agencies = agencyDao.query( agencyDao.queryBuilder().orderBy( "name", true ).prepare() );

				m_adapter.addAll( agencies );
				m_adapter.notifyDataSetChanged();
				dataLoaded = true;
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

		return dataLoaded;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		m_callbacks = null;

		Activity activity = getActivity();
		if( activity != null && m_updateReceiver != null )
		{
			activity.unregisterReceiver( m_updateReceiver );
			m_updateReceiver = null;
		}
	}

	public void refresh()
	{
		requestAgencies();
	}

	private void requestAgencies()
	{
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			Log.d( TAG, "Requesting agencies..." );

			activity.setProgressBarIndeterminateVisibility( true );

			Intent updateIntent = new Intent( activity, DataUpdaterService.class );
			updateIntent.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, AgencyUpdateTask.UPDATE_TYPE );
			activity.startService( updateIntent );
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

	private class AgencyUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( AgencyUpdateTask.ACTION_AGENCY_LIST_UPDATED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Agency List update SUCCESS broadcast, will update the UI now." );

					reloadData();

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_agency_list_update_complete, Style.CONFIRM ).show();
				}
				else if( AgencyUpdateTask.ACTION_AGENCY_LIST_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Agency List update FAILURE broadcast." );

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_agency_list_update_failed, Style.ALERT ).show();
				}
			}
		}
	}
}
