package net.sf.openmm.frontend2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.intirix.openmm.server.api.MovieDetailsResponse;
import com.intirix.openmm.server.api.beans.MediaLink;

public class MovieDetailsActivity extends BaseDynamicActivity< String, MovieDetailsResponse >
{
	private static final String TAG = ShowListActivity.class.getName();

	private MediaLink[] links = null;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		listItems.add( "Play" );
		super.onCreate( savedInstanceState );
	}



	@Override
	protected String getDataUrl()
	{
		return OpenMMUtil.getServerUrl( this ) + "/api/get/movies/get/" + getIntent().getIntExtra( "MOVIE_ID", 0 ) + ".xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.show_list_row;
	}



	@Override
	protected int getHeaderLayout()
	{
		return R.layout.movie_details_header;
	}



	@Override
	protected void applyDataToRow( String obj, View view, Object tag )
	{
		final TextView v = (TextView)view.findViewById( R.id.show_list_label );
		v.setHeight( getTextViewHeight() );
		v.setText( obj.toString() );
		v.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		v.setGravity( Gravity.CENTER_VERTICAL );

	}


	@Override
	protected Class< MovieDetailsResponse > getResponseClass()
	{
		return MovieDetailsResponse.class;
	}

	@Override
	protected void handleResponse( final MovieDetailsResponse response )
	{
		links = response.getMovie().getLinks();
		final String bannerPath = response.getMovie().getPosterUrl();
		if ( bannerPath != null && bannerPath.length() > 0 )
		{
			final ImageButton iv = (ImageButton)getHeaderView().findViewById( R.id.movie_details_screenshot );
			iv.setOnClickListener( new OnClickListener() {

				@Override
				public void onClick( View v )
				{
					playVideo();
				}
			} );
			final String bannerUrl = OpenMMUtil.getServerUrl( this ) + '/' + bannerPath;
			new DownloadImageTask(this) {

				@Override
				protected void onPostExecute( Bitmap result )
				{
					if ( result != null )
					{
						iv.setImageBitmap( result );
					}
				}

			}.execute( bannerUrl );
		}
		final int mediumTextSize = getTextMaxHeight() * 3 / 4;
		final int mediaTextHeight = getTextViewHeight() * 3 / 4;

		final TextView description = (TextView)getHeaderView().findViewById( R.id.movie_details_description );
		description.setText( response.getMovie().getDescription() );
		description.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );

		final TextView releaseLabel = (TextView)getHeaderView().findViewById( R.id.movie_details_release_label );
		releaseLabel.setHeight( mediaTextHeight );
		releaseLabel.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );
		final TextView mpaaLabel = (TextView)getHeaderView().findViewById( R.id.movie_details_mpaa_label );
		mpaaLabel.setHeight( mediaTextHeight );
		mpaaLabel.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );
		final TextView watchedLabel = (TextView)getHeaderView().findViewById( R.id.movie_details_watched_label );
		watchedLabel.setHeight( mediaTextHeight );
		watchedLabel.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );

		final TextView release = (TextView)getHeaderView().findViewById( R.id.movie_details_release );
		release.setText( response.getMovie().getReleaseDate() );
		release.setHeight( mediaTextHeight );
		release.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );

		final TextView mpaa = (TextView)getHeaderView().findViewById( R.id.movie_details_mpaa );
		mpaa.setText( response.getMovie().getMpaaRating() );
		mpaa.setHeight( mediaTextHeight );
		mpaa.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );

		final TextView watched = (TextView)getHeaderView().findViewById( R.id.movie_details_watched );
		if ( true )
		{
			watched.setVisibility( View.GONE );
			watchedLabel.setVisibility( View.GONE );
		}
		else
		{
			//watched.setText( response.getEpisode().getEpisode().getLastWatched() );
			watched.setHeight( mediaTextHeight );
			watched.setTextSize( TypedValue.COMPLEX_UNIT_PX, mediumTextSize );
			watched.setVisibility( View.VISIBLE );
			watchedLabel.setVisibility( View.VISIBLE );
		}

		final RatingBar ratingBar = (RatingBar)getHeaderView().findViewById( R.id.movie_details_rating );
		float rating = 0.0f;
		try
		{
			rating = Float.parseFloat( response.getMovie().getRating() ) / 2.0f;
		}
		catch ( Exception e )
		{
			// ignore
		}
		ratingBar.setRating( rating );



	}



	private void playVideo()
	{
		if ( links != null && links.length > 0 )
		{
			new UpdateServerTask( this ).execute( "action", "WatchMovie", "movieId", "" + getIntent().getIntExtra( "MOVIE_ID", 0 ) );
			
			
			String url = links[ 0 ].getUrl();
			final String ext = url.replaceFirst( ".*\\.", "" );
			
			if ( url.startsWith( "vfs://" ) )
			{
				url = url.replace( "vfs://", "" );
				try
				{
					url = URLEncoder.encode( url, "UTF-8" );
				}
				catch ( UnsupportedEncodingException e )
				{
					Log.w( TAG, "Failed to encode url", e );
				}
				url = OpenMMUtil.getServerAccessUrl( this ) + "/download" + url;
			}
			
			OpenMMUtil.playUrl( this, url, ext );
		}
	}



	@Override
	protected void onListItemClick( String item, int position )
	{
		if ( position == 1 )
		{
			playVideo();
		}
	}



}
