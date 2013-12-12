package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.intirix.openmm.server.api.MoviePrefixListResponse;
import com.intirix.openmm.server.api.beans.MoviePrefixCounts;

public class MoviePrefixListActivity extends BaseDynamicActivity< MoviePrefixCounts, MoviePrefixListResponse >
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setTitle( "Movies" );
	}



	@Override
	protected String getDataUrl()
	{
		return OpenMMUtil.getServerUrl( this ) + "/api/get/movies/prefix/list/all.xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.movie_prefix_list_row;
	}
	




	@Override
	protected void applyDataToRow( MoviePrefixCounts obj, View view, Object tag )
	{
		final TextView label = (TextView)view.findViewById( R.id.movie_prefix_list_label );
		label.setHeight( getTextViewHeight() );
		label.setText( obj.getPrefix() );
		label.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		label.setGravity( Gravity.CENTER_VERTICAL );
		
		final TextView count = (TextView)view.findViewById( R.id.movie_prefix_list_count );
		count.setText( obj.getNumMoviesAvailable() + "/" + obj.getNumMovies() );
	}

	@Override
	protected Class< MoviePrefixListResponse > getResponseClass()
	{
		return MoviePrefixListResponse.class;
	}

	@Override
	protected void handleResponse( MoviePrefixListResponse response )
	{
		listItems.clear();
		listItems.addAll( Arrays.asList( response.getPrefixes() ) );
	}



	@Override
	protected void onListItemClick( MoviePrefixCounts item, int position )
	{
		final Intent i = new Intent( this, MovieListActivity.class );
		i.putExtra( "PREFIX", item.getPrefix() );
		i.putExtra( "TITLE", "Movies - " + item.getPrefix() );
		startActivity( i );
		overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
	}




}
