package com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.TMinusApplication;
import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.AgencyDetail;
import com.darkrockstudios.apps.tminus.database.tables.AgencyType;
import com.darkrockstudios.apps.tminus.dataupdate.DataUpdaterService;
import com.darkrockstudios.apps.tminus.experiences.agency.detail.dataupdate.AgencyDetailUpdateTask;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.FlagResourceUtility;
import com.darkrockstudios.apps.tminus.misc.TminusUri;
import com.darkrockstudios.apps.tminus.misc.Utilities;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by Adam on 2/11/14.
 */
public class AgencyDetailFragment extends DialogFragment implements Utilities.ZoomAnimationHandler
{
	private static final String TAG                              = AgencyDetailFragment.class.getSimpleName();
	private static final String FRAGMENT_TAG_COUNTRY_LIST_DIALOG = "CountryListDialog";
	public static final  String ARG_ITEM_ID                      = "item_id";

	@InjectView(R.id.AGENCYDETAIL_container)
	View m_containerView;

	@InjectView(R.id.AGENCYDETAIL_name)
	TextView m_name;

	@InjectView(R.id.AGENCYDETAIL_abbreviation)
	TextView m_abbreviation;

	@InjectView(R.id.AGENCYDETAIL_country)
	TextView m_country;

	@InjectView(R.id.AGENCYDETAIL_type)
	TextView m_type;

	@InjectView(R.id.AGENCYDETAIL_info_url)
	TextView m_infoUrl;

	@InjectView(R.id.AGENCYDETAIL_agency_image)
	NetworkImageView m_agencyImage;

	@InjectView(R.id.AGENCYDETAIL_expanded_agency_image)
	NetworkImageView m_agencyImageExpanded;

	@InjectView(R.id.AGENCYDETAIL_summary)
	TextView m_summary;

	private int          m_agencyId;
	private Agency       m_agency;
	private AgencyType   m_agencyType;
	private AgencyDetail m_agencyDetail;

	private Animator m_currentAnimator;
	private int      m_shortAnimationDuration;

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

		m_shortAnimationDuration =
				getResources().getInteger( android.R.integer.config_shortAnimTime );

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

		m_agencyImage.setDefaultImageResId( R.drawable.launch_detail_no_rocket_image );
		m_agencyImage.setEnabled( false );

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
			m_agencyDetail = getAgencyDetail();
			if( m_agencyDetail == null && !TextUtils.isEmpty( m_agency.wikiURL ) )
			{
				requestAgencyDetails();
			}
			else if( m_agencyDetail != null )
			{
				if( m_agencyDetail.imageUrl != null && m_agencyDetail.imageUrl.length() > 0 )
				{
					ImageLoader imageLoader = new ImageLoader( TMinusApplication
							                                           .getRequestQueue(),
					                                           TMinusApplication.getBitmapCache() );

					m_agencyImage.setImageUrl( m_agencyDetail.imageUrl, imageLoader );
					m_agencyImage.setEnabled( true );

					m_agencyImageExpanded.setImageUrl( m_agencyDetail.imageUrl, imageLoader );
				}

				m_summary.setText( Html.fromHtml( m_agencyDetail.summary ) );
			}
			else
			{
				m_summary.setText( R.string.AGENCYDETAIL_no_summary );
			}

			m_name.setText( m_agency.name );
			m_abbreviation.setText( m_agency.abbrev );

			m_country.setText( m_agency.countryCode );

			Drawable flagDrawable = FlagResourceUtility.getFlagDrawable( m_agency.countryCode, getActivity() );
			m_country.setCompoundDrawablesWithIntrinsicBounds( flagDrawable,
			                                                   null,
			                                                   getResources().getDrawable( R.drawable.ic_expand ),
			                                                   null );

			m_agencyType = getAgencyType();
			if( m_agencyType != null )
			{
				m_type.setText( m_agencyType.name );
			}
			else
			{
				m_type.setText( R.string.AGENCYDETAIL_no_type );
			}

			if( m_agency.infoURL != null && URLUtil.isValidUrl( m_agency.infoURL ) )
			{
				m_infoUrl.setVisibility( View.VISIBLE );
				m_infoUrl.setText( m_agency.infoURL );
			}
			else
			{
				m_infoUrl.setVisibility( View.GONE );
			}
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

	public void refresh()
	{
		requestAgencyDetails();
	}

	private AgencyType getAgencyType()
	{
		AgencyType agencyType = null;

		Activity activity = getActivity();
		if( m_agency != null && activity != null && isAdded() )
		{
			DatabaseHelper databaseHelper = new DatabaseHelper( activity );
			try
			{
				Dao<AgencyType, Integer> agencyDao = databaseHelper.getDao( AgencyType.class );
				agencyType = agencyDao.queryForId( m_agency.type );
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}
		}

		return agencyType;
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

	private AgencyDetail getAgencyDetail()
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

						reloadData();
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

					m_summary.setText( R.string.AGENCYDETAIL_no_summary );

					Crouton.makeText( activity, R.string.TOAST_agency_detail_update_failed, Style.ALERT ).show();
					activity.setProgressBarIndeterminateVisibility( false );
				}
			}
		}
	}

	public void zoomAgencyImage()
	{
		// If we don't have rocket info yet, don't bother zooming
		if( m_agencyDetail != null && m_agencyDetail.imageUrl != null )
		{
			Utilities.zoomImage( m_agencyImage, m_agencyImageExpanded, m_containerView, this,
			                     m_shortAnimationDuration );
		}
	}

	@Override
	public void setCurrentAnimator( final Animator animator )
	{
		m_currentAnimator = animator;
	}

	@Override
	public Animator getCurrentAnimator()
	{
		return m_currentAnimator;
	}

	@OnClick(R.id.AGENCYDETAIL_country)
	public void onCountriesClicked()
	{
		CountryListDialog dialog = CountryListDialog.newInstance( m_agency.countryCode );
		dialog.show( getFragmentManager(), FRAGMENT_TAG_COUNTRY_LIST_DIALOG );
	}
}
