package com.example.queuemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.KeruxSession;
import com.example.queuemanager.session.QueueSession;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;

public class ManageQueue extends AppCompatActivity implements DBUtility {

    private TextView txtPatient;
    private TextToSpeech t1;
    private Button btnCallAgain;
    private Button btnMarkServed;
    private Button btnMarkNoShow;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    private String queueid;
    private int instanceid;
    private String queuenumber;
    private QueueSession qs;

    DrawerLayout drawerLayout;
    KeruxSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_queue);
        connectionClass =new ConnectionClass();
        drawerLayout = findViewById(R.id.drawer_layout);
        session=new KeruxSession(getApplicationContext());

        txtPatient =(TextView)findViewById(R.id.txtPatient);
        btnCallAgain=(Button)findViewById(R.id.btnCallAgain);
        btnMarkServed=(Button)findViewById(R.id.btnMarkPatientServed);
        btnMarkNoShow=(Button)findViewById(R.id.btnMarkPatientNoShow);

        progressDialog=new ProgressDialog(this);
        qs = new QueueSession(getApplicationContext());

        final Intent i= getIntent();
        queueid=i.getStringExtra("queueid");
        queuenumber=i.getStringExtra("queuenumber");
        instanceid=i.getIntExtra("instanceid", 0);

        txtPatient.setText("Patient #"+queuenumber);


        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });

        btnCallAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkCalled markCalled =new MarkCalled();
                markCalled.execute();
                String toSpeak = txtPatient.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                insertAudit();
            }
        });

        btnMarkServed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkServed markServed = new MarkServed();
                markServed.execute();
                insertAudit();
            }
        });

        btnMarkNoShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkNoShow markNoShow = new MarkNoShow();
                markNoShow.execute();
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
                    .appendQueryParameter("first", sec.encrypt("Queue Actions"))
                    .appendQueryParameter("second", sec.encrypt("Queue Manager actions for queue"))
                    .appendQueryParameter("third", sec.encrypt("Queue Manager starting actions for queue"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("Queue Manager ID: " + session.getqueuemanagerid()))
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
        Dashboard.redirectActivity(this, EditProfile.class);
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

    private class MarkServed extends AsyncTask<String,String,String> {
        boolean isSuccess;
        String z="";
        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
                z="";

                try {

                    URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/MarkServedQMServlet");
                    URLConnection connection = url.openConnection();

                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("instanceid", Integer.toString(instanceid))
                            .appendQueryParameter("queueid", queueid);
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
                        z = "Patient Served";
                        isSuccess=true;
                        Log.d("returnString", returnString);
                        output.add(returnString);
                    }
                    in.close();
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
                return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            if(isSuccess) {

                Intent intent=new Intent(ManageQueue.this,Queue.class);
                qs.setqueueid(queueid);
                // intent.putExtra("name",usernam);

                startActivity(intent);
                finish();
            }




        }
    }

    private class MarkNoShow extends AsyncTask<String,String,String> {
        boolean isSuccess;
        String z="";
        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            z="";

            try {
                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/MarkNoShowQMServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("instanceid", Integer.toString(instanceid))
                        .appendQueryParameter("queueid", queueid);
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
                    z = "Patient Cancelled";
                    isSuccess=true;
                    Log.d("returnString", returnString);
                    output.add(returnString);
                }
                in.close();
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            if(isSuccess) {

                Intent intent=new Intent(ManageQueue.this,Queue.class);
                qs.setqueueid(queueid);
                // intent.putExtra("name",usernam);

                startActivity(intent);
                finish();
            }




        }
    }

    private class MarkCalled extends AsyncTask<String,String,String> {
        boolean isSuccess;
        String z="";
        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            z="";

            try {
                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/MarkCalledQMServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("instanceid", Integer.toString(instanceid))
                        .appendQueryParameter("queueid", queueid);
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
                    z = "Patient Called";
                    isSuccess=true;
                    Log.d("returnString", returnString);
                    output.add(returnString);
                }
                in.close();
            }
            catch (Exception ex)
            {
                isSuccess = false;
                z = "Exceptions"+ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();

        }
    }
}