package com.example.diba.applicationparentchild;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ParentViewMap extends FragmentActivity implements Chronometer.OnChronometerTickListener {

    private GoogleMap mMap;
    UiSettings mUiSettings;

    Chronometer parentTimer;
    TextView tvChild;

    int counter;
    long chTime;

    String parentEmail,childName,currentDate;
    ArrayList<Double> lat = new ArrayList<Double>();
    ArrayList<Double> lon = new ArrayList<Double>();
    ArrayList<String> locationName = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_view_map);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        parentTimer = (Chronometer) findViewById(R.id.parentTimer);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        currentDate = dateFormat.format(today);

        tvChild = (TextView) findViewById(R.id.tvChild);
        tvChild.setText(childName);
        counter = 1;

        Toast.makeText(ParentViewMap.this,"Map Loading..",Toast.LENGTH_SHORT).show();
        setUpMapIfNeeded();
    }


    @Override
    protected void onResume() {
        super.onResume();
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

        Toast.makeText(ParentViewMap.this,"Map Load Complete.",Toast.LENGTH_SHORT).show();
        new AsyncRecordDetails().execute();
    }

    protected class AsyncRecordDetails extends AsyncTask<Void, JSONObject, ArrayList<RecordTable>> {
        ArrayList<RecordTable> recordTable = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected ArrayList<RecordTable> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            RestAPI api = new RestAPI();
            try {

                JSONObject jsonObj = api.RecordDetails();
                JSONParser parser = new JSONParser();
                recordTable = parser.parseRecord(jsonObj);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncLoadChildDetails", e.getMessage());
            }
            return recordTable;
        }

        protected void onPostExecute(ArrayList<RecordTable> result) {
            // TODO Auto-generated method stub
            String sqlDate, sqlEmail, sqlChild;
            for (int i = 0; i < result.size(); i++) {
                sqlDate = result.get(i).getRecordDate();
                sqlEmail = result.get(i).getRecordEmail();
                sqlChild = result.get(i).getRecordChildName();

                if(sqlDate.equals(currentDate) && sqlEmail.equals(parentEmail) && sqlChild.equals(childName)){
                    locationName.add(result.get(i).getLocationName());
                    lat.add(result.get(i).getLat());
                    lon.add(result.get(i).getLon());

                    if(i==0) {
                        LatLng myLatLng = new LatLng(lat.get(i), lon.get(i));
                        mMap.addMarker(new MarkerOptions().position(myLatLng)
                                .title("Location :" + locationName.get(i))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                                .draggable(true));
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(myLatLng, 10);
                        mMap.animateCamera(cameraUpdate);
                    }
                    else{
                        String myurl = makeURL(lat.get(i-1), lon.get(i-1), lat.get(i), lon.get(i));
                        new connectAsyncTask(myurl).execute();
                    }
                }
            }
        }
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
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass)
        {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(ParentViewMap.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
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
            progressDialog.hide();
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
                Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude)).width(4).color(Color.BLUE).geodesic(true));
            }

        } catch (JSONException e) {

        }finally {
            parentTimer.setOnChronometerTickListener(this);
            parentTimer.setBase(SystemClock.elapsedRealtime());
            parentTimer.start();
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

    public void onChronometerTick(Chronometer chronometer) {
        chTime = (SystemClock.elapsedRealtime()-parentTimer.getBase())/1000;
        if(chTime == 75){
            parentTimer.stop();
            new AsyncChildLocation().execute();
        }
        else if(chTime%15 == 0){
            new AsyncChildLocation().execute();
        }
    }

    protected class AsyncChildLocation extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;

        @Override
        protected ArrayList<ChildTable> doInBackground(Void... params) {
            // TODO Auto-generated method stub
            RestAPI api = new RestAPI();
            try {
                JSONObject jsonObj = api.ChildDetails();
                JSONParser parser = new JSONParser();
                childTable = parser.parseChild(jsonObj);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncChildLocation", e.getMessage());
            }
            return childTable;
        }

        protected void onPostExecute(ArrayList<ChildTable> result) {
            // TODO Auto-generated method stub
            String em,name;
            double childLat = 0;
            double childLon = 0;
            for (int i = 0; i < result.size(); i++) {
                em = result.get(i).getEmail();
                name = result.get(i).getChildName();
                if(em.equals(parentEmail) && name.equals(childName)){
                    childLat = result.get(i).getCurrentLat();
                    childLon = result.get(i).getCurrentLon();
                }
            }
            Toast.makeText(ParentViewMap.this,"Child Location Updated.",Toast.LENGTH_SHORT).show();
            toMapMarker(childLat,childLon);
        }
    }

    private void toMapMarker(double childLat, double childLon) {
        LatLng childLatLng = new LatLng(childLat, childLon);
        mMap.addMarker(new MarkerOptions().position(childLatLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .draggable(true)
                .title("Child location :" + childLatLng));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(childLatLng, 12);
        mMap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ParentViewMap.this, ParentActivity.class);
        i.putExtra("email",parentEmail);
        i.putExtra("child",childName);
        startActivity(i);
    }
}
