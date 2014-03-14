package com.darkrockstudios.apps.tminus.launchlibrary;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter( Double.class, new DoubleTypeAdapter() );
		builder.registerTypeAdapter( DateTime.class, new DateTypeAdapter() );

		return builder.create();
	}

	private static class DateTypeAdapter extends TypeAdapter<DateTime>
	{
		private DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern( Launch.DATE_FORMAT );

		@Override
		public void write( JsonWriter out, DateTime value )
		throws IOException
		{
			out.value( value.toString() );
		}

		@Override
		public DateTime read( JsonReader in ) throws IOException
		{
			if( in.peek() == JsonToken.NULL )
			{
				in.nextNull();
				return null;
			}

			String result = in.nextString();
			if( TextUtils.isEmpty( result ) )
			{
				return null;
			}

			return DATE_TIME_FORMATTER.parseDateTime( result );
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
