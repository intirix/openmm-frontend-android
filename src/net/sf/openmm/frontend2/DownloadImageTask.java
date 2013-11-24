package net.sf.openmm.frontend2;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class DownloadImageTask extends AsyncTask< String, Integer, Bitmap >
{
	
	private static final String TAG = DownloadImageTask.class.getSimpleName();
	
	private final Context ctx;
	
	public DownloadImageTask( Context ctx )
	{
		super();
		this.ctx = ctx;
	}

	@Override
	protected Bitmap doInBackground( String... params )
	{
		final String url = params[ 0 ];
		
		final InputStream cacheStream = CacheUtil.getCacheContents( ctx, url );
		if ( cacheStream != null )
		{
			return BitmapFactory.decodeStream( cacheStream );
		}
		
		try
		{
			final DefaultHttpClient client = new DefaultHttpClient();
			final HttpGet request = new HttpGet( url );
			Log.d( TAG, "Downloading " + url );
			final HttpResponse resp = client.execute( request );
			if ( resp.getStatusLine().getStatusCode() == 200 )
			{
				final HttpEntity entity = resp.getEntity();
				
				final ByteArrayOutputStream buffer = new ByteArrayOutputStream( 1024 );
				IOUtils.copy( entity.getContent(), buffer );
				final byte[] byteArray = buffer.toByteArray();
				CacheUtil.writeToCache( ctx, url, byteArray );
				
				return BitmapFactory.decodeByteArray( byteArray, 0, byteArray.length );
			}

		}
		catch ( Exception e )
		{
			Log.e( TAG, "Failed to get " + url, e );
		}
		return null;

	}


}
