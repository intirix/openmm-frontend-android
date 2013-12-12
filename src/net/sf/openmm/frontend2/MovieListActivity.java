package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.intirix.openmm.server.api.MovieListResponse;
import com.intirix.openmm.server.api.beans.MovieDetails;

public class MovieListActivity extends BaseDynamicActivity< MovieDetails, MovieListResponse >
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}



	@Override
	protected String getDataUrl()
	{
		return OpenMMUtil.getServerUrl( this ) + "/api/get/movies/list/" + getIntent().getStringExtra( "PREFIX" ) + ".xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.movie_list_row;
	}



	@Override
	protected void applyDataToRow( MovieDetails obj, View view, Object tag )
	{
		final TextView label = (TextView)view.findViewById( R.id.movie_list_label );
		label.setHeight( getTextViewHeight() );
		final String year = obj.getMovie().getYear();
		if ( year != null && year.length() > 0 )
		{
			label.setText( obj.getMovie().getDisplayName() + " (" + year + ')' );
		}
		else
		{
			label.setText( obj.getMovie().getDisplayName() );
		}
		label.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		label.setGravity( Gravity.CENTER_VERTICAL );

		final TextView avail = (TextView)view.findViewById( R.id.movie_list_avail );
		if ( obj.isAvailable() )
		{
			avail.setText( "" );
		}
		else
		{
			avail.setText( "Unavailable" );
		}

		final RatingBar ratingBar = (RatingBar)view.findViewById( R.id.movie_list_rating );
		float rating = 0.0f;
		try
		{
			rating = Float.parseFloat( obj.getMovie().getRating() ) / 2.0f;
		}
		catch ( Exception e )
		{
			// ignore the error, default to 0
		}
		ratingBar.setRating( rating );
		//count.setText( obj.getNumEpisodesAvailable() + "/" + obj.getNumEpisodes() );
	}

	@Override
	protected Class< MovieListResponse > getResponseClass()
	{
		return MovieListResponse.class;
	}

	@Override
	protected void handleResponse( MovieListResponse response )
	{

		listItems.clear();
		listItems.addAll( Arrays.asList( response.getMovies() ) );
	}

	@Override
	protected void onListItemClick( MovieDetails item, int position )
	{
		if ( item.isAvailable() )
		{
			final Intent i = new Intent( this, MovieDetailsActivity.class );
			i.putExtra( "MOVIE_ID", item.getMovie().getId() );
			i.putExtra( "TITLE", item.getMovie().getDisplayName() );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
	}


}
