package com.bluerocket.map;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import android.R.string;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends FragmentActivity implements
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{


	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	private static final String DEFAULT = "N/A";
	public static Boolean off_vibrator=false;
	GoogleMap mMap;
    public Vibrator vibrator;


	@SuppressWarnings("unused")
	private static final double JALGAON_LAT = 21.013321, 
	JALGAON_LNG =75.563972;
	
	private static final float DEFAULTZOOM = 15;
	LocationClient mLocationClient;
	Marker marker;
	Circle shape;
	Location currentLoc;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(isNetworkAvailable()){
			if (servicesOK()) {
				setContentView(R.layout.activity_map);
	
				if(initMap()){
					Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
					mMap.setMyLocationEnabled(true);
					mLocationClient = new LocationClient(this, this, this);
					mLocationClient.connect();
					if (mLocationClient == null){
					currentLoc = mLocationClient.getLastLocation();
					if (currentLoc == null){
						gotoLocation(JALGAON_LAT, JALGAON_LNG, DEFAULTZOOM);
						Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
					}else{
						LatLng ll = new LatLng(currentLoc.getLatitude(), currentLoc.getLongitude());
						gotoLocation(currentLoc.getLatitude(), currentLoc.getLongitude(), DEFAULTZOOM);
					}
					}else {
						gotoLocation(JALGAON_LAT, JALGAON_LNG, DEFAULTZOOM);
					}
//					if(isNetworkAvailable()){
//						gotoCurrentLocation();
//					}else{
//						Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
//					}
				}else{
					Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
				}
			}
			else {
				setContentView(R.layout.activity_main);
			}
		}else{
			Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			if(isNetworkAvailable()){
				mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.mapTypeNormal:
			if(isNetworkAvailable()){
				mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.mapTypeSatellite:
			if(isNetworkAvailable()){
				mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.mapTypeTerrain:
			if(isNetworkAvailable()){
				mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.mapTypeHybrid:
			if(isNetworkAvailable()){
				mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.gotoCurrentLocation:
			if(isNetworkAvailable()){
				gotoCurrentLocation();
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;

		case R.id.createNote:
			if(isNetworkAvailable()){
				if(marker == null){
					Toast.makeText(this, "Please select location first !!!", Toast.LENGTH_SHORT).show();
				}else{
				create_note();
				}
			}else{
				Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean servicesOK()
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

	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	private boolean initMap() {
		if (mMap == null) {
			SupportMapFragment mapFrag =
					(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mMap = mapFrag.getMap();

			if (mMap != null) {
				mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

					@Override
					public View getInfoWindow(Marker arg0) {
						return null;
					}

					@Override
					public View getInfoContents(Marker marker) {
						View v = getLayoutInflater().inflate(R.layout.info_window, null);
						TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
						TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
						TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
						TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);


						LatLng ll = marker.getPosition();

						tvLocality.setText(marker.getTitle());
						tvLat.setText("Latitude: " + ll.latitude);
						tvLng.setText("Longitude: " + ll.longitude);
						tvSnippet.setText(marker.getSnippet());



						return v;
					}
				});

				mMap.setOnMapLongClickListener(new OnMapLongClickListener() {

					@Override
					public void onMapLongClick(LatLng ll) {
						Geocoder gc = new Geocoder(MainActivity.this);
						List<Address> list = null;

						try {
							list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						Address add = list.get(0);
						MainActivity.this.setMarker(add.getLocality(), add.getCountryName(), 
								ll.latitude, ll.longitude);

					}
				});

				mMap.setOnMarkerClickListener(new OnMarkerClickListener() {

					@Override
					public boolean onMarkerClick(Marker marker) {
						String msg = marker.getTitle() + " (" + marker.getPosition().latitude + 
								"," + marker.getPosition().longitude + ")";
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
						return false;
					}
				});

				mMap.setOnMarkerDragListener(new OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						Geocoder gc = new Geocoder(MainActivity.this);
						List<Address> list = null;
						LatLng ll = marker.getPosition();
						try {
							list = gc.getFromLocation(ll.latitude, ll.longitude, 1);
						} catch (IOException e) {
							e.printStackTrace();
							return;
						}

						Address add = list.get(0);
						marker.setTitle(add.getLocality());
						marker.setSnippet(add.getCountryName());
						marker.showInfoWindow();
					}

					@Override
					public void onMarkerDrag(Marker arg0) {
						// TODO Auto-generated method stub

					}
				});

			}
		}
		return (mMap != null);
	}

//	private void gotoLocation(double lat, double lng) {
//		LatLng ll = new LatLng(lat, lng);
//		CameraUpdate update = CameraUpdateFactory.newLatLng(ll);
//		mMap.moveCamera(update);
//	}

	private void gotoLocation(double lat, double lng,
			float zoom) {
		LatLng ll = new LatLng(lat, lng);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update);
	}

	public void geoLocate(View v) throws IOException {
		hideSoftKeyboard(v);

		EditText et = (EditText) findViewById(R.id.editText1);
		String location = et.getText().toString();

		Geocoder gc = new Geocoder(this);
		List<Address> list = gc.getFromLocationName(location, 1);
		Address add = list.get(0);
		String locality = add.getLocality();
		Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

		double lat = add.getLatitude();
		double lng = add.getLongitude();

		gotoLocation(lat, lng, DEFAULTZOOM);
		if (marker != null) {
			marker.remove();
		}
		MarkerOptions options = new MarkerOptions()
		.title(locality)
		.position(new LatLng(lat, lng));
		marker = mMap.addMarker(options);

	}

	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if(isNetworkAvailable()){
			MapStateManager mgr = new MapStateManager(this);
			mgr.saveMapState(mMap);
		}else{
		Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
	}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(isNetworkAvailable()){
			MapStateManager mgr = new MapStateManager(this);
			CameraPosition position = mgr.getSavedCameraPosition();
			if (position != null) {
				CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
				mMap.moveCamera(update);
				//			This is part of the answer to the code challenge
				mMap.setMapType(mgr.getSavedMapType());
			}
		}else{
			Toast.makeText(this, "Internet not available!", Toast.LENGTH_SHORT).show();
		}
	}

	protected void gotoCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
		}
		else {
			LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
			mMap.animateCamera(update);
		}
	}



	@Override
	public void onConnected(Bundle connectionHint) {
		Toast.makeText(this, "Connected to location service", Toast.LENGTH_SHORT).show();
		LocationRequest request = LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.getFastestInterval();
		mLocationClient.requestLocationUpdates(request, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
//				String msg = "Location: " + location.getLatitude() + "," + location.getLongitude();
//				Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
		SharedPreferences sharedPreferences=getSharedPreferences("SaveData", Context.MODE_PRIVATE);
//		String lat=sharedPreferences.getString("lat", DEFAULT); 
		double lat = Double.longBitsToDouble(sharedPreferences.getLong("lat", 0));
//		String lng=sharedPreferences.getString("lng", DEFAULT); 
		String message=sharedPreferences.getString("message", DEFAULT); 
		double lng = Double.longBitsToDouble(sharedPreferences.getLong("lng", 0));
		if (lat == 0 || lng == 0 || message.equals(DEFAULT)) {
			Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
			
		}else {
//			String latDecrease = Float.toString((float) (Float.parseFloat(lat) - 0.090));
			
//			DecimalFormat dFormat = new DecimalFormat("#.####"); 
//			Double Latitude = Double.valueOf(dFormat.format(location.getLatitude()));
//			Double Longitude = Double.valueOf(dFormat.format(location.getLongitude()));
//			Toast.makeText(this, "Match Found"  + Double.parseDouble(lat) + "" + Double.parseDouble(lng) , Toast.LENGTH_SHORT).show();
			
//		
			double Latitude = location.getLatitude();
			double Longitude = location.getLongitude();
//		if (distance(lat, lng, Latitude, Longitude) < 0.1 && distance(lat, lng, Latitude, Longitude) > 0.1) {
			double dist = distance(lat, lng, Latitude, Longitude);	
			Toast.makeText(this, "" + dist, Toast.LENGTH_SHORT).show();
//			}
			
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
			// set title
			alertDialogBuilder.setTitle("Notification");
			
			// set dialog message
			alertDialogBuilder
			.setMessage(message)
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
//					OFF_VIBRATION  = true;
					
					off_vibrator = true;
				}
			})
			.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, just close
					// the dialog box and do nothing
					dialog.cancel();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			
			if(dist<0.08000 && off_vibrator==false){
					Toast.makeText(MainActivity.this, "Location found", Toast.LENGTH_LONG).show();
					//Start the vibration
			        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
			        vibrator.vibrate(2500);
			     // show alert box 
					alertDialog.show();
				// for ringtone
					Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
					r.play();
			}
		}
	}

	private double distance(double lat, double lng, double Latitude, double Longitude) {
		
	    double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output

	    double dLat = Math.toRadians(Latitude-lat);
	    double dLng = Math.toRadians(Longitude-lng);

	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);

	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	        * Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(Latitude));

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    double dist = earthRadius * c;

	    return dist; // output distance, in MILES
	}
	
	private void setMarker(String locality, String country, double lat, double lng) {
		LatLng ll = new LatLng(lat,lng);

		MarkerOptions options = new MarkerOptions()	
		.title(locality)
		.position(new LatLng(lat,lng))
		.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mapmarker))
		//		.icon(BitmapDescriptorFactory.defaultMarker())
		.anchor(.5f, .5f)
		.draggable(true);
		if (country.length() > 0) {
			options.snippet(country);
		}

		if (marker != null) {
			removeEverything();
		}

		marker = mMap.addMarker(options);
		shape = drawCircle(ll);

	}

	private Circle drawCircle(LatLng ll) {
		CircleOptions options = new CircleOptions()
		.center(ll)
		.radius(500)
		.fillColor(0x330000FF)
		.strokeColor(Color.BLUE)
		.strokeWidth(3);
		return mMap.addCircle(options);
	}

	private void removeEverything() {
		marker.remove();
		marker = null;
		shape.remove();
		shape = null;
	}

	public void create_note(){
		setContentView(R.layout.message_save);
		TextView lat = (TextView) findViewById(R.id.lat);
		TextView lng = (TextView) findViewById(R.id.lng);
		LatLng ll = marker.getPosition();

		lat.setText("Latitude: " + ll.latitude);
		lng.setText("Longitude: " + ll.longitude);

	}

//	if (marker != null) {
//		button.setVisibility(1);
//	}else {
//		button.setVisibility(2);
//	}
		public void saveNote(View v){
			//setContentView(R.layout.activity_map);
			LatLng ll = marker.getPosition();
			EditText message  = (EditText) findViewById(R.id.message);
			TextView lat = (TextView) findViewById(R.id.lat);
			TextView lng = (TextView) findViewById(R.id.lng);
			Log.d("nik", "SaveText");
			
			//to save note in shared preferances
			SharedPreferences sharedPreferences=getSharedPreferences("SaveData", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor=sharedPreferences.edit();
			editor.putLong("lat",  Double.doubleToLongBits(ll.latitude));
			editor.putLong("lng", Double.doubleToLongBits(ll.longitude));
			editor.putString("message", message.getText().toString());
			editor.commit();
		
			off_vibrator = false;
			Toast.makeText(this, "Note Created Sucessfully", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent(this,MainActivity.class);
			startActivity(intent);
			
		}
}
