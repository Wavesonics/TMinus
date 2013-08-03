package com.darkrockstudios.apps.tminus.launchlibrary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by Adam on 8/3/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LaunchLibraryGson
{
	public static Gson create()
	{
		return new GsonBuilder().setDateFormat( Launch.DATE_FORMAT ).registerTypeAdapter( Double.class, new DoubleTypeAdapter() ).create();
	}

	private static class DoubleTypeAdapter extends TypeAdapter<Double>
	{
		@Override
		public void write( JsonWriter out, Double value )
		throws IOException
		{
			out.value( value );
		}

		@Override
		public Double read( JsonReader in ) throws IOException
		{
			if( in.peek() == JsonToken.NULL )
			{
				in.nextNull();
				return null;
			}
			try
			{
				String result = in.nextString();
				if( "".equals( result ) )
				{
					return null;
				}
				return Double.parseDouble( result );
			}
			catch( NumberFormatException e )
			{
				throw new JsonSyntaxException( e );
			}
		}
	}
}
