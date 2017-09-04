package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.intirix.openmm.server.api.SearchResponse;
import com.intirix.openmm.server.api.beans.SearchResult;

public class SearchActivity extends BaseDynamicActivity< SearchResult, SearchResponse >
{
	
	/**
	 * The query to perform
	 */
	private String query = "";
	
	/**
	 * Reference to the EditText that contains the query
	 */
	private EditText searchEditText = null;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		
		final Button searchButton = (Button)findViewById( R.id.searchButton );
		searchButton.setTextSize( getTextMaxHeight() * 0.75f );
		searchButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick( View v )
			{
				performSearch();
			}
		} );
		
		searchEditText = (EditText)findViewById( R.id.searchQuery );
		searchEditText.setTextSize( getTextMaxHeight() * 0.75f );
		searchEditText.addTextChangedListener( new TextWatcher() {
			
			@Override
			public void onTextChanged( CharSequence s, int start, int before, int count )
			{	
			}
			
			@Override
			public void beforeTextChanged( CharSequence s, int start, int count, int after )
			{
			}
			
			@Override
			public void afterTextChanged( Editable s )
			{
				performSearch();
			}
		} );
	}
	
	/**
	 * Perform the search
	 */
	private void performSearch()
	{
		query = searchEditText.getText().toString().trim();
		
		// only perform the query if the user actually typed soemthing in
		if ( query.length() > 2 )
		{
			requery();
		}
	}



	@Override
	protected boolean shouldCacheResponse()
	{
		// we don't want to cache the response because it is a post
		return false;
	}



	@Override
	protected boolean shouldLoadDataOnPageLoad()
	{
		// we don't want to load the data until the user hits the search button
		return false;
	}



	@Override
	protected String getDataUrl()
	{
		return OpenMMUtil.getServerUrl( this ) + "/api/get/search/query.xml?query=" + query;
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.search_list_row;
	}
	
	



	@Override
	protected int getHeaderLayout()
	{
		return R.layout.search_header;
	}



	@Override
	protected void applyDataToRow( SearchResult obj, View view, Object tag )
	{
		final TextView label = (TextView)view.findViewById( R.id.search_label );
		label.setText( obj.getDescription() );
//		label.setHeight( getTextViewHeight() );
		label.setTextSize( getTextMaxHeight() * 0.5f );
		label.setGravity( Gravity.CENTER_VERTICAL );
		label.setTextColor( Color.WHITE );

		/*
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
		 */
	}

	@Override
	protected Class< SearchResponse > getResponseClass()
	{
		return SearchResponse.class;
	}

	@Override
	protected void handleResponse( SearchResponse response )
	{

		listItems.clear();
		listItems.addAll( Arrays.asList( response.getResults() ) );
	}

	@Override
	protected void onListItemClick( SearchResult item, int position )
	{
		if ( "movie".equalsIgnoreCase( item.getType() ) )
		{
			final Intent i = new Intent( this, MovieDetailsActivity.class );
			i.putExtra( "MOVIE_ID", item.getRefId() );
			i.putExtra( "TITLE", item.getTitle() );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
		else if ( "show".equalsIgnoreCase( item.getType() ) )
		{
			final Intent i = new Intent( this, ShowSeasonListActivity.class );
			i.putExtra( "SHOW_ID", item.getRefId() );
			i.putExtra( "TITLE", item.getTitle() + " - Seasons" );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );

		}
		else if ( "episode".equalsIgnoreCase( item.getType() ) )
		{
			final Intent i = new Intent( this, ShowEpisodeDetailsActivity.class );
			i.putExtra( "EPISODE_ID", item.getRefId() );
			i.putExtra( "TITLE", item.getTitle() );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
	}


}
