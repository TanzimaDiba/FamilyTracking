package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class ChildAll extends ActionBarActivity {

    String parentEmail,childName;

    ArrayAdapter<String> adapterChild;
    ListView lvChild;
    Context contextChild;
    ArrayList<String> dataChild;
    String IpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_all);

        IpAddress = ((MyApplication) this.getApplication()).getSomeVariable();

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");

        dataChild = new ArrayList<String>();
        lvChild = (ListView) findViewById(R.id.lv_childAll);
        contextChild = this;

        adapterChild = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataChild);
        lvChild.setAdapter(adapterChild);
        lvChild.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                childName  = parent.getItemAtPosition(position).toString();
                toSelectionDialog();

            }
        });
        Toast.makeText(this, "Loading Please Wait..", Toast.LENGTH_SHORT).show();
        new AsyncAllChildDetails().execute();
    }

    protected class AsyncAllChildDetails extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
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
                Log.d("AsyncAllChildDetails", e.getMessage());
            }
            return childTable;
        }

        protected void onPostExecute(ArrayList<ChildTable> result) {
            // TODO Auto-generated method stub
            String em,status;
            for (int i = 0; i < result.size(); i++) {
                em = result.get(i).getEmail();
                status = result.get(i).getOnOff();
                if(em.equals(parentEmail) && status.equals("off")){
                    dataChild.add(result.get(i).getChildName());
                }
            }
            adapterChild.notifyDataSetChanged();
            Toast.makeText(contextChild, "Loading Completed", Toast.LENGTH_SHORT).show();
        }
    }

    private void toSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Save device for child : " + childName)
                .setPositiveButton("Save", dialogClickListener)
                .setNegativeButton("Cancel", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){

                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    new AsyncDeviceChild().execute(IpAddress, parentEmail, childName);
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    protected class AsyncDeviceChild extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                api.SelectChild(params[0], params[1],params[2]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncSelectedChild", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(contextChild, "Child Selected.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ChildAll.this, ChildActivity.class);
            i.putExtra("email",parentEmail);
            i.putExtra("child",childName);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_child_all, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ChildAll.this, ActivityLogin.class);
        startActivity(i);
    }
}
