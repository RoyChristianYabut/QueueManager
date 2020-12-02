package com.example.queuemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.queuemanager.session.KeruxSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import se.aaro.gustav.passwordstrengthmeter.PasswordStrengthMeter;

public class EditProfile extends AppCompatActivity {

    private EditText qmFName;
    private EditText qmLName;
    private EditText qmNewPassword;
    private EditText qmOldPassword;
    private EditText qmEmail;
    private String qmID;
    private Button saveChanges;
    private Button confirmPass;

    ConnectionClass connectionClass;
    private KeruxSession session;//global variable
    ProgressDialog progressDialog;

    DrawerLayout drawerLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        drawerLayout = findViewById(R.id.drawer_layout);

        connectionClass = new ConnectionClass();

        session = new KeruxSession(getApplicationContext());

        progressDialog = new ProgressDialog(this);

        qmFName = (EditText)findViewById(R.id.editFirstName);
        qmLName = (EditText)findViewById(R.id.editLastName);
        qmNewPassword = (EditText)findViewById(R.id.editPatientPassword);
        qmOldPassword = (EditText)findViewById(R.id.editPatientOldPassword);
        qmEmail = (EditText)findViewById(R.id.editEmailText);

        qmFName.setText(session.getfirstname());
        qmLName.setText(session.getlastname());
        qmEmail.setText(session.getemail());
        qmID=session.getqueuemanagerid();

        saveChanges = (Button)findViewById(R.id.button_save);
        confirmPass = (Button)findViewById(R.id.button_confirm);

        saveChanges.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                updateQMInfo updateqinfo=new updateQMInfo();
                updateqinfo.execute();
            }
        });

        confirmPass.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                updateQMPass updateminfo=new updateQMPass();
                updateminfo.execute();
            }
        });

        PasswordStrengthMeter meter = findViewById(R.id.passwordInputMeter);
        meter.setEditText(qmNewPassword);
    }
    public void ClickMenu (View view){
        //open drawer
        Dashboard.openDrawer(drawerLayout);
    }

    public void ClickLogo (View view){
        //Close drawer
        Dashboard.closeDrawer(drawerLayout);
    }

    public void ClickEditProfile(View view){
        //Redirect activity to dashboard
        recreate();
    }

    public void ClickDashboard(View view){
        //Redirect activity to dashboard
        Dashboard.redirectActivity(this, Dashboard.class);
    }

    public void ClickCurrentQueue(View view){
        //Redirect activity to manage accounts
        Dashboard.redirectActivity(this, Queue.class);
    }

    public void ClickLogout(View view){
        Dashboard.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        Dashboard.closeDrawer(drawerLayout);
    }

    private class updateQMInfo extends AsyncTask<String,String,String> {
        String qmFirstName=qmFName.getText().toString();
        String qmLastName=qmLName.getText().toString();
        String qmEmai = qmEmail.getText().toString();

        String z = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        /*sql retrieves current patient data using select method and place in corresponding text fields
                user will now have the chance to edit the said text fields
                user will press the update button
                sql update will get the entry from the fields and push the new data to the database
                make an intent that will either go back to the dashboard or stay at the edit profile page*/

        @Override
        protected String doInBackground(String... params) {
            if(qmFirstName.trim().equals("")||qmLastName.trim().equals(""))
                z = "Please enter values in the First Name and Last Name.";
            else
            {
                try {

                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/UpdateQMProfile");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(300000);
                    connection.setConnectTimeout(300000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("qmEmail", qmEmai)
                            .appendQueryParameter("qmFirstName", qmFirstName)
                            .appendQueryParameter("qmID", qmID);
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String returnString="";
                    ArrayList<String> output=new ArrayList<String>();
                    while ((returnString = in.readLine()) != null)
                    {
                        session.setemail(qmEmai);
                        session.setfirstname(qmFirstName);
                        session.setlastname(qmLastName);
                        isSuccess=true;
                        z = "Profile successfully edited!";
                        output.add(returnString);
                    }
                    in.close();




                }catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {

                Intent intent=new Intent(EditProfile.this,Dashboard.class);
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }

    private class updateQMPass extends AsyncTask<String,String,String> {

        String qmNewPass = qmNewPassword.getText().toString();
        String qmOldPass = qmOldPassword.getText().toString();


        String z = "";
        boolean isSuccess = false;

        /*String pID;*/

        /*ArrayList<String> ar = new ArrayList<String>();*/

        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        /*sql retrieves current patient data using select method and place in corresponding text fields
                user will now have the chance to edit the said text fields
                user will press the update button
                sql update will get the entry from the fields and push the new data to the database
                make an intent that will either go back to the dashboard or stay at the edit profile page*/

        @Override
        protected String doInBackground(String... params) {
            if(qmOldPass.trim().equals("")||qmNewPass.trim().equals(""))
                z = "Please enter your old and new password.";
            else
            {
                try {


                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/UpdateQMPass");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("patientid", qmID)
                            .appendQueryParameter("oldPassword", qmOldPass)
                            .appendQueryParameter("newpass", qmNewPass);
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();

                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String returnString="";
                    ArrayList<String> output=new ArrayList<String>();
                    while ((returnString = in.readLine()) != null)
                    {
                        isSuccess=true;
                        z = "Password successfully edited!";
                        output.add(returnString);
                    }
                    in.close();




                }catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();
            if(isSuccess) {
                Intent intent=new Intent(EditProfile.this,Dashboard.class);
                startActivity(intent);

            }
            progressDialog.hide();
        }


    }
}