package com.example.queuemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.KeruxSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements DBUtility {

    private EditText username;
    private EditText password;
    private TextView attempt;
    private Button login_button;
    int attempt_counter=5;

    ProgressDialog progressDialog; //
    ConnectionClass connectionClass; //

    private KeruxSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionClass=new ConnectionClass();//

        progressDialog=new ProgressDialog(this);//

        username = (EditText)findViewById(R.id.editText_user);
        password = (EditText)findViewById(R.id.editText_password);
        login_button = (Button)findViewById(R.id.button_login);

        session = new KeruxSession(getApplicationContext());

        login_button.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                Dologin dologin=new Dologin();
                dologin.execute();
                insertAudit();
            }
        });
    }

    //insert to audit logs
    public void insertAudit(){

        Security sec = new Security();

        try {
            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/InsertAuditAdminServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("first", sec.encrypt("Login"))
                    .appendQueryParameter("second", sec.encrypt("Queue Manager Login"))
                    .appendQueryParameter("third", sec.encrypt("Queue Manager logging in to the application"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("Login ID: " + session.getqueuemanagerid()))
                    .appendQueryParameter("sixth", session.getqueuemanagerid());
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String returnString="";
            ArrayList<String> output=new ArrayList<String>();
            while ((returnString = in.readLine()) != null)
            {
                Log.d("returnString", returnString);
                output.add(returnString);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class Dologin extends AsyncTask<String,String,String> {



        String usernam=username.getText().toString();
        String passstr=password.getText().toString();
        String z="Login failed";
        boolean isSuccess=false;

        String qmid, clinicid,  un, fn, ln, e;


        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(usernam.trim().equals("")||passstr.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Security sec = new Security();

                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/LoginQMServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("username", sec.encrypt(usernam))
                            .appendQueryParameter("password", sec.encrypt(passstr));
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
                        z = "Login successfull";
                        Log.d("returnString", returnString);
                        output.add(returnString);
                    }
                    for (int i = 0; i < output.size(); i++) {

                        if(i==0){
                            qmid=output.get(i);
                        }
                        else if(i==1){
                            clinicid=output.get(i);
                        }
                        else if(i==2){
                            un=output.get(i);
                        }
                        else if(i==3){
                            fn=output.get(i);
                        }
                        else if(i==4){
                            ln=output.get(i);
                        }
                        else if(i==5){
                            e=output.get(i);
                        }

                    }
                    in.close();


                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            if(isSuccess) {
                session.setqueuemanagerid(qmid);
                session.setclinicid(clinicid);
                session.setusername(un);
                session.setfirstname(fn);
                session.setlastname(ln);
                session.setemail(e);
                Intent intent=new Intent(MainActivity.this,Dashboard.class);

                // intent.putExtra("name",usernam);

                startActivity(intent);
                finish();
            }




        }
    }
}