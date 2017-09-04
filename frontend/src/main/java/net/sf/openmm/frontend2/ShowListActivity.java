package net.sf.openmm.frontend2;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.intirix.openmm.server.api.ShowListResponse;
import com.intirix.openmm.server.api.beans.Show;

public class ShowListActivity extends BaseDynamicActivity< Show, ShowListResponse >
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
		return OpenMMUtil.getServerUrl( this ) + "/api/get/shows/list/all.xml";
	}

	@Override
	protected int getRowLayout()
	{
		return R.layout.show_list_row;
	}

	@Override
	protected void applyDataToRow( Show obj, View view, Object tag )
	{
		final TextView v = (TextView)view.findViewById( R.id.show_list_label );
		v.setHeight( getTextViewHeight() );
		v.setText( obj.toString() );
		v.setTextSize( TypedValue.COMPLEX_UNIT_PX, getTextMaxHeight() );
		v.setGravity( Gravity.CENTER_VERTICAL );
	}

	@Override
	protected Class< ShowListResponse > getResponseClass()
	{
		return ShowListResponse.class;
	}

	@Override
	protected void handleResponse( ShowListResponse response )
	{
		listItems.clear();
		listItems.addAll( Arrays.asList( response.getShows() ) );
	}



	@Override
	protected void onListItemClick( Show item, int position )
	{
		final int showId = item.getId();
		final Intent i = new Intent( this, ShowSeasonListActivity.class );
		i.putExtra( "SHOW_ID", showId );
		i.putExtra( "TITLE", item.getDisplayName() + " - Seasons" );
		startActivity( i );
		overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );

	}




}
