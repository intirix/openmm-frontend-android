package net.sf.openmm.frontend2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.util.Log;
import android.view.Display;
import android.webkit.MimeTypeMap;

public class OpenMMUtil
{
	private static final String TAG = OpenMMUtil.class.getSimpleName();
	
	public static int BACKGROUND_COLOR = 0xFF3385FF;
	
	static final String PREF_NAME = "OpenMM2";

	public static SharedPreferences getPrefs( Context act )
	{
		return act.getSharedPreferences( OpenMMUtil.PREF_NAME, 0 );
	}
	
	/**
	 * Get the server url
	 * @param act
	 * @return
	 */
	public static String getServerConfigUrl( Activity act )
	{
		return getPrefs( act ).getString( "server.url", "http://localhost:3760" );
	}

	/**
	 * Get the server url
	 * @param act
	 * @return
	 */
	public static String getServerUrl( Activity act )
	{
		return getPrefs( act ).getString( "server.url", "http://localhost:3760" ) + "/openmm";
	}

	/**
	 * Get the server url with username/password
	 * @param act
	 * @return
	 */
	public static String getServerAccessUrl( Activity act )
	{
		return getServerUrl( act ).replace( "://", "://" + getServerUsername( act ) + ':' + getServerPassword( act ) + '@' );
	}

	/**
	 * Get the server password
	 * @param act
	 * @return
	 */
	public static String getServerPassword( Context act )
	{
		return getPrefs( act ).getString( "server.password", "password" );
	}
	

	/**
	 * Get the server username
	 * @param act
	 * @return
	 */
	public static String getServerUsername( Context act )
	{
		return getPrefs( act ).getString( "server.username", "admin" );
	}
	
	/**
	 * Get the number of rows to display
	 * @param act
	 * @return
	 */
	public static int getDisplayRows( Activity act )
	{
		return getPrefs( act ).getInt( "display.rows", 9 );
	}

	public static int getDisplayWidth( Activity act )
	{
		final Display display = act.getWindowManager().getDefaultDisplay();
		final Point size = new Point();
		display.getSize(size);
		return size.x;
	}

	public static int getTextViewHeight( Activity act )
	{
		final Display display = act.getWindowManager().getDefaultDisplay();
		final Point size = new Point();
		display.getSize( size );
		final int height = size.y;
		final int numRows = getPrefs( act ).getInt( "display.rows", 9 );

		final int TEXT_VIEW_HEIGHT = height / numRows;
		return TEXT_VIEW_HEIGHT;
	}

	public static int getTextMaxHeight( Activity act )
	{
		return getTextViewHeight( act ) * 9 / 10;
	}


	/**
	 * Play a url
	 * @param activity
	 * @param url
	 * @param ext
	 */
	public static void playUrl( Activity act, String url, String ext )
	{
		Log.d( TAG, "playUrl(" + url + ',' + ext + ')' );
		final String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension( ext );
		final Intent mediaIntent = new Intent( Intent.ACTION_VIEW );
		mediaIntent.setDataAndType( Uri.parse( url ), mimeType );
		act.startActivity( mediaIntent );
	}
	

	/**
	 * Start an activity
	 * @param act
	 * @param i
	 */
	public static void startActivityForResult( Activity act, Intent i )
	{
		if ( act.getIntent().hasExtra( "ACT_INDEX" ) )
		{
			int myIndex = act.getIntent().getIntExtra( "ACT_INDEX", 1 );
			i.putExtra( "ACT_INDEX", myIndex + 1 );
			Log.d( TAG, "Starting activity from " + act.getClass().getSimpleName() + '[' + ( myIndex + 1 ) + ']' );
			act.startActivityForResult( i, myIndex + 1 );
		}
		else
		{
			i.putExtra( "ACT_INDEX", 1 );
			Log.d( TAG, "Starting activity from " + act.getClass().getSimpleName() + '[' + 1 + ']' );
			act.startActivityForResult( i, 1 );
		}
	}

	
}
