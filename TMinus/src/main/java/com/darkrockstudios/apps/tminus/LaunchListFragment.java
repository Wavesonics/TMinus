package com.darkrockstudios.apps.tminus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.darkrockstudios.apps.tminus.launchlibrary.Launch;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A list fragment representing a list of Launches. This fragment
 * also supports tablet devices by allowing list items to be given an
 * 'activated' state upon selection. This helps indicate which item is
 * currently being viewed in a {@link com.darkrockstudios.apps.tminus.LaunchDetailFragment}.
 * <p/>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class LaunchListFragment extends ListFragment
{
	private static final String TAG = LaunchListFragment.class.getSimpleName();

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String    STATE_ACTIVATED_POSITION = "activated_position";
	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static       Callbacks s_dummyCallbacks         = new Callbacks()
	{
		@Override
		public void onItemSelected( Launch launch )
		{
		}
	};

	private RequestQueue         m_requestQueue;
	private ArrayAdapter<Launch> m_adapter;

	private Callbacks m_callbacks         = s_dummyCallbacks;
	private int       m_activatedPosition = ListView.INVALID_POSITION;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public LaunchListFragment()
	{
	}

	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		m_requestQueue = Volley.newRequestQueue( getActivity() );

		m_adapter = new ArrayAdapter<Launch>(
				                                    getActivity(),
				                                    android.R.layout.simple_list_item_activated_1,
				                                    android.R.id.text1 );

		setListAdapter( m_adapter );

		refresh();
	}

	@Override
	public void onViewCreated( View view, Bundle savedInstanceState )
	{
		super.onViewCreated( view, savedInstanceState );

		// Restore the previously serialized activated item position.
		if( savedInstanceState != null
				    && savedInstanceState.containsKey( STATE_ACTIVATED_POSITION ) )
		{
			setActivatedPosition( savedInstanceState.getInt( STATE_ACTIVATED_POSITION ) );
		}
	}

	@Override
	public void onAttach( Activity activity )
	{
		super.onAttach( activity );

		// Activities containing this fragment must implement its callbacks.
		if( !(activity instanceof Callbacks) )
		{
			throw new IllegalStateException( "Activity must implement fragment's callbacks." );
		}

		m_callbacks = (Callbacks)activity;
	}

	@Override
	public void onDetach()
	{
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		m_callbacks = s_dummyCallbacks;
	}

	@Override
	public void onListItemClick( ListView listView, View view, int position, long id )
	{
		super.onListItemClick( listView, view, position, id );

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		Launch launch = (Launch)listView.getAdapter().getItem( position );
		m_callbacks.onItemSelected( launch );
	}

	@Override
	public void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
		if( m_activatedPosition != ListView.INVALID_POSITION )
		{
			// Serialize and persist the activated item position.
			outState.putInt( STATE_ACTIVATED_POSITION, m_activatedPosition );
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick( boolean activateOnItemClick )
	{
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode( activateOnItemClick
		                             ? ListView.CHOICE_MODE_SINGLE
		                             : ListView.CHOICE_MODE_NONE );
	}

	private void setActivatedPosition( int position )
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

	private void testRequest()
	{
		if( m_requestQueue != null )
		{
			final String url = "http://launchlibrary.net/ll/json/next/10";

			LaunchListResponseListener listener = new LaunchListResponseListener();
			JsonObjectRequest request = new JsonObjectRequest( url, null, listener, listener );
			m_requestQueue.add( request );
		}
	}

	public void refresh()
	{
		testRequest();
	}

	private class LaunchListResponseListener implements Response.Listener<JSONObject>, Response.ErrorListener
	{
		@Override
		public void onResponse( JSONObject response )
		{
			Log.i( TAG, response.toString() );

			parseLaunchList( response );
		}

		@Override
		public void onErrorResponse( VolleyError error )
		{
			Log.i( TAG, error.getMessage() );
		}

		private void parseLaunchList( JSONObject launchListObj )
		{
			m_adapter.clear();

			final Gson gson = new Gson();

			try
			{
				JSONArray launchListArray = launchListObj.getJSONArray( "launch" );
				for( int ii = 0; ii < launchListArray.length(); ++ii )
				{
					JSONObject launchObj = launchListArray.getJSONObject( ii );
					if( launchObj != null && m_adapter != null )
					{
						Launch launch = gson.fromJson( launchObj.toString(), Launch.class );
						Log.d( TAG, "name: " + launch.name );
						m_adapter.add( launch );
					}
				}
			}
			catch( JSONException e )
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks
	{
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected( Launch launch );
	}
}
