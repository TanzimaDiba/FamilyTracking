package com.example.diba.applicationparentchild;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateAccount extends ActionBarActivity {

    EditText edEmail, edPassword, edConfirm;
    Button btnCreate;
    String email,password,confirm;
    String IpAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        IpAddress = ((MyApplication) this.getApplication()).getSomeVariable();

        edEmail =(EditText) findViewById(R.id.edit_email);
        edPassword =(EditText) findViewById(R.id.edit_password);
        edConfirm =(EditText) findViewById(R.id.edit_confirm);
        btnCreate =(Button) findViewById(R.id.btn_create);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                ConnectivityManager cm = (ConnectivityManager) getSystemService(CreateAccount.CONNECTIVITY_SERVICE);
                NetworkInfo ni = cm.getActiveNetworkInfo();
                if (ni != null) {
                    email = edEmail.getText().toString();
                    password = edPassword.getText().toString();
                    confirm = edConfirm.getText().toString();

                    if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                        Dialog d = new Dialog(CreateAccount.this);
                        d.setTitle("Warning");
                        TextView tv = new TextView(CreateAccount.this);
                        tv.setHeight(100);
                        tv.setGravity(1);
                        tv.setText("Please provide all the information.");
                        tv.setTextSize(20);
                        d.setContentView(tv);
                        d.show();
                    } else {
                        boolean validEmail = isEmailValid(email);
                        if (validEmail) {
                            if (!password.equals(confirm)) {
                                Dialog d = new Dialog(CreateAccount.this);
                                d.setTitle("Warning");
                                TextView tv = new TextView(CreateAccount.this);
                                tv.setHeight(100);
                                tv.setGravity(1);
                                tv.setText("Two passwords do not match");
                                tv.setTextSize(20);
                                d.setContentView(tv);
                                d.show();
                            } else {
                                if(password.length() <5){
                                    Dialog d = new Dialog(CreateAccount.this);
                                    d.setTitle("Warning");
                                    TextView tv = new TextView(CreateAccount.this);
                                    tv.setHeight(100);
                                    tv.setGravity(1);
                                    tv.setText("Password must be more than 5 characters.");
                                    tv.setTextSize(20);
                                    d.setContentView(tv);
                                    d.show();
                                }else {
                                    ParentTable parentDetail = new ParentTable(IpAddress, email, password);
                                    new AsyncCreateUser().execute(parentDetail);
                                }
                            }
                        } else {
                            Dialog d = new Dialog(CreateAccount.this);
                            d.setTitle("Warning");
                            TextView tv = new TextView(CreateAccount.this);
                            tv.setHeight(100);
                            tv.setGravity(1);
                            tv.setText("Invalid email.");
                            tv.setTextSize(20);
                            d.setContentView(tv);
                            d.show();
                        }
                    }
                }
                else{
                    toNetWarningDialog();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {

        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
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

    protected class AsyncCreateUser extends AsyncTask<ParentTable, Void, Integer> {
        @Override
        protected Integer doInBackground(ParentTable... params) {
            RestAPI api = new RestAPI();
            int createAuth = 0;
            try {
                JSONObject jsonObj = api.CreateNewAccount(params[0].getParentIp(),params[0].getEmail(),params[0].getPassword());
                JSONParser parser = new JSONParser();
                createAuth = parser.parseCreateAuth(jsonObj);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("AsyncCreateUser", e.getMessage());
            }
            return createAuth;
        }

        protected void onPostExecute(Integer result) {
            if(result == 1){
                Toast.makeText(CreateAccount.this, "Account already exists on this device as parent.", Toast.LENGTH_SHORT).show();
            }
            else if(result == 2){
                Toast.makeText(CreateAccount.this, "Account already exists.", Toast.LENGTH_SHORT).show();
            }
            else if(result == 3){
                Toast.makeText(CreateAccount.this, "Account already exists on this device as child.", Toast.LENGTH_SHORT).show();
            }
            else if(result == 4){
                Toast.makeText(CreateAccount.this, "Account Created", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreateAccount.this, ActivityLogin.class);
                startActivity(i);
            }
            else {
                Toast.makeText(CreateAccount.this, "Server error.", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_account, menu);
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
        Intent i = new Intent(CreateAccount.this, ActivityLogin.class);
        startActivity(i);
    }
}
