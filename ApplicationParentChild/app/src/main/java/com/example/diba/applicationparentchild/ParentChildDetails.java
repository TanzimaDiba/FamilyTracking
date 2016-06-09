package com.example.diba.applicationparentchild;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class ParentChildDetails extends ActionBarActivity {

    TextView tvChildName, tvAge, tvProblem;
    Button bEdit;
    String parentEmail,childName,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_details);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        tvChildName = (TextView) findViewById(R.id.tv_childName);
        tvAge = (TextView) findViewById(R.id.tv_childAge);
        tvProblem = (TextView) findViewById(R.id.tv_childProblem);

        bEdit = (Button) findViewById(R.id.btn_editChild);
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ParentChildDetails.this,ParentChildEdit.class);
                i.putExtra("email",parentEmail);
                i.putExtra("child",childName);
                startActivity(i);
            }
        });
        new AsyncChildDetails().execute();
    }

    protected class AsyncChildDetails extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ParentChildDetails.this, "Loading...", Toast.LENGTH_SHORT).show();
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
                    password = result.get(i).getPassword();
                    tempAge = result.get(i).getAge();
                    tempProblem = result.get(i).getProblem();
                }
            }
            Toast.makeText(ParentChildDetails.this, "Load Completed", Toast.LENGTH_SHORT).show();
            tvChildName.setText(childName);
            tvAge.setText(String.valueOf(tempAge));
            tvProblem.setText(tempProblem);
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
        Intent i = new Intent(ParentChildDetails.this, ParentActivity.class);
        i.putExtra("email",parentEmail);
        i.putExtra("password",password);
        startActivity(i);
    }
}
