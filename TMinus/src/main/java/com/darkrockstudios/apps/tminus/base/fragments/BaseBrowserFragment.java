package com.darkrockstudios.apps.tminus.base.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.darkrockstudios.apps.tminus.R;
import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Adam on 2/10/14.
 */
public abstract class BaseBrowserFragment extends ListFragment implements OnRefreshListener
{
	private              int    m_activatedPosition      = ListView.INVALID_POSITION;
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	@InjectView(R.id.COMMONBROWSER_pull_to_refresh)
	protected PullToRefreshLayout m_ptrLayout;

	@InjectView(android.R.id.list)
	protected ListView m_listView;

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState )
	{
		View view = inflater.inflate( R.layout.fragment_common_browser, null );
		ButterKnife.inject( this, view );

		// Restore the previously serialized activated item position.
		if( savedInstanceState != null
		    && savedInstanceState.containsKey( STATE_ACTIVATED_POSITION ) )
		{
			setActivatedPosition( savedInstanceState.getInt( STATE_ACTIVATED_POSITION ) );
		}

		ActionBarPullToRefresh.from( getActivity() ).allChildrenArePullable().listener( this ).setup( m_ptrLayout );

		return view;
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
		ButterKnife.reset( this );
	}

	@Override
	public void setListAdapter( final ListAdapter adapter )
	{
		if( !(adapter instanceof BaseAdapter) )
		{
			throw new IllegalArgumentException( "adapter must inherit from BaseAdapter" );
		}

		BaseAdapter baseAdapter = (BaseAdapter) adapter;

		AnimationAdapter animationAdapter = new ScaleInAnimationAdapter( baseAdapter );
		super.setListAdapter( animationAdapter );

		animationAdapter.setAbsListView( m_listView );
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick( final boolean activateOnItemClick )
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode( activateOnItemClick
		                             ? ListView.CHOICE_MODE_SINGLE
		                             : ListView.CHOICE_MODE_NONE );
	}

	@Override
	public void onSaveInstanceState( final Bundle outState )
	{
		super.onSaveInstanceState( outState );
		if( m_activatedPosition != ListView.INVALID_POSITION )
		{
			// Serialize and persist the activated item position.
			outState.putInt( STATE_ACTIVATED_POSITION, m_activatedPosition );
		}
	}

	private void setActivatedPosition( final int position )
	{
		if( position == ListView.INVALID_POSITION )
		{
			getListView().setItemChecked( m_activatedPosition, false );
		}
		else
		{
			getListView().setItemChecked( position, true );
		}

		m_activatedPosition = position;
	}

	public abstract void refresh();

	@Override
	public void onRefreshStarted( final View view )
	{
		refresh();
	}

	protected void hideLoadingIndicators()
	{
		if( m_ptrLayout != null )
		{
			m_ptrLayout.setRefreshComplete();
		}

		Activity activity = getActivity();
		if( activity != null )
		{
			activity.setProgressBarIndeterminateVisibility( false );
		}
	}
}
