package net.sf.openmm.frontend2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity implements OnItemClickListener
{
	
	private final ArrayList< String > listItems = new ArrayList< String >( 5 );

	private static final String TAG = MainActivity.class.getName();


	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.main );
		
        listItems.add( "Browse" );
        listItems.add( "Shows" );
        listItems.add( "Config" );
        
        final int TEXT_VIEW_HEIGHT = OpenMMUtil.getTextViewHeight( this );
        final int TEXT_MAX_HEIGHT = OpenMMUtil.getTextMaxHeight( this );
        
		final ListView listView = getListView();
		listView.setAdapter( new ArrayAdapter< String >( this,
                android.R.layout.simple_list_item_1,
                listItems ) {

					@Override
					public View getView(int position, View convertView, ViewGroup parent)
					{
						final TextView v = new TextView( MainActivity.this );
						v.setHeight( TEXT_VIEW_HEIGHT );
						v.setText( listItems.get( position ) );
						v.setTextColor( 0xFFFFFFFF );
						v.setTextSize( TypedValue.COMPLEX_UNIT_PX, TEXT_MAX_HEIGHT );
						v.setGravity( Gravity.CENTER_VERTICAL );
						return v;
					}
        	
        } );
		
		//listView.setOnItemSelectedListener( this );
		listView.setOnItemClickListener( this );
		
	}

	public ListView getListView()
	{
		final ListView listView = (ListView)findViewById( android.R.id.list );
		return listView;
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate( R.menu.main, menu );
		return true;
	}
	
	
	
//	public void onItemSelected( AdapterView< ? > parent, View view, int position, long id )




	@Override
	public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
	{
		Log.d( TAG, "onItemClick(" + position + ')' );
		if ( position == 0 )
		{
			final Intent i = new Intent( this, BrowseActivity.class );
			i.putExtra( "TITLE", "/" );
			startActivity( i );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
		else if ( position == 1 )
		{
			startActivity( new Intent( this, ShowListActivity.class ) );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
		else if ( position == 2 )
		{
			startActivity( new Intent( this, ConfigActivity.class ) );
			overridePendingTransition( android.R.anim.slide_in_left, android.R.anim.slide_out_right );
		}
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		Log.i( TAG, "keyCode=" + keyCode );
		if ( keyCode == KeyEvent.KEYCODE_PAGE_DOWN )
		{
			int pos = getListView().getSelectedItemPosition();
			getListView().setSelection( pos + 10 );
		}
		else if ( keyCode == KeyEvent.KEYCODE_PAGE_UP )
		{
			int pos = getListView().getSelectedItemPosition();
			getListView().setSelection( pos + 10 );
		}
		else if ( keyCode == KeyEvent.KEYCODE_F1 )
		{
			getListView().setSelection( listItems.size() );
		}
		
		return super.onKeyDown(keyCode, event);
	}


}
