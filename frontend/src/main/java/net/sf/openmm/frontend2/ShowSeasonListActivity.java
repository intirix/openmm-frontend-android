package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.intirix.openmm.server.api.ShowSeasonListResponse;
import com.intirix.openmm.server.api.beans.SeasonDetails;

public class ShowSeasonListActivity extends BaseDynamicActivity< SeasonDetails, ShowSeasonListResponse >
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
		return OpenMMUtil.getServerUrl( this ) + "/api/get/shows/seasons/list/" + getIntent().getIntExtra( "SHOW_ID", 0 ) + ".xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.show_season_list_row;
	}
	
	

	@Override
	protected int getHeaderLayout()
	{
		return R.layout.show_season_list_header;
	}



	@Override
	protected void applyDataToRow( SeasonDetails obj, View view, Object tag )
	{
		final TextView label = (TextView)view.findViewById( R.id.show_season_list_label );
		label.setHeight( getTextViewHeight() );
		label.setText( obj.getSeason().getName() );
		label.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		label.setGravity( Gravity.CENTER_VERTICAL );
		
		final TextView count = (TextView)view.findViewById( R.id.show_season_list_count );
		count.setText( obj.getNumEpisodesAvailable() + "/" + obj.getNumEpisodes() );
	}

	@Override
	protected Class< ShowSeasonListResponse > getResponseClass()
	{
		return ShowSeasonListResponse.class;
	}

	@Override
	protected void handleResponse( ShowSeasonListResponse response )
	{
		final String bannerPath = response.getShow().getBannerPath();
		if ( bannerPath != null && bannerPath.length() > 0 )
		{
			final ImageView iv = (ImageView)getHeaderView().findViewById( R.id.show_season_list_header_image );
			final String bannerUrl = OpenMMUtil.getServerUrl( this ) + bannerPath;
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
		listItems.addAll( Arrays.asList( response.getSeasons() ) );
	}



	@Override
	protected void onListItemClick( SeasonDetails item, int position )
	{
		final Intent i = new Intent( this, ShowEpisodesListActivity.class );
		i.putExtra( "SEASON_ID", item.getSeason().getId() );
		i.putExtra( "TITLE", getResponse().getShow().getDisplayName() + " - " + item.getSeason().getName() );
		startActivity( i );
		overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
	}




}
