package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class ParentEditRoute extends ActionBarActivity implements AdapterView.OnItemClickListener {

    ListView listv;
    SimpleCursorAdapter dataAdapter;
    LocalDatabase dbHelper;

    String parentEmail, childName, currentDate;
    String locationName;
    int rowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_edit_route);

        listv = (ListView) findViewById(R.id.listViewRecord);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parent_edit_route, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.calender) {
            toSelectRecordDate();
        }

        return super.onOptionsItemSelected(item);
    }

    private void toSelectRecordDate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ParentEditRoute.this);
        builder.setTitle("Select Record Date");
        final DatePicker date = new DatePicker(ParentEditRoute.this);
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
                dialog.dismiss();

                displayListView();
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

    private void displayListView() {
        dbHelper = new LocalDatabase(this);
        dbHelper.open();
        Cursor cursor = dbHelper.getData(currentDate, childName);

        String[] columns = new String[] {LocalDatabase.KEY_DATE, LocalDatabase.KEY_LOCATION, LocalDatabase.KEY_LAT, LocalDatabase.KEY_LON};

        int[] to = new int[] {R.id.tv_sqlRecord, R.id.tv_sqlLocation, R.id.tv_sqlLat, R.id.tv_sqlLon};

        dataAdapter = new SimpleCursorAdapter(
                this, R.layout.layout_record,
                cursor,
                columns,
                to,
                0);

        dbHelper.close();
        listv.setAdapter(dataAdapter);
        listv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) parent.getItemAtPosition(position);
        String stringId = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
        rowId = Integer.parseInt(stringId);
        locationName = cursor.getString(cursor.getColumnIndexOrThrow("location_name"));
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Record?");
        builder.setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }


    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){

                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    new AsyncDeleteRecord().execute();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    protected class AsyncDeleteRecord extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            RestAPI api = new RestAPI();
            try {
                api.DeleteRecord(currentDate, parentEmail, childName, locationName);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncDeleteRecord", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            dbHelper.open();
            dbHelper.deleteEntry(rowId);
            dbHelper.close();
            Toast.makeText(ParentEditRoute.this, "Record Deleted.", Toast.LENGTH_SHORT).show();
            displayListView();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ParentEditRoute.this, MainActivity.class);
        i.setAction(Intent.ACTION_SEARCH);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.putExtra("email",parentEmail);
        i.putExtra("child",childName);
        startActivity(i);
    }
}
