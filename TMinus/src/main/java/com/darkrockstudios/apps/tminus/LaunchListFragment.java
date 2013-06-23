package com.darkrockstudios.apps.tminus;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.darkrockstudios.apps.tminus.R.id;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Adam on 6/22/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LaunchListFragment extends Fragment
{
	private static final String TAG = LaunchListFragment.class.getSimpleName();
	private LaunchListAdapter m_adapter;
	private RequestQueue      m_requestQueue;

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container,
	                          Bundle savedInstanceState )
	{
		View layout = inflater.inflate( R.layout.fragment_launch_list, container, false );
		if( layout != null )
		{
			final ListView listView = (ListView)layout.findViewById( id.launch_list );

			m_adapter = new LaunchListAdapter( inflater.getContext(), R.layout.row_launch_list_item, id.launch_list_item_title );
			listView.setAdapter( m_adapter );
		}

		return layout;
	}

	public void setRequestQueue( RequestQueue requestQueue )
	{
		m_requestQueue = requestQueue;
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

	private static class LaunchListAdapter extends ArrayAdapter<String>
	{

		public LaunchListAdapter( Context context, int resource, int textViewResourceId )
		{
			super( context, resource, textViewResourceId );
		}
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

			try
			{
				JSONArray launchListArray = launchListObj.getJSONArray( "launch" );
				for( int ii = 0; ii < launchListArray.length(); ++ii )
				{
					JSONObject launchObj = launchListArray.getJSONObject( ii );
					if( launchObj != null && m_adapter != null )
					{
						final String launchName = launchObj.getString( "name" );
						m_adapter.add( launchName );
					}
				}
			}
			catch( JSONException e )
			{
				e.printStackTrace();
			}
		}
	}
}
