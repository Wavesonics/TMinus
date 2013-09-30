package com.darkrockstudios.apps.tminus.launchlibrary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Adam on 8/3/13.
 * Dark Rock Studios
 * darkrockstudios.com
 */
public class LaunchLibraryGson
{
	public static Gson create()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter( Double.class, new DoubleTypeAdapter() );
		builder.registerTypeAdapter( Date.class, new DateTypeAdapter() );

		return builder.create();
	}

	private static class DateTypeAdapter extends TypeAdapter<Date>
	{
		@Override
		public void write( JsonWriter out, Date value )
				throws IOException
		{
			DateFormat df = new SimpleDateFormat( Launch.DATE_FORMAT );
			out.value( df.format( value ) );
		}

		@Override
		public Date read( JsonReader in ) throws IOException
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

				DateFormat df = new SimpleDateFormat( Launch.DATE_FORMAT );
				return df.parse( result );
			}
			catch( ParseException e )
			{
				throw new JsonSyntaxException( e );
			}
		}
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
