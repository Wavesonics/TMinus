package com.darkrockstudios.apps.tminus.base;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.darkrockstudios.apps.tminus.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Adam on 2/18/14.
 */
public class ListDialog extends DialogFragment
{
	@InjectView(R.id.DIALOGLIST_list_view)
	protected ListView m_listView;

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.dialog_list, container, false );
		ButterKnife.inject( this, view );

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}
}
