package net.sf.openmm.frontend2;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class UpdateServerTask extends AsyncTask< String, Integer, Integer >
{
	
	private static final String TAG = UpdateServerTask.class.getSimpleName();
	
	private final Activity act;
	
	public UpdateServerTask( Activity act )
	{
		super();
		this.act = act;
	}
	
	@Override
	protected Integer doInBackground( String... params )
	{
		try
		{
			final CredentialsProvider cprov = new BasicCredentialsProvider();
			final String username = OpenMMUtil.getServerUsername( act );
			final String password = OpenMMUtil.getServerPassword( act );
			cprov.setCredentials( new AuthScope( AuthScope.ANY_HOST, AuthScope.ANY_PORT ), new UsernamePasswordCredentials( username, password ) );
			final DefaultHttpClient client = new DefaultHttpClient();
			client.setCredentialsProvider( cprov );
			final HttpPost request = new HttpPost( OpenMMUtil.getServerUrl( act ) + "/api/update" );
			
			final List< NameValuePair > args = new ArrayList< NameValuePair >( params.length / 2 );
			for ( int i = 0; i < params.length; i += 2 )
			{
				args.add( new BasicNameValuePair( params[ i ], params[ i + 1 ] ) );
			}
			request.setEntity( new UrlEncodedFormEntity( args ) );
			
			final HttpResponse resp = client.execute( request );
			return resp.getStatusLine().getStatusCode();
		}
		catch ( Exception e )
		{
			Log.e( TAG, "Failed to perform update", e );
		}
		return 0;

	}


}
