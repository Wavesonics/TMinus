package com.darkrockstudios.apps.tminus.dataupdate.wikipedia;

import android.content.Context;
import android.util.Log;

import com.darkrockstudios.apps.tminus.database.DatabaseHelper;
import com.darkrockstudios.apps.tminus.database.tables.RocketDetail;
import com.darkrockstudios.apps.tminus.launchlibrary.Rocket;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Adam on 2/9/14.
 */
// This Wiki api call will get the summary section for a given article
// /w/api.php?action=parse&format=json&page=Falcon_9&prop=text&section=0

// the Parse tree option might be what we want, it allows us to traverse the content via XML rather than regex that crap
public class WikiArticleHandler
{
	private static final String TAG = WikiArticleHandler.class.getSimpleName();

	private static final Pattern WIKI_ARTICLE_PATTERN =
			Pattern.compile( "^http[s]?://[a-z]{2}.wikipedia.org/wiki/([a-zA-Z0-9-_\\(\\)]+)/?(?:\\?.*)?$" );

	private static final Pattern COMMENT_PATTERN      = Pattern.compile( "\\<\\!--(.*?)--\\>" );
	private static final Pattern GENERAL_LINK_PATTERN = Pattern.compile( "\\[\\[(.*?:)?(.*?)(\\|.*?)?\\]\\]" );
	private static final Pattern SIMPLE_LINK_PATTERN  = Pattern.compile( "\\[\\[([^|]*?)\\]\\]" );
	private static final Pattern ASSET_PATTERN        = Pattern.compile( "\\[\\[[a-zA-Z]+:(.*?)\\]\\]" );
	private static final Pattern REF_PATTERN          = Pattern.compile( "(<ref>.*?</ref>)", Pattern.CASE_INSENSITIVE );
	private static final Pattern CITE_PATTERN         =
			Pattern.compile( "(\\{\\{cite.*?\\}\\})", Pattern.CASE_INSENSITIVE );
	private static final Pattern CONVERT_PATTERN      =
			Pattern.compile( "\\{\\{convert\\|([0-9]+)\\|([a-zA-Z]+)\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern BOLD_PATTERN         = Pattern.compile( "'''(.+?)'''" );
	private static final Pattern ITALICS_PATTERN      = Pattern.compile( "''(.+?)''" );

	public static boolean processWikiArticle( final JSONObject response, int rocketId, Context context )
	{
		boolean success = false;

		Log.d( TAG, "Received wiki ARTICLE data, parsing..." );
		try
		{
			JSONObject parse = response.getJSONObject( "parse" );
			JSONObject text = parse.getJSONObject( "wikitext" );

			String articleText = text.getString( "*" );
			articleText = cleanUpWikiText( articleText );

			if( saveToDatabase( articleText, rocketId, context ) )
			{
				success = true;
			}
			else
			{
				success = false;
			}
		}
		catch( final JSONException e )
		{
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	private static boolean saveToDatabase( final String articleText, int rocketId, Context context )
	{
		boolean success = false;

		final DatabaseHelper databaseHelper = OpenHelperManager.getHelper( context, DatabaseHelper.class );
		if( databaseHelper != null )
		{
			try
			{
				Dao<RocketDetail, Integer> rocketDetailDao = databaseHelper.getDao( RocketDetail.class );
				RocketDetail rocketDetail = rocketDetailDao.queryForId( rocketId );
				if( rocketDetail == null )
				{
					rocketDetail = new RocketDetail();
					rocketDetail.rocketId = rocketId;
					rocketDetail.summary = articleText;
					rocketDetailDao.create( rocketDetail );
					success = true;
				}
				else
				{
					rocketDetail.summary = articleText;
					rocketDetailDao.update( rocketDetail );
					success = true;
				}
			}
			catch( final SQLException e )
			{
				e.printStackTrace();
			}

			OpenHelperManager.releaseHelper();
		}

		return success;
	}

	private static String cleanUpWikiText( final String wikiText )
	{
		// Unescape the wikitext
		String articleText = wikiText.replace( "\\\"", "\"" );
		articleText = articleText.replace( "\\/", "/" );

		articleText = removeInfoBox( articleText );

		// Use our regex patterns to clean out the wiki syntax and replace it with mostly plain text
		// or simple HTML for formatting
		Matcher matcher;

		matcher = COMMENT_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = REF_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = CITE_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = CONVERT_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1 $2" );

		matcher = ASSET_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = SIMPLE_LINK_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1" );

		matcher = GENERAL_LINK_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$2" );

		matcher = BOLD_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "<strong>$1</strong>" );

		matcher = ITALICS_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "<em>$1</em>" );

		// Lastly trim any whitespace, and then space things out
		articleText = articleText.trim();
		articleText = articleText.replace( "\n", "<br/>" );

		return articleText;
	}

	private static String removeInfoBox( final String articleText )
	{
		final String cleanedArticle;

		// Find the end of the Infobox
		final String infoBox = "{{Infobox";
		final String openBracket = "{{";
		final String closeBracket = "}}";
		final Locale locale = Locale.ENGLISH;
		int curPos = articleText.toLowerCase( locale ).indexOf( infoBox.toLowerCase( locale ) );
		if( curPos > -1 )
		{
			curPos += infoBox.length();
			int openBrackets = 1;
			while( openBrackets > 0 )
			{
				int nextOpen = articleText.indexOf( openBracket, curPos );
				int nextClose = articleText.indexOf( closeBracket, curPos );
				if( nextOpen < nextClose )
				{
					curPos = nextOpen + openBracket.length();
					++openBrackets;
				}
				else
				{
					curPos = nextClose + closeBracket.length();
					--openBrackets;
				}
			}

			// Grab everything immediately after the Infobox
			cleanedArticle = articleText.substring( curPos, articleText.length() );
		}
		else
		{
			cleanedArticle = articleText;
		}

		return cleanedArticle;
	}

	public static String extractArticleTitle( final Rocket rocket )
	{
		final String articleTitle;
		if( rocket != null && rocket.wikiURL != null && !rocket.wikiURL.isEmpty() )
		{
			Matcher matcher = WIKI_ARTICLE_PATTERN.matcher( rocket.wikiURL );
			if( matcher.matches() && matcher.groupCount() == 1 )
			{
				articleTitle = matcher.group( 1 );
			}
			else
			{
				articleTitle = null;
			}
		}
		else
		{
			articleTitle = null;
		}

		return articleTitle;
	}
}