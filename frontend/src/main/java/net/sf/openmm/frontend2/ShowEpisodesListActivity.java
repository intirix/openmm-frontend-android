package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.intirix.openmm.server.api.ShowEpisodesListResponse;
import com.intirix.openmm.server.api.beans.EpisodeDetails;

public class ShowEpisodesListActivity extends BaseDynamicActivity< EpisodeDetails, ShowEpisodesListResponse >
{
	//private static final String TAG = ShowListActivity.class.getName();


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
	}



	@Override
	protected String getDataUrl()
	{
		return OpenMMUtil.getServerUrl( this ) + "/api/get/shows/episodes/list/" + getIntent().getIntExtra( "SEASON_ID", 0 ) + ".xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.show_episode_list_row;
	}
	
	

	@Override
	protected int getHeaderLayout()
	{
		return R.layout.show_season_list_header;
	}



	@Override
	protected void applyDataToRow( EpisodeDetails obj, View view, Object tag )
	{
		final TextView label = (TextView)view.findViewById( R.id.show_episode_list_label );
		label.setHeight( getTextViewHeight() );
		label.setText( obj.getEpisode().getEpNum() + ") " + obj.getEpisode().getName() );
		label.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		label.setGravity( Gravity.CENTER_VERTICAL );
		
		final TextView count = (TextView)view.findViewById( R.id.show_episode_list_count );
		if ( obj.getNumInternalLinks() == 0 && obj.getNumExternalLinks() == 0 )
		{
			count.setText( "Unavailable" );
		}
		else
		{
			count.setText( "" );
		}
		
		final RatingBar ratingBar = (RatingBar)view.findViewById( R.id.show_episode_list_rating );
		float rating = 0.0f;
		try
		{
			rating = Float.parseFloat( obj.getEpisode().getRating() ) / 2.0f;
		}
		catch ( Exception e )
		{
			// ignore the error, default to 0
		}
		ratingBar.setRating( rating );
		//count.setText( obj.getNumEpisodesAvailable() + "/" + obj.getNumEpisodes() );
	}

	@Override
	protected Class< ShowEpisodesListResponse > getResponseClass()
	{
		return ShowEpisodesListResponse.class;
	}

	@Override
	protected void handleResponse( ShowEpisodesListResponse response )
	{
		final String bannerPath = response.getShow().getBannerPath();
		if ( bannerPath != null && bannerPath.length() > 0 )
		{
			final ImageView iv = (ImageView)getHeaderView().findViewById( R.id.show_season_list_header_image );
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

		listItems.clear();
		listItems.addAll( Arrays.asList( response.getEpisodes() ) );
	}

	@Override
	protected void onListItemClick( EpisodeDetails item, int position )
	{
		final Intent i = new Intent( this, ShowEpisodeDetailsActivity.class );
		i.putExtra( "EPISODE_ID", item.getEpisode().getId() );
		i.putExtra( "TITLE", item.getEpisode().getName() );
		startActivity( i );
		overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
	}


}
