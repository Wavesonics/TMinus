package com.darkrockstudios.apps.tminus.dataupdate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.darkrockstudios.apps.tminus.TMinusApplication;

import org.json.JSONObject;

/**
 * Created by Adam on 10/23/13.
 */
public abstract class UpdateTask implements Response.Listener<JSONObject>, Response.ErrorListener
{
	private static final String TAG = UpdateTask.class.getSimpleName();

	private Context m_context;
	private Request m_request;

	public UpdateTask( final Context context )
	{
		m_context = context;
	}

	protected Context getContext()
	{
		return m_context;
	}

	public void run()
	{
		makeRequest();
	}

	private void makeRequest()
	{
		if( m_request == null )
		{
			JsonObjectRequest request = new JsonObjectRequest( getRequestUrl(), null, this, this );
			request.setTag( this );
			m_request = TMinusApplication.getRequestQueue().add( request );
		}
	}

	@Override
	public void onResponse( final JSONObject response )
	{
		Log.i( TAG, "Response successfully retrieved from sever." );

		HandleResponseThread thread = new HandleResponseThread( response );
		thread.start();
	}

	@Override
	public void onErrorResponse( final VolleyError error )
	{
		Log.i( TAG, "Failed to retrieve data from sever." );
		if( error != null && error.getMessage() != null )
		{
			Log.i( TAG, error.getMessage() );
		}

		sendFailureBroadcast();

		m_request = null;
	}

	private void sendSuccessBroadcast()
	{
		Log.i( TAG, "Data successfully updated, sending success broadcast." );
		final Intent intent = new Intent( getSuccessIntentAction() );
		getContext().sendBroadcast( intent );
	}

	private void sendFailureBroadcast()
	{
		Log.i( TAG, "Data update failed, sending failure broadcast." );
		final Intent intent = new Intent( getFailureIntentAction() );
		getContext().sendBroadcast( intent );
	}

	private class HandleResponseThread extends Thread
	{
		private JSONObject m_response;

		public HandleResponseThread( final JSONObject response )
		{
			m_response = response;
		}

		@Override
		public void run()
		{
			if( handleData( m_response ) )
			{
				sendSuccessBroadcast();
			}
			else
			{
				sendFailureBroadcast();
			}

			m_request = null;
		}
	}

	public abstract boolean handleData( JSONObject response );

	public abstract String getRequestUrl();

	public abstract String getSuccessIntentAction();

	public abstract String getFailureIntentAction();
}
