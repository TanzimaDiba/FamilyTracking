package com.example.diba.applicationparentchild;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChildMap extends FragmentActivity implements LocationListener, Chronometer.OnChronometerTickListener {

    private GoogleMap mMap;
    UiSettings mUiSettings;
    LocationManager locationManager;
    Location mLocation;
    private static final long MIN_TIME = 0;
    private static final float MIN_DISTANCE = 0;

    ArrayList<Double> lat = new ArrayList<Double>();
    ArrayList<Double> lon = new ArrayList<Double>();

    Chronometer childTimer;
    TextView tvChild;

    int counter;
    long chTime;

    String parentEmail,childName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_map);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        tvChild = (TextView) findViewById(R.id.textView_child);
        tvChild.setText(childName);
        counter = 1;

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMyLocationEnabled(true);

        mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMapToolbarEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setCompassEnabled(true);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        mLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        childTimer = (Chronometer) findViewById(R.id.childTimer);
        childTimer.setOnChronometerTickListener(this);
        childTimer.setBase(SystemClock.elapsedRealtime());
        childTimer.start();
    }

    @Override
    public void onLocationChanged(Location mLocation) {

        double myLatitude = mLocation.getLatitude();
        double myLongitude = mLocation.getLongitude();
        LatLng myLatLng = new LatLng(myLatitude, myLongitude);
        toSaveLatLng(myLatitude, myLongitude);

        mMap.addMarker(new MarkerOptions().position(myLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
                .title("My location :" + myLatLng));

        if(counter == 1) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 12);
            mMap.animateCamera(cameraUpdate);
            new AsyncChildLocation().execute(myLatitude, myLongitude);
        }
    }

    private void toSaveLatLng(double myLatitude, double myLongitude) {
        lat.add(myLatitude);
        lat.add(22.865102);//Daulatpur
        lat.add(22.862097);//BIDC road
        lat.add(22.841058);//Boira
        lat.add(22.767);//KU

        lon.add(myLongitude);
        lon.add(89.514444);
        lat.add(89.535558);
        lat.add(89.522683);
        lat.add(89.419);
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        chTime = (SystemClock.elapsedRealtime()-childTimer.getBase())/1000;
        if(chTime == 90){
            childTimer.stop();
            toAddMapPolyLine();
        }
        else if(chTime !=0 && chTime%30 == 0){
            toAddMapPolyLine();
        }
    }

    private void toAddMapPolyLine() {

        String myurl = makeURL(lat.get(counter-1), lon.get(counter-1), lat.get(counter), lon.get(counter));
        new connectAsyncTask(myurl).execute();

        new AsyncChildLocation().execute(lat.get(counter), lon.get(counter));
        counter++;
    }

    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        return urlString.toString();
    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String>
    {
        String url;

        connectAsyncTask(String urlPass)
        {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            RouteJSONParser jParser = new RouteJSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                drawPath(result);
            }
        }
    }

    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            for (int z = 0; z < list.size() - 1; z++) {
                LatLng src = list.get(z);
                LatLng dest = list.get(z + 1);
                Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude))
                                                                      .width(4).color(Color.BLUE)
                                                                      .geodesic(true));
            }

        } catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    protected class AsyncChildLocation extends AsyncTask<Double, Void, Void> {
        @Override
        protected Void doInBackground(Double... params) {
            RestAPI api = new RestAPI();
            try {
                api.ChildLocation(parentEmail, childName, params[0], params[1]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncAChildLocation", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(ChildMap.this,"Location Updated.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        new AsyncChildOff().execute(parentEmail, childName, "off");
        new AsyncChildLocation().execute(0.0, 0.0);
    }

    protected class AsyncChildOff extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                api.ChildOnOff(params[0],params[1],params[2]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncChildOn", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(ChildMap.this, "Child tracking is off.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ChildMap.this, ChildActivity.class);
            i.putExtra("email",parentEmail);
            i.putExtra("child",childName);
            startActivity(i);
        }
    }

}
