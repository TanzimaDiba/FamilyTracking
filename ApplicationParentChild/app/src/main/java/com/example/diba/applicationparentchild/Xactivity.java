package com.example.diba.applicationparentchild;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class Xactivity extends ActionBarActivity {

    ListView lv;
    ArrayAdapter<String> adapter;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xactivity);

        lv = (ListView) findViewById(R.id.lv);
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
            data = new ArrayList<String>();
            for (int i = 0; i < result.size(); i++) {
                    data.add(result.get(i).getRecordDate());
            }
            adapter = new ArrayAdapter<String>(Xactivity.this,android.R.layout.simple_list_item_1, data);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_xactivity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
