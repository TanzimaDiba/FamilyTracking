package com.example.diba.applicationparentchild;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;


public class ParentChildEdit extends ActionBarActivity {

    EditText edChildName, edAge, edProblem;
    Button bSave;
    String parentEmail,childName;
    String newChild, problem;
    int age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_child_edit);

        Bundle bd = getIntent().getExtras();
        parentEmail = bd.getString("email");
        childName = bd.getString("child");

        edChildName = (EditText) findViewById(R.id.ed_childName);
        edAge = (EditText) findViewById(R.id.ed_childAge);
        edProblem = (EditText) findViewById(R.id.ed_childProblem);

        bSave = (Button) findViewById(R.id.btn_saveChild);
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newChild = edChildName.getText().toString();
                String s = edAge.getText().toString();
                age = Integer.parseInt(s);
                problem = edProblem.getText().toString();
                new AsyncEditChild().execute();
            }
        });
        new AsyncChildDetails().execute();
    }

    protected class AsyncChildDetails extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
        ArrayList<ChildTable> childTable = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(ParentChildEdit.this, "Loading...", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ParentChildEdit.this, "Load Completed", Toast.LENGTH_SHORT).show();
            edChildName.setText(childName);
            edAge.setText(String.valueOf(tempAge));
            edProblem.setText(tempProblem);
        }
    }

    protected class AsyncEditChild extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            RestAPI api = new RestAPI();
            try {
                api.EditChild(parentEmail, childName, newChild, age, problem);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncEditChild", e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Toast.makeText(ParentChildEdit.this,"Child Updated.",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ParentChildEdit.this, ParentChildDetails.class);
            i.putExtra("email",parentEmail);
            i.putExtra("child",childName);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_parent_edit, menu);
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
        Intent i = new Intent(ParentChildEdit.this, ParentChildDetails.class);
        i.putExtra("email",parentEmail);
        i.putExtra("child",childName);
        startActivity(i);
    }
}
