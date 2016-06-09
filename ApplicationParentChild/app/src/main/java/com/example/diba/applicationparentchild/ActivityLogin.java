package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ActivityLogin extends ActionBarActivity {

    EditText edEmail, edPassword;
    Button btnLogin;
    Button tvCreate;
    Context context;

    String email, password,childName;
    String IpAddress;

    CheckBox cbRememberMe;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor loginPrefsEditor;
    Boolean saveLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toDeviceIP();

        context=this;
        edEmail =(EditText) findViewById(R.id.edit_email);
        edPassword =(EditText) findViewById(R.id.edit_password);
        tvCreate =(Button) findViewById(R.id.btn_create);
        btnLogin =(Button) findViewById(R.id.btn_login);
        cbRememberMe = (CheckBox) findViewById(R.id.checkBoxRemember);

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            edEmail.setText(loginPreferences.getString("email", ""));
            edPassword.setText(loginPreferences.getString("password", ""));
            cbRememberMe.setChecked(true);
        }

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ActivityLogin.this, CreateAccount.class);
                startActivity(i);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectivityManager cm = (ConnectivityManager) getSystemService(ActivityLogin.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if(ni != null) {
                    email = edEmail.getText().toString();
                    password = edPassword.getText().toString();
                    if (email.isEmpty() || password.isEmpty()) {
                        Dialog d=new Dialog(ActivityLogin.this);
                        d.setTitle("WARNING");
                        TextView tv=new TextView(ActivityLogin.this);
                        tv.setHeight(100);
                        tv.setGravity(1);
                        tv.setText("Please provide all the information.");
                        tv.setTextSize(20);
                        d.setContentView(tv);
                        d.show();
                    } else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(edEmail.getWindowToken(), 0);
                        if (cbRememberMe.isChecked()) {
                            loginPrefsEditor.putBoolean("saveLogin", true);
                            loginPrefsEditor.putString("email", email);
                            loginPrefsEditor.putString("password", password);
                            loginPrefsEditor.commit();
                        } else {
                            loginPrefsEditor.clear();
                            loginPrefsEditor.commit();
                        }

                        new AsyncLogin().execute(IpAddress, email, password);
                    }
                }
                else{
                    toNetWarningDialog();
                }
            }
        });
    }

    private void toDeviceIP() {
        IpAddress = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        ((MyApplication) this.getApplication()).setSomeVariable(IpAddress);
    }

    private void toNetWarningDialog() {

        AlertDialog.Builder builderWarning = new AlertDialog.Builder(this);
        builderWarning.setMessage("Please turn on your Internet connection.")
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

    protected class AsyncLogin extends AsyncTask<String, JSONObject, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            Toast.makeText(context, "Please Wait...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {

            RestAPI api = new RestAPI();
            String userAuth = null;
            try {
                JSONObject jsonObj = api.UserAuthentication(params[0],params[1],params[2]);
                JSONParser parser = new JSONParser();
                userAuth = parser.parseUserAuth(jsonObj);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncLogin", e.getMessage());
            }
            return userAuth;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            //Check user validity
            if(result.equals("parent")){
                Intent i = new Intent(ActivityLogin.this, ParentActivity.class);
                i.putExtra("email", email);
                i.putExtra("password", password);
                startActivity(i);
            }
            else  if(result.equals("child")) {
                new AsyncChildName().execute();
            }
            else if(result.equals("set")){
                Intent i = new Intent(ActivityLogin.this, ChildAll.class);
                i.putExtra("email", email);
                startActivity(i);
            }
            else if(result.equals("not")){
                Toast.makeText(context, "Invalid information." , Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(context, "Server error" + result, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected class AsyncChildName extends AsyncTask<Void, JSONObject, ArrayList<ChildTable>> {
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
                Log.d("AsyncLoadUserDetails", e.getMessage());
            }
            return childTable;
        }

        protected void onPostExecute(ArrayList<ChildTable> result) {
            // TODO Auto-generated method stub
            String em, ip;
            for (int i = 0; i < result.size(); i++) {
                ip = result.get(i).getChildIp();
                em = result.get(i).getEmail();
                if (em.equals(email) && ip.equals(IpAddress)) {
                    childName = result.get(i).getChildName();
                }
            }
            Intent i = new Intent(ActivityLogin.this,ChildActivity.class);
            i.putExtra("email",email);
            i.putExtra("child",childName);
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menuAbout) {
            Intent i = new Intent(ActivityLogin.this, Xactivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){

                case DialogInterface.BUTTON_POSITIVE:
                    dialog.dismiss();
                    finish();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}

