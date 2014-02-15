package com.darkrockstudios.apps.tminus.dataupdate.wikipedia;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

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
	private static final Pattern REF_PATTERN          = Pattern.compile( "<ref>.*?</ref>", Pattern.CASE_INSENSITIVE );
	private static final Pattern CITE_PATTERN         =
			Pattern.compile( "\\{\\{cite.*?\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern LANG_PATTERN         =
			Pattern.compile( "\\{\\{lang-[a-z]+[|](.*?)\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern COORD_PATTERN        =
			Pattern.compile( "\\{\\{coord.*?\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern CONVERT_PATTERN      =
			Pattern.compile( "\\{\\{convert\\|([0-9]+)\\|([a-zA-Z]+)\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern BOLD_PATTERN         = Pattern.compile( "'''(.+?)'''" );
	private static final Pattern ITALICS_PATTERN      = Pattern.compile( "''(.+?)''" );

	public static String processWikiArticle( final JSONObject response )
	{
		String articleText;

		Log.d( TAG, "Received wiki ARTICLE data, parsing..." );
		try
		{
			JSONObject parse = response.getJSONObject( "parse" );
			JSONObject text = parse.getJSONObject( "wikitext" );

			String rawArticleText = text.getString( "*" );
			articleText = cleanUpWikiText( rawArticleText );
		}
		catch( final JSONException e )
		{
			e.printStackTrace();
			articleText = null;
		}

		return articleText;
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

		matcher = LANG_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1" );

		matcher = COORD_PATTERN.matcher( articleText );
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

		final String lowerCaseArticleText = articleText.toLowerCase( locale );

		int curPos = lowerCaseArticleText.indexOf( infoBox.toLowerCase( locale ) );
		if( curPos > -1 )
		{
			curPos += infoBox.length();
			int openBrackets = 1;
			while( openBrackets > 0 )
			{
				int nextOpen = lowerCaseArticleText.indexOf( openBracket, curPos );
				int nextClose = lowerCaseArticleText.indexOf( closeBracket, curPos );
				if( nextOpen > -1 && nextOpen < nextClose )
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

	public static String extractArticleTitle( final String wikiUrl )
	{
		final String articleTitle;
		if( wikiUrl != null && !wikiUrl.isEmpty() )
		{
			Matcher matcher = WIKI_ARTICLE_PATTERN.matcher( wikiUrl );
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