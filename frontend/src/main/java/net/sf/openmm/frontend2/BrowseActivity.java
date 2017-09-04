package net.sf.openmm.frontend2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intirix.openmm.server.api.BrowseResponse;
import com.intirix.openmm.server.api.beans.Entry;
import com.intirix.openmm.server.api.beans.FileEntry;
import com.intirix.openmm.server.api.beans.FolderEntry;

public class BrowseActivity extends BaseDynamicActivity< Entry, BrowseResponse >
{
	private static final String TAG = BrowseActivity.class.getName();


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}



	@Override
	protected String getDataUrl()
	{
		String path = getPath();
		if ( path == null )
		{
			path = "/";
		}
		try
		{
			path = URLEncoder.encode( path, "UTF-8" );
		}
		catch ( UnsupportedEncodingException e )
		{
			// ignore
		}
		return OpenMMUtil.getServerUrl( this ) + "/api/get/browse/list.xml?path=" + path;
	}



	private String getPath()
	{
		String path = getIntent().getStringExtra( "PATH" );
		if ( path == null )
		{
			path = "/";
		}
		return path;
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.browse_row;
	}

	@Override
	protected void applyDataToRow( Entry obj, View view, Object tag )
	{
		final ImageView iv = (ImageView)view.findViewById( R.id.browse_image );

		iv.getLayoutParams().height = getTextViewHeight();
		iv.getLayoutParams().width = getTextViewHeight();
		
		if ( obj instanceof FileEntry )
		{
			iv.setVisibility( View.INVISIBLE );
		}
		else
		{
			iv.setVisibility( View.VISIBLE );
		}

		final TextView v = (TextView)view.findViewById( R.id.browse_name );
		v.setHeight( getTextViewHeight() );
		v.setText( obj.getName() );
		v.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		v.setGravity( Gravity.CENTER_VERTICAL );
	}

	@Override
	protected Class< BrowseResponse > getResponseClass()
	{
		return BrowseResponse.class;
	}

	@Override
	protected void handleResponse( BrowseResponse response )
	{
		listItems.clear();
		listItems.addAll( Arrays.asList( response.getFolders() ) );
		listItems.addAll( Arrays.asList( response.getFiles() ) );
	}



	@Override
	protected void onListItemClick( Entry item, int position )
	{
		//final int showId = item.getId();

		if ( item instanceof FolderEntry )
		{
			final Intent i = new Intent( this, BrowseActivity.class );
			i.putExtra( "PATH", getPath() + item.getName() + '/' );
			i.putExtra( "TITLE", item.getName() );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
		else
		{
			final String ext = item.getName().replaceFirst( ".*\\.", "" );
			
			String url = getPath() + '/' + item.getName();
			try
			{
				url = URLEncoder.encode( url, "UTF-8" );
			}
			catch ( UnsupportedEncodingException e )
			{
				Log.w( TAG, "Failed to encode url", e );
			}
			url = OpenMMUtil.getServerUrl( this ) + "/download" + url;

			
			
			OpenMMUtil.playUrl( this, url, ext );
		}

	}




}
