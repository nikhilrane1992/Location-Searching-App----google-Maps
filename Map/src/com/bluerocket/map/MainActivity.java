package com.bluerocket.map;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;


public class MainActivity extends FragmentActivity {
	GoogleMap mMap;
	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (serviceOK()){
			Toast.makeText(this, "Ready to Use MAP", Toast.LENGTH_SHORT).show();
			setContentView(R.layout.activity_map);
		}
		else{

			setContentView(R.layout.activity_main);
		}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public boolean serviceOK()
	{
		int isAvailables = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (isAvailables == ConnectionResult.SUCCESS){
			return true;
		}else if(GooglePlayServicesUtil.isUserRecoverableError(isAvailables)){
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailables, this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		}
		else{
			Toast.makeText(this, "Google service not available", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
}
