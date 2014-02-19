package com.darkrockstudios.apps.tminus.experiences.agency.detail.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.base.ListDialog;
import com.darkrockstudios.apps.tminus.misc.FlagResourceUtility;

/**
 * Created by Adam on 2/18/14.
 */
public class CountryListDialog extends ListDialog
{
	private static final String ARG_COUNTRIES = CountryListDialog.class.getPackage() + ".COUNTRIES";

	private String           m_countries;
	private CountriesAdapter m_adapter;

	public static CountryListDialog newInstance( final String cslCountries )
	{
		CountryListDialog fragment = new CountryListDialog();

		Bundle bundle = new Bundle();
		bundle.putString( ARG_COUNTRIES, cslCountries );
		fragment.setArguments( bundle );

		return fragment;
	}

	@Override
	public void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		Bundle args = getArguments();
		if( args != null )
		{
			m_countries = args.getString( ARG_COUNTRIES );
			m_adapter = new CountriesAdapter( getActivity(), m_countries );
		}
	}

	@Override
	public void onViewCreated( final View view, final Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		m_listView.setAdapter( m_adapter );

		getDialog().setTitle( R.string.COUNTRYLISTDIALOG_title );
	}

	private class CountriesAdapter extends ArrayAdapter<String>
	{
		private LayoutInflater m_inflater;

		public CountriesAdapter( final Context context, final String countries )
		{
			super( context, android.R.layout.simple_list_item_1 );

			m_inflater = LayoutInflater.from( context );

			if( countries.contains( "," ) )
			{
				addAll( countries.split( "," ) );
			}
			else
			{
				add( countries );
			}
		}

		@Override
		public android.view.View getView( final int position, final View convertView, final ViewGroup parent )
		{
			final View view;

			if( convertView == null )
			{
				view = m_inflater.inflate( android.R.layout.simple_list_item_1, parent, false );
			}
			else
			{
				view = convertView;
			}

			String item = getItem( position );

			TextView countryTextView = (TextView) view.findViewById( android.R.id.text1 );
			countryTextView.setText( item );

			countryTextView.setCompoundDrawablesWithIntrinsicBounds( FlagResourceUtility.getFlagResource( item ), 0, 0, 0 );

			return view;
		}
	}
}
