package com.darkrockstudios.apps.tminus.experiences.agency.browse.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.darkrockstudios.apps.tminus.R;
import com.darkrockstudios.apps.tminus.launchlibrary.Agency;
import com.darkrockstudios.apps.tminus.misc.FlagResourceUtility;

/**
 * Created by Adam on 2/18/14.
 */
public class AgencyListAdapter extends ArrayAdapter<Agency>
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

			AgencyListViewHolder viewHolder = new AgencyListViewHolder( view );
			view.setTag( viewHolder );
		}

		AgencyListViewHolder viewHolder = (AgencyListViewHolder) view.getTag();

		Agency agency = getItem( pos );
		viewHolder.m_name.setText( agency.name );
		viewHolder.m_abbreviation.setText( agency.abbrev );

		Drawable flagDrawable = FlagResourceUtility.getFlagDrawable( agency.countryCode, getContext() );
		viewHolder.m_abbreviation.setCompoundDrawablesWithIntrinsicBounds( flagDrawable, null, null, null );

		return view;
	}
}
