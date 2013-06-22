package com.darkrockstudios.apps.tminus;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.darkrockstudios.apps.tminus.R.layout;

/**
 * Created by Adam on 6/22/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LaunchListFragment extends Fragment
{
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		// Inflate the layout for this fragment
		return inflater.inflate( layout.fragment_launch_list, container, false );
	}
}
