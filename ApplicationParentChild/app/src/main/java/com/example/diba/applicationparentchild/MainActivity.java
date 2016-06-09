package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class MainActivity extends FragmentActivity implements LoaderCallbacks<Cursor> {

    GoogleMap mGoogleMap;
    ImageButton ibSearch,ibCalender, ibLocationList;
    TextView tvLabel, tvRecordDate;

    String parentEmail, password, childName, currentDate;

    ArrayList<Double> lat = new ArrayList<Double>();
    ArrayList<Double> lon = new ArrayList<Double>();
    String locationName = "";

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        new AsyncChildDetails().execute();

        tvLabel = (TextView) findViewById(R.id.tvLabel);
        tvRecordDate = (TextView) findViewById(R.id.tvRecordDate);

        Calendar calendar = Calendar.getInstance();
        Date today = calendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
        currentDate = dateFormat.format(today);

        tvRecordDate.setText("Record Date :" + currentDate);

        ibSearch = (ImageButton) findViewById(R.id.ibSearch);
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });

        ibCalender = (ImageButton) findViewById(R.id.ibCalender);
        ibCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSelectRecordDate();
            }
        });

        ibLocationList = (ImageButton) findViewById(R.id.ibLocationList);
        ibLocationList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ParentEditRoute.class);
                i.putExtra("email",parentEmail);
                i.putExtra("child",childName);
                startActivity(i);
            }
        });

        setUpMapIfNeeded();
        handleIntent(getIntent());
    }

    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mGoogleMap == null) {
            mGoogleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        }
    }

    protected class AsyncChildDetails extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
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
                Log.d("AsyncLoadChildDetails", e.getMessage());
            }
            return childTable;
        }

        protected void onPostExecute(ArrayList<ChildTable> result) {
            // TODO Auto-generated method stub
            String tempEmail,tempChild;
            for (int i = 0; i < result.size(); i++) {
                tempEmail = result.get(i).getEmail();
                tempChild = result.get(i).getChildName();
                if(tempEmail.equals(parentEmail) && tempChild.equals(childName)){
                    password = result.get(i).getPassword();
                }
            }
        }
    }

    private void toSelectRecordDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Select Record Date");
        final DatePicker date = new DatePicker(MainActivity.this);
        date.setCalendarViewShown(false);
        builder.setView(date);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                int year = date.getYear();
                int month = date.getMonth();
                int day = date.getDayOfMonth();

                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);
                DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
                currentDate = dateFormat.format(calendar.getTime());
                tvRecordDate.setText("Record Date :" + currentDate);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void handleIntent(Intent intent){
        if(intent.getAction().equals(Intent.ACTION_SEARCH)){
            doSearch(intent.getStringExtra(SearchManager.QUERY));
        }else if(intent.getAction().equals(Intent.ACTION_VIEW)){
            getPlace(intent.getStringExtra(SearchManager.EXTRA_DATA_KEY));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void doSearch(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(0, data, this);
    }

    private void getPlace(String query){
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().restartLoader(1, data, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:

                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public Loader<Cursor> onCreateLoader(int arg0, Bundle query) {
        CursorLoader cLoader = null;
        if(arg0==0)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.SEARCH_URI, null, null, new String[]{ query.getString("query") }, null);
        else if(arg0==1)
            cLoader = new CursorLoader(getBaseContext(), PlaceProvider.DETAILS_URI, null, null, new String[]{ query.getString("query") }, null);
        return cLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        showLocations(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }

    private void showLocations(Cursor c){
        MarkerOptions markerOptions = null;
        LatLng position = null;
        mGoogleMap.clear();
        while(c.moveToNext()){
            locationName = c.getString(0);
            markerOptions = new MarkerOptions();
            lat.add(Double.parseDouble(c.getString(1)));
            lon.add(Double.parseDouble(c.getString(2)));
            position = new LatLng(lat.get(index), lon.get(index));
            markerOptions.position(position);
            markerOptions.title(locationName);
            mGoogleMap.addMarker(markerOptions);
        }
        if(position!=null){
            CameraUpdate cameraPosition = CameraUpdateFactory.newLatLngZoom(position, 10);
            mGoogleMap.animateCamera(cameraPosition);
            tvLabel.setText(locationName);

            LocalDatabase ld = new LocalDatabase(this);
            ld.open();
            ld.initialEntry(currentDate, parentEmail, childName, locationName, lat.get(index), lon.get(index));
            ld.close();

            RecordTable recordDetail = new RecordTable(currentDate, parentEmail, childName, locationName, lat.get(index), lon.get(index));
            index ++;
            new AsyncAddRecord().execute(recordDetail);
        }
    }

    protected class AsyncAddRecord extends AsyncTask<RecordTable, Void, Void> {
        @Override
        protected Void doInBackground(RecordTable... params) {
            RestAPI api = new RestAPI();
            try {
                api.CreateRecord(params[0].getRecordDate(), params[0].getRecordEmail(), params[0].getRecordChildName(),
                                 params[0].getLocationName(), params[0].getLat(), params[0].getLon());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncAddRecord", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(MainActivity.this, "Location Added", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(MainActivity.this, ParentActivity.class);
        i.putExtra("email",parentEmail);
        i.putExtra("password",password);
        startActivity(i);
    }
}

