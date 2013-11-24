package net.sf.openmm.frontend2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import android.content.Context;
import android.util.Log;

public class CacheUtil
{
	
	private static final String TAG = CacheUtil.class.getSimpleName();

	/**
	 * Get a cache key for a url
	 * @param url
	 * @return
	 */
	public static String getCacheKey( String url )
	{
		return new String( Hex.encodeHex( DigestUtils.md5( url ) ) );
	}

	/**
	 * Get the cache contents
	 * @param ctx
	 * @param url
	 * @return
	 */
	public static InputStream getCacheContents( Context ctx, String url )
	{
		final File cacheFile = new File( ctx.getCacheDir(), getCacheKey( url ) );
		if ( cacheFile.exists() )
		{
			try
			{
				Log.d( TAG, "Reading cached file " + cacheFile.getName() + " for " + url );
				return new BufferedInputStream( new FileInputStream( cacheFile ) );
			}
			catch ( IOException e )
			{
				// delete file if an error occurred
				cacheFile.delete();
				return null;
			}
		}
		return null;
	}

	/**
	 * Get cache contents as a string
	 * @param ctx
	 * @param url
	 * @return
	 */
	public static String getCacheContentsAsString( Context ctx, String url )
	{
		final InputStream is = getCacheContents( ctx, url );
		if ( is != null )
		{
			try
			{
				final ByteArrayOutputStream buffer = new ByteArrayOutputStream( 1024 );
				IOUtils.copy( is, buffer );
				return buffer.toString();
			}
			catch ( IOException e )
			{
				// ignore
			}
			finally
			{
				try
				{
					is.close();
				}
				catch ( IOException e )
				{
					// ignore
				}
			}
		}
		return null;
	}

	/**
	 * Write to a cache file
	 * @param ctx
	 * @param url
	 * @param buffer
	 */
	public static void writeToCache( Context ctx, String url, byte[] buffer )
	{
		final File cacheFile = new File( ctx.getCacheDir(), getCacheKey( url ) );
		try
		{
			Log.d( TAG, "Writing cached file " + cacheFile.getName() + " [" + buffer.length + "b] for " + url );
			final OutputStream os = new BufferedOutputStream( new FileOutputStream( cacheFile ) );
			try
			{
				os.write( buffer );
			}
			finally
			{
				os.close();
			}
		}
		catch ( IOException e )
		{
			// ignore
		}
	}
}
