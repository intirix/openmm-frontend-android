package net.sf.openmm.frontend2;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

/**
 * Base activity for dynamic content
 * @author jeff
 *
 * @param <E> the type of object that represents each row
 * @param <F> the type of object that gets deserialized from the server
 */
public abstract class BaseDynamicActivity< E, F > extends Activity
{
	protected final ArrayList< E > listItems = new ArrayList< E >( 5 );

	private static final String TAG = MainActivity.class.getName();

	private ArrayAdapter< E > adapter;

	private LayoutInflater inflator;

	private View headerView = null;

	private int textViewHeight = 0;

	private int textMaxHeight = 0;

	private F response;

	protected enum ViewType
	{
		LIST,
		GRID
	}

	public ArrayAdapter< E > getAdapter()
	{
		return adapter;
	}

	public LayoutInflater getInflator()
	{
		return inflator;
	}



	public View getHeaderView()
	{
		return headerView;
	}

	public final int getTextViewHeight()
	{
		return textViewHeight;
	}

	public final int getTextMaxHeight()
	{
		return textMaxHeight;
	}



	public F getResponse()
	{
		return response;
	}

	/**
	 * Get the view type
	 * @return LIST or GRID
	 */
	protected ViewType getViewType()
	{
		return ViewType.LIST;
	}

	/**
	 * Should we cache the response
	 * @return
	 */
	protected boolean shouldCacheResponse()
	{
		return true;
	}

	/**
	 * Should we load data when the page loads
	 * @return
	 */
	protected boolean shouldLoadDataOnPageLoad()
	{
		return true;
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		final String title = getIntent().getStringExtra( "TITLE" );
		if ( title != null && title.length() > 0 )
		{
			setTitle( title );
		}

		textViewHeight = OpenMMUtil.getTextViewHeight( this );
		textMaxHeight = OpenMMUtil.getTextMaxHeight( this );

		inflator = LayoutInflater.from( this );
		
		AbsListView baseView = null;

		if ( getViewType() == ViewType.LIST )
		{
			setContentView( R.layout.dynamic_list_view );

			final ListView listView = (ListView)findViewById( android.R.id.list );
			baseView = listView;


			final int headerLayoutNumber = getHeaderLayout();
			if ( headerLayoutNumber != 0 )
			{
				headerView = inflator.inflate( headerLayoutNumber, null );
				listView.addHeaderView( headerView );
			}

		}
		else if ( getViewType() == ViewType.GRID )
		{
			setContentView( R.layout.dynamic_grid_view );
			
			final GridView gridView = (GridView)findViewById( R.id.grid );
			baseView = gridView;
		}
		
		baseView.setBackgroundColor( OpenMMUtil.BACKGROUND_COLOR );
		
		baseView.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView< ? > l, View v, int position, long id )
			{
				onListItemClick( (AbsListView)l, v, position, id );
			}
			
		} );

		adapter = new ArrayAdapter< E >( this,
				getRowLayout(),
				listItems ) {

			@Override
			public View getView(int position, View convertView, ViewGroup parent)
			{
				View row = convertView;
				Object tag = null;
				if ( row == null )
				{
					row = inflateLayout( inflator, getRowLayout() );
					tag = createLayoutTag( row );
				}
				else
				{
					tag = row.getTag();
				}
				applyDataToRow( listItems.get( position ), row, tag );
				return row;
			}

		};
		baseView.setAdapter( adapter );


		// only process the cached xml if caching is enabled
		if ( shouldCacheResponse() )
		{
			final String cachedResult = CacheUtil.getCacheContentsAsString( this, getDataUrl() );
			if ( cachedResult != null )
			{
				processXml( cachedResult );
			}
		}

	}



	@Override
	protected void onResume()
	{
		super.onResume();

		if ( shouldLoadDataOnPageLoad() )
		{
			// fire off the async task to download the xml file
			// we put this in onResume() to auto-refresh the data
			requery();
		}
	}

	/**
	 * Requery
	 */
	public void requery()
	{
		new QueryTask().execute( getDataUrl() );
	}


	private class QueryTask extends AsyncTask< String, Integer, String >
	{

		@Override
		protected String doInBackground( String... params )
		{
			final String url = params[ 0 ];
			try
			{
				final CredentialsProvider cprov = new BasicCredentialsProvider();
				final String username = OpenMMUtil.getServerUsername( BaseDynamicActivity.this );
				final String password = OpenMMUtil.getServerPassword( BaseDynamicActivity.this );
				cprov.setCredentials( new AuthScope( AuthScope.ANY_HOST, AuthScope.ANY_PORT ), new UsernamePasswordCredentials( username, password ) );
				final DefaultHttpClient client = new DefaultHttpClient();
				client.setCredentialsProvider( cprov );
				final HttpRequestBase request = new HttpGet( url );

				Log.d( TAG, "Downloading " + url );
				final HttpResponse resp = client.execute( request );
				if ( resp.getStatusLine().getStatusCode() == 200 )
				{
					final HttpEntity entity = resp.getEntity();
					final ByteArrayOutputStream buffer = new ByteArrayOutputStream( 1024 );
					IOUtils.copy( entity.getContent(), buffer );

					// write to cache if this page enables it
					if ( shouldCacheResponse() )
					{
						CacheUtil.writeToCache( BaseDynamicActivity.this, getDataUrl(), buffer.toByteArray() );
					}

					//Log.d( TAG, "Contents: " + buffer.toString() );

					return buffer.toString();
				}
				else
				{
					Log.e( TAG,"Failed to download " + url + ", Response code: " + resp.getStatusLine().getStatusCode() );
				}

			}
			catch ( Exception e )
			{
				Log.e( TAG, "Failed to get " + url, e );
			}
			return null;
		}

		@Override
		protected void onPostExecute( String result )
		{
			processXml( result );
		}
	}

	/**
	 * Get the url for this page view
	 * @return
	 */
	protected abstract String getDataUrl();

	/**
	 * Get the layout for the row
	 * @return
	 */
	protected abstract int getRowLayout();

	/**
	 * Get the header layout if there is one
	 * @return
	 */
	protected int getHeaderLayout()
	{
		return 0;
	}

	/**
	 * Inflate a layout
	 * @param inflator
	 * @param layoutId
	 * @return
	 */
	protected View inflateLayout( LayoutInflater inflator, int layoutId )
	{
		return inflator.inflate( layoutId, null );
	}

	/**
	 * Create the tag object for your view
	 * @param view
	 * @return
	 */
	protected Object createLayoutTag( View view )
	{
		return null;
	}

	/**
	 * Apply the row object to the view using the tag
	 * @param obj
	 * @param view
	 * @param tag
	 */
	protected abstract void applyDataToRow( E obj, View view, Object tag );

	/**
	 * Get the class for the response object
	 * @return
	 */
	protected abstract Class< F > getResponseClass();

	/**
	 * Handle the response
	 * @param response
	 */
	protected abstract void handleResponse( F response );

	private void processXml( String xml )
	{
		//Log.d( TAG, "xml=" + xml );
		if ( xml != null && xml.length() > 0 )
		{
			Serializer ser = new Persister();
			try
			{
				response = ser.read( getResponseClass(), xml );
				handleResponse( response );
				adapter.notifyDataSetChanged();
			}
			catch ( Exception e )
			{
				Log.e( TAG, "Failed to process response", e );
			}
		}
	}

	@SuppressWarnings( "unchecked" )
	protected final void onListItemClick( AbsListView l, View v, int position, long id )
	{
		Log.d( TAG, "Selected " + position );
		final E obj = (E)l.getItemAtPosition( position );
		if ( obj != null )
		{
			onListItemClick( obj, position );
		}
	}

	/**
	 * Handle the click
	 * @param item
	 * @param position
	 */
	protected void onListItemClick( E item, int position )
	{

	}



}
