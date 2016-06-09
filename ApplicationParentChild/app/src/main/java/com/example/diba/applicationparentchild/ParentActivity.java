package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import org.json.JSONObject;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class ParentActivity extends ActionBarActivity implements Chronometer.OnChronometerTickListener {

    ArrayAdapter<String> adapter;
    ListView listv;
    ArrayList<String> data;

    ArrayAdapter<String> adapterOn;
    ListView listvOn;
    ArrayList<String> dataOn;

    String parentEmail, parentPassword;
    EditText newChild;
    String IpAddress;
    String childStatus = "off";

    Chronometer timer;
    long chTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        IpAddress = ((MyApplication) this.getApplication()).getSomeVariable();

        listv = (ListView) findViewById(R.id.lv_child);
        listvOn = (ListView) findViewById(R.id.lv_on_off);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        parentPassword = bd.getString("password");

        timer = (Chronometer) findViewById(R.id.activityTimer);
        timer.setOnChronometerTickListener(this);
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }

    @Override
    public void onChronometerTick(Chronometer chronometer) {
        chTime = (SystemClock.elapsedRealtime()-timer.getBase())/1000;
        if(chTime%30 == 0){
            new AsyncChildDetails().execute();
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
            data = new ArrayList<String>();
            dataOn = new ArrayList<String>();
            String email;
            for (int i = 0; i < result.size(); i++) {
                email = result.get(i).getEmail();
                if(email.equals(parentEmail)){
                    data.add(result.get(i).getChildName());
                    dataOn.add(result.get(i).getOnOff());
                }
            }
            adapter = new ArrayAdapter<String>(ParentActivity.this,android.R.layout.simple_list_item_1, data);
            listv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            adapterOn = new ArrayAdapter<String>(ParentActivity.this,android.R.layout.simple_list_item_1, dataOn);
            listvOn.setAdapter(adapterOn);
            adapterOn.notifyDataSetChanged();

            listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String  tempChild    = parent.getItemAtPosition(position).toString();
                    new AsyncChildStatus().execute(tempChild);
                }
            });
        }
    }

    protected class AsyncChildStatus extends AsyncTask<String, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;
        String tempChild;
        @Override
        protected ArrayList<ChildTable> doInBackground(String... params) {
            // TODO Auto-generated method stub
            RestAPI api = new RestAPI();
            try {
                tempChild = params[0];
                JSONObject jsonObj = api.ChildDetails();
                JSONParser parser = new JSONParser();
                childTable = parser.parseChild(jsonObj);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncChildStatus", e.getMessage());
            }
            return childTable;
        }

        protected void onPostExecute(ArrayList<ChildTable> result) {
            // TODO Auto-generated method stub
            String email,child;
            for (int i = 0; i < result.size(); i++) {
                email = result.get(i).getEmail();
                child = result.get(i).getChildName();
                if(email.equals(parentEmail) && child.equals(tempChild)){
                    childStatus = result.get(i).getOnOff();
                }
            }
            toDialog(tempChild);
        }
    }

    private void toDialog(final String cName) {

        final Dialog d = new Dialog(this);
        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.layout_child);
        d.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        d.show();

        TextView details = (TextView) d.findViewById(R.id.tv_details);
        TextView viewLocation = (TextView) d.findViewById(R.id.tv_viewLocation);
        TextView editRoute = (TextView) d.findViewById(R.id.tv_editRoute);
        TextView childDelete = (TextView) d.findViewById(R.id.tv_delete);

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                timer.stop();
                Intent i = new Intent(ParentActivity.this,ParentChildDetails.class);
                i.putExtra("email",parentEmail);
                i.putExtra("child",cName);
                startActivity(i);
            }
        });

        viewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                if(childStatus.equals("on")) {
                    timer.stop();
                    Intent i = new Intent(ParentActivity.this, ParentViewMap.class);
                    i.putExtra("email", parentEmail);
                    i.putExtra("child", cName);
                    startActivity(i);
                }
                else{
                    Dialog d=new Dialog(ParentActivity.this);
                    d.setTitle("WARNING");
                    TextView tv=new TextView(ParentActivity.this);
                    tv.setHeight(100);
                    tv.setGravity(1);
                    tv.setText("Child tracking is turned off.");
                    tv.setTextSize(20);
                    d.setContentView(tv);
                    d.show();
                }
            }
        });

        editRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                timer.stop();
                Intent i = new Intent(ParentActivity.this, MainActivity.class);
                i.setAction(Intent.ACTION_SEARCH);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.putExtra("email", parentEmail);
                i.putExtra("child", cName);
                startActivity(i);
            }
        });

        childDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.dismiss();
                new AsyncDeleteChild().execute(parentEmail,cName);
            }
        });
    }

    protected class AsyncDeleteChild extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            RestAPI api = new RestAPI();
            try {
                api.DeleteChild(params[0], params[1]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncDeleteChild", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(ParentActivity.this,"Child Deleted.",Toast.LENGTH_SHORT).show();
            new AsyncChildDetails().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parent, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addChild) {
            toAddChild();
        }

        return super.onOptionsItemSelected(item);
    }

    private void toAddChild() {

        LayoutInflater inflater = LayoutInflater.from(this);
        View layout = inflater.inflate(R.layout.layout_add_child, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Save", dialogClickListener).setNegativeButton("Cancel", dialogClickListener);

        newChild = (EditText) layout.findViewById(R.id.add_child);

        builder.setView(layout);
        AlertDialog displayInfo = builder.create();
        displayInfo.show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){

                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    String childName = newChild.getText().toString();
                    ChildTable childDetail = new ChildTable("child", parentEmail, parentPassword, childName, 0, "problem", 0, 0, "off");
                    new AsyncAddChild().execute(childDetail);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    protected class AsyncAddChild extends AsyncTask<ChildTable, Void, Void> {
        @Override
        protected Void doInBackground(ChildTable... params) {
            RestAPI api = new RestAPI();
            try {
                api.CreateChildAccount(params[0].getChildIp(),params[0].getEmail(),params[0].getPassword(),
                                       params[0].getChildName(),params[0].getAge(),params[0].getProblem(),
                                       params[0].getCurrentLat(),params[0].getCurrentLon(), params[0].getOnOff());
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncAddChild", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(ParentActivity.this,"Child added.",Toast.LENGTH_SHORT).show();
            new AsyncChildDetails().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    public void onBackPressed() {
        timer.stop();
        Intent i = new Intent(ParentActivity.this, ActivityLogin.class);
        startActivity(i);
    }
}
