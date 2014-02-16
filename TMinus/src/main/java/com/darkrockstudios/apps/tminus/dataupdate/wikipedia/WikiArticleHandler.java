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
	private static final Pattern ASSET_PATTERN        = Pattern.compile( "\\[\\[(.*?)(?::|\\|)(.*?)\\]\\]" );
	private static final Pattern REF_PATTERN          = Pattern.compile( "(<ref>.*?</ref>)", Pattern.CASE_INSENSITIVE );

	private static final Pattern LANG_PATTERN    =
			Pattern.compile( "\\{\\{lang(?:-|\\|)[a-z]+[|](.*?)\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern CONVERT_PATTERN =
			Pattern.compile( "\\{\\{convert\\|([0-9]+)\\|([a-zA-Z]+)\\}\\}", Pattern.CASE_INSENSITIVE );
	private static final Pattern BOLD_PATTERN    = Pattern.compile( "'''(.+?)'''" );
	private static final Pattern ITALICS_PATTERN = Pattern.compile( "''(.+?)''" );

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

		// This thing is often huge, lets remove it first to cut down the size of the text
		articleText = removeWikiElement( "Infobox", articleText );

		// Use our regex patterns to clean out the wiki syntax and replace it with mostly plain text
		// or simple HTML for formatting
		Matcher matcher;

		matcher = COMMENT_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = REF_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "" );

		matcher = ASSET_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$2" );

		matcher = SIMPLE_LINK_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1" );

		matcher = LANG_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1" );

		matcher = CONVERT_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$1 $2" );

		matcher = GENERAL_LINK_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "$2" );

		matcher = BOLD_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "<strong>$1</strong>" );

		matcher = ITALICS_PATTERN.matcher( articleText );
		articleText = matcher.replaceAll( "<em>$1</em>" );

		// Remove any remaining Wiki elements
		articleText = removeWikiElement( "", articleText );

		// Lastly trim any whitespace, and then space things out
		articleText = articleText.trim();
		articleText = articleText.replace( "\n", "<br/>" );

		return articleText;
	}

	private static String removeWikiElement( final String tag, final String articleText )
	{
		String cleanedArticle = articleText;

		final String tagStart = "{{" + tag;
		final String openBracket = "{{";
		final String closeBracket = "}}";
		final Locale locale = Locale.ENGLISH;

		String lowerCaseArticleText = articleText.toLowerCase( locale );

		int startPos = -1;
		while( (startPos = lowerCaseArticleText.indexOf( tagStart.toLowerCase( locale ) )) > -1 )
		{
			int endPos = startPos + tagStart.length();
			int openBrackets = 1;
			while( openBrackets > 0 )
			{
				int nextOpen = lowerCaseArticleText.indexOf( openBracket, endPos );
				int nextClose = lowerCaseArticleText.indexOf( closeBracket, endPos );
				if( nextOpen > -1 && nextOpen < nextClose )
				{
					endPos = nextOpen + openBracket.length();
					++openBrackets;
				}
				else if( nextClose > -1 )
				{
					endPos = nextClose + closeBracket.length();
					--openBrackets;
				}
				else
				{
					Log.d( TAG, "Broken WIki tag" );
					break;
				}
			}

			cleanedArticle =
					cleanedArticle.substring( 0, startPos ) + cleanedArticle.substring( endPos, cleanedArticle.length() );
			lowerCaseArticleText = lowerCaseArticleText.substring( 0, startPos ) +
			                       lowerCaseArticleText.substring( endPos, lowerCaseArticleText.length() );
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