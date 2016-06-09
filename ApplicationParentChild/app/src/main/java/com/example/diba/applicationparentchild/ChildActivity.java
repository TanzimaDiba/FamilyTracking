package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class ChildActivity extends ActionBarActivity implements View.OnClickListener {

    TextView tvChildName, tvAge, tvProblem;
    Button bStart;
    String parentEmail,childName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        tvChildName = (TextView) findViewById(R.id.tv_childName);
        tvAge = (TextView) findViewById(R.id.tv_childAge);
        tvProblem = (TextView) findViewById(R.id.tv_childProblem);

        bStart = (Button) findViewById(R.id.btn_startTracking);
        bStart.setOnClickListener(this);
        new AsyncChildDetails().execute();
    }

    protected class AsyncChildDetails extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ChildActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
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
            String tempEmail,tempChild,tempProblem = null;
            int tempAge = 0;

            for (int i = 0; i < result.size(); i++) {
                tempEmail = result.get(i).getEmail();
                tempChild = result.get(i).getChildName();
                if(tempEmail.equals(parentEmail) && tempChild.equals(childName)){
                    tempAge = result.get(i).getAge();
                    tempProblem = result.get(i).getProblem();
                }
            }
            Toast.makeText(ChildActivity.this, "Load Completed", Toast.LENGTH_SHORT).show();
            tvChildName.setText(childName);
            tvAge.setText("" + tempAge);
            tvProblem.setText(tempProblem);
        }
    }

    @Override
    public void onClick(View v) {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(ChildActivity.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        LocationManager lm = (LocationManager) getSystemService(ChildActivity.LOCATION_SERVICE );
        boolean gpsStatus = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(ni == null || !gpsStatus){
            toWarningDialog();
        }

        else{
            new AsyncChildOn().execute(parentEmail, childName, "on");
        }
    }

    private void toWarningDialog() {

        AlertDialog.Builder builderWarning = new AlertDialog.Builder(this);
        builderWarning.setMessage("Please turn on your Internet connection && GPS Location.")
                .setPositiveButton("OK", warningDialogClickListener).show();

    }

    DialogInterface.OnClickListener warningDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    Intent is = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(is);
                    break;
            }
        }
    };

    protected class AsyncChildOn extends AsyncTask<String, Void, Void> {
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
            Toast.makeText(ChildActivity.this, "Child tracking is on.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ChildActivity.this, ChildMap.class);
            i.putExtra("email",parentEmail);
            i.putExtra("child",childName);
            startActivity(i);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parent_child_details, menu);
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
        Intent i = new Intent(ChildActivity.this, ActivityLogin.class);
        startActivity(i);
    }
}
