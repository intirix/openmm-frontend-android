package net.sf.openmm.frontend2;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ConfigActivity extends Activity implements OnClickListener
{

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_config );
		
		final EditText txt1 = (EditText)findViewById( R.id.config_edit_serverurl );
		txt1.setText( OpenMMUtil.getServerUrl( this ) );

		final EditText txtUsername = (EditText)findViewById( R.id.config_edit_username );
		txtUsername.setText( OpenMMUtil.getServerUsername( this ) );
		
		final EditText txtPassword = (EditText)findViewById( R.id.config_edit_password );
		txtPassword.setText( OpenMMUtil.getServerPassword( this ) );
		

		final EditText txt2 = (EditText)findViewById( R.id.config_edit_rows );
		txt2.setText( "" + OpenMMUtil.getDisplayRows( this ) );
		
		final Button saveButton = (Button)findViewById( R.id.config_edit_save );
		saveButton.setOnClickListener( this );
		
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate( R.menu.config, menu );
		return true;
	}

	@Override
	public void onClick( View v )
	{
		final EditText txt1 = (EditText)findViewById( R.id.config_edit_serverurl );
		final EditText txtUsername = (EditText)findViewById( R.id.config_edit_username );
		final EditText txtPassword = (EditText)findViewById( R.id.config_edit_password );
		final EditText txt2 = (EditText)findViewById( R.id.config_edit_rows );
		
		final SharedPreferences prefs = OpenMMUtil.getPrefs( this );
		final Editor editor = prefs.edit();
		editor.putString( "server.url", txt1.getText().toString() );
		editor.putString( "server.username", txtUsername.getText().toString() );
		editor.putString( "server.password", txtPassword.getText().toString() );
		editor.putInt( "display.rows", Integer.parseInt( txt2.getText().toString() ) );
		editor.commit();
		
		final Intent i = new Intent( this, MainActivity.class );
		i.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK );
		startActivity( i );
	}

	
}
