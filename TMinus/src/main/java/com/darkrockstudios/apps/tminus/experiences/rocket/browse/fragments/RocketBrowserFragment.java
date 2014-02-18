package com.darkrockstudios.apps.tminus.experiences.rocket.browse.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.fragments.BaseBrowserFragment;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.dataupdate.DataUpdaterService;
import com.darkrockstudios.apps.tminus.experiences.rocket.browse.dataupdate.RocketUpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.darkrockstudios.apps.tminus.misc.FlagResourceUtility;
import com.darkrockstudios.apps.tminus.misc.Preferences;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 10/13/13.
 */
public class RocketBrowserFragment extends BaseBrowserFragment
{
	private static final String TAG = RocketBrowserFragment.class.getSimpleName();

	private RocketUpdateReceiver m_updateReceiver;

	private ArrayAdapter<Rocket> m_adapter;
	private static final long UPDATE_THRESHOLD = TimeUnit.DAYS.toMillis( 7 );

	public interface Callbacks
	{
		public void onItemSelected( Rocket rocket );
	}

	private Callbacks m_callbacks;

	public static RocketBrowserFragment newInstance()
	{
		RocketBrowserFragment fragment = new RocketBrowserFragment();

		return fragment;
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container,
	                          final Bundle savedInstanceState )
	{
		View view = super.onCreateView( inflater, container, savedInstanceState );

		m_adapter = new RocketListAdapter( getActivity() );
		setListAdapter( m_adapter );

		return view;
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		boolean shouldRefresh = !reloadData();

		final SharedPreferences preferences = PreferenceManager
				                                      .getDefaultSharedPreferences( getActivity() );
		if( preferences.contains( Preferences.KEY_LAST_ROCKET_LIST_UPDATE ) )
		{
			long lastUpdated = preferences.getLong( Preferences.KEY_LAST_ROCKET_LIST_UPDATE, -1 );
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

	@Override
	public void onAttach( final Activity activity )
	{
		super.onAttach( activity );

		// Activities containing this fragment must implement its callbacks.
		if( !(activity instanceof Callbacks) )
		{
			throw new IllegalStateException( "Activity must implement fragment's callbacks." );
		}
		else
		{
			m_callbacks = (Callbacks) activity;
		}

		m_updateReceiver = new RocketUpdateReceiver();

		IntentFilter m_updateIntentFilter = new IntentFilter();
		m_updateIntentFilter.addAction( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATED );
		m_updateIntentFilter.addAction( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATE_FAILED );
		activity.registerReceiver( m_updateReceiver, m_updateIntentFilter );
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

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	@Override
	public void onListItemClick( final ListView listView, final View view, final int position, final long id )
	{
		if( m_callbacks != null )
		{
			// Notify the active callbacks interface (the activity, if the
			// fragment is attached to one) that an item has been selected.
			Rocket rocket = (Rocket) listView.getAdapter().getItem( position );
			m_callbacks.onItemSelected( rocket );
		}
	}

	private boolean reloadData()
	{
		boolean dataLoaded = false;

		final Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( activity, DatabaseHelper.class );
			if( databaseHelper != null )
			{
				m_adapter.clear();

				try
				{
					Dao<Rocket, Integer> rocketDao = databaseHelper.getDao( Rocket.class );

					List<Rocket> results = rocketDao.query( rocketDao.queryBuilder().orderBy( "name", true ).prepare() );

					for( final Rocket rocket : results )
					{
						rocket.refreshFamily( databaseHelper );
					}

					if( results != null && results.size() > 0 )
					{
						m_adapter.addAll( results );
						dataLoaded = true;
					}
				}
				catch( final SQLException e )
				{
					e.printStackTrace();
				}

				OpenHelperManager.releaseHelper();
			}
		}

		return dataLoaded;
	}

	public void refresh()
	{
		requestRockets();
	}

	private void requestRockets()
	{
		Activity activity = getActivity();
		if( activity != null && isAdded() )
		{
			Log.d( TAG, "Requesting rockets..." );

			activity.setProgressBarIndeterminateVisibility( true );

			Intent rocketUpdate = new Intent( activity, DataUpdaterService.class );
			rocketUpdate.putExtra( DataUpdaterService.EXTRA_UPDATE_TYPE, RocketUpdateTask.UPDATE_TYPE );
			activity.startService( rocketUpdate );
		}
	}

	static class ViewHolder
	{
		@InjectView(R.id.ROCKETLISTITEM_rocket_name)
		TextView rocketNameView;

		@InjectView(R.id.ROCKETLISTITEM_rocket_configuration)
		TextView rocketConfigurationView;

		@InjectView(R.id.ROCKETLISTITEM_rocket_agencies)
		TextView rocketAgenciesView;

		public ViewHolder( final View view )
		{
			ButterKnife.inject( this, view );
		}
	}

	private static class RocketListAdapter extends ArrayAdapter<Rocket>
	{
		public RocketListAdapter( final Context context )
		{
			super( context, R.layout.row_rocket_list_item );
		}

		@Override
		public View getView( final int pos, final View convertView, final ViewGroup parent )
		{
			final Context context = getContext();

			final View view;
			if( convertView != null )
			{
				view = convertView;
			}
			else
			{
				LayoutInflater inflater = LayoutInflater.from( getContext() );
				view = inflater.inflate( R.layout.row_rocket_list_item, parent, false );

				ViewHolder viewHolder = new ViewHolder( view );
				view.setTag( viewHolder );
			}

			ViewHolder viewHolder = (ViewHolder) view.getTag();

			Rocket rocket = getItem( pos );

			viewHolder.rocketNameView.setText( rocket.name );

			if( rocket.family != null && rocket.family.agencies != null )
			{
				StringBuilder sb = new StringBuilder();
				Iterator<Agency> it = rocket.family.agencies.iterator();
				while( it.hasNext() )
				{
					Agency agency = it.next();
					sb.append( agency.countryCode );

					if( it.hasNext() )
					{
						sb.append( ',' );
					}
				}

				Drawable flagDrawable = FlagResourceUtility.getFlagDrawable( sb.toString(), getContext() );
				viewHolder.rocketNameView.setCompoundDrawablesWithIntrinsicBounds( flagDrawable, null, null, null );
			}
			else
			{
				viewHolder.rocketNameView.setCompoundDrawablesWithIntrinsicBounds( R.drawable.flag_unknown, 0, 0, 0 );
			}

			final String agencies;
			if( rocket.family != null && rocket.family.agencies != null )
			{
				StringBuilder agenciesSb = new StringBuilder();
				for( Iterator<Agency> it = rocket.family.agencies.iterator(); it.hasNext(); )
				{
					Agency agency = it.next();
					if( agency.abbrev != null )
					{
						agenciesSb.append( agency.abbrev );
						if( it.hasNext() )
						{
							agenciesSb.append( ", " );
						}
					}
				}
				agencies = agenciesSb.toString();
			}
			else
			{
				agencies = context.getString( R.string.ROCKETLIST_item_no_agencies );
			}

			viewHolder.rocketAgenciesView.setText( context.getString( R.string.ROCKETLIST_item_agencies, agencies ) );

			final String configuration;
			if( rocket.configuration != null && rocket.configuration.trim().length() > 0 )
			{
				configuration = rocket.configuration.trim();
			}
			else
			{
				configuration = getContext().getString( R.string.ROCKETLIST_item_no_configuration );
			}

			viewHolder.rocketConfigurationView
					.setText( context.getString( R.string.ROCKETLIST_item_configuration, configuration ) );

			return view;
		}
	}

	private class RocketUpdateReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive( final Context context, final Intent intent )
		{
			final Activity activity = getActivity();
			if( activity != null && isAdded() )
			{
				if( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Rocket List update SUCCESS broadcast, will update the UI now." );

					reloadData();

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_rocket_list_update_complete, Style.CONFIRM ).show();
				}
				else if( RocketUpdateTask.ACTION_ROCKET_LIST_UPDATE_FAILED.equals( intent.getAction() ) )
				{
					Log.d( TAG, "Received Rocket List update FAILURE broadcast." );

					hideLoadingIndicators();
					Crouton.makeText( activity, R.string.TOAST_rocket_list_update_failed, Style.ALERT ).show();
				}
			}
		}
	}
}
