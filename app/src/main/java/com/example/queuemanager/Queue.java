package com.example.queuemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.queuemanager.adapter.QueueAdapter;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.list.PatientList;
import com.example.queuemanager.model.Patient;
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

public class Queue extends AppCompatActivity implements DBUtility {

    private TextView qname;
    private QueueSession qs;
    private TextToSpeech t1;

    private Button beginbutton;
    private Button endbutton;
    private Button callnextpatient;
    private Button refresh;
    private ListView listView;

    DrawerLayout drawerLayout;

    private QueueAdapter queueAdapter;
    private int queueid;

    ProgressDialog progressDialog; //
    ConnectionClass connectionClass; //
    KeruxSession session;

    private int instanceidCall;
    private String queueidCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);
        drawerLayout = findViewById(R.id.drawer_layout);

        connectionClass = new ConnectionClass();//
        progressDialog = new ProgressDialog(this);//
        session=new KeruxSession(getApplicationContext());

        qs = new QueueSession(getApplicationContext());

        qname = (TextView) findViewById(R.id.queueName);
        qname.setText(qs.getqueuename());

        beginbutton = (Button) findViewById(R.id.beginQueue);
        endbutton = (Button) findViewById(R.id.endQueue);
        callnextpatient = (Button) findViewById(R.id.callNextPatient);
        listView =(ListView) findViewById(R.id.patientList);
        refresh = (Button) findViewById(R.id.btnRefresh);

        beginbutton.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                BeginQueue beginqueue = new BeginQueue();
                beginqueue.execute();
                insertAudit();
            }
        });
       queueid=0;

        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.US);
                }
            }
        });


//      boolean successPatientList=false;
        try{
            PatientList pl = new PatientList(Queue.this, listView, qs.getqueueid(), queueAdapter);
            pl.execute();
            queueid=Integer.parseInt(qs.getqueueid());
//          successPatientList=true;
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                    Intent intent = new Intent(Queue.this, ManageQueue.class);
                    Patient item = (Patient) listView.getAdapter().getItem(position);

                    intent.putExtra("queueid", Integer.toString(queueid));
                    intent.putExtra("queuenumber", item.getQueueNumber());
                    intent.putExtra("instanceid", item.getInstanceid());
                    startActivity(intent);
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
//          FOR REFRESHING PERIODCIALLY
//        if(successPatientList){
//            final Handler handler = new Handler();
//            handler.postDelayed( new Runnable() {
//
//                @Override
//                public void run() {
////                    queueAdapter.updateAdapter(queueid);
////                    listView.setAdapter(queueAdapter);
//                    PatientList pl = new PatientList(Queue.this, listView, qs.getqueueid(), queueAdapter);
//                    pl.execute();
//                    Toast.makeText(getBaseContext(), "" + queueid, Toast.LENGTH_LONG).show();
////                adapter.notifyDataSetChanged();
//                    handler.postDelayed( this, 5000 );
//                }
//            }, 5000 );
//        }

        callnextpatient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String toSpeak = "";

                try {
                    for (int i = 0; i < listView.getCount(); i++) {
                        v = listView.getChildAt(i);
                        TextView status=(TextView) v.findViewById(R.id.patientStatus);
                        TextView patientNumber=(TextView) v.findViewById(R.id.patientNumber);
                        if (status.getText().toString().equals("Active")){
                            toSpeak=patientNumber.getText().toString();
                            Patient item = (Patient) listView.getAdapter().getItem(i);
                            instanceidCall=item.getInstanceid();
                            queueidCall=qs.getqueueid();
                            break;
                        }


//
                    }
                    if(toSpeak.equals("")){

                    }
                    else{
                        MarkCalled markCalled=new MarkCalled();
                        markCalled.execute();
                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        });

        endbutton.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                EndQueue endQueue=new EndQueue();
                endQueue.execute();
                insertAudit();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                PatientList pl = new PatientList(Queue.this, listView, qs.getqueueid(), queueAdapter);
                pl.execute();
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
                    .appendQueryParameter("first", sec.encrypt("Queue Begin and End Actions"))
                    .appendQueryParameter("second", sec.encrypt("Start and End Queue"))
                    .appendQueryParameter("third", sec.encrypt("Queue Manager actions for starting and ending queue"))
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
        recreate();
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

    private class BeginQueue extends AsyncTask<String, String, String> {

        String z = "";
        boolean isSuccess = false;


        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {

                    if(qs.getqueueid()==null||qs.getqueueid().isEmpty()){
                        URL url = new URL("http://10.70.3.1:8080/KeruxRootAdmin/BeginQueueQMServlet");
                        URLConnection connection = url.openConnection();

                        connection.setReadTimeout(10000);
                        connection.setConnectTimeout(15000);
                        connection.setDoInput(true);
                        connection.setDoOutput(true);

                        Uri.Builder builder = new Uri.Builder()
                                .appendQueryParameter("queuename", qs.getdoctorfirstname() + " " + qs.getdoctorlastname())
                                .appendQueryParameter("docid", qs.getdoctorid())
                                .appendQueryParameter("depid",qs.getdepartmentid())
                                .appendQueryParameter("qmid",session.getqueuemanagerid());
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

                            qs.setqueueid(output.get(i));

                        }
                        in.close();
                    }
                    else{
                        isSuccess = true;
                        Log.d("From session Queue", qs.getqueueid());
                        z= "Queue already active, opening active queue...";
                    }


            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            if (z.equals("")) {

            } else {
                Toast.makeText(getBaseContext(), "" + z, Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
            if (isSuccess) {


                Intent intent = new Intent(Queue.this, Queue.class);
                startActivity(intent);
            }


        }
    }

    private class EndQueue extends AsyncTask<String, String, String> {

        String z = "";
        boolean isSuccess = false;


        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/EndQueueQMServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("queueid", qs.getqueueid());
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
                    z = "Queue Ended";
                    isSuccess=true;
                    Log.d("returnString", returnString);
                    output.add(returnString);
                }
                in.close();
            } catch (Exception ex) {
                isSuccess = false;
                z = "Exceptions" + ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            if (z.equals("")) {

            } else {
                Toast.makeText(getBaseContext(), "" + z, Toast.LENGTH_LONG).show();
            }

            progressDialog.dismiss();
            if (isSuccess) {


                Intent intent = new Intent(Queue.this,GenerateReport.class);
                intent.putExtra("queueid", qs.getqueueid());
                startActivity(intent);
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
                        .appendQueryParameter("instanceid", Integer.toString(instanceidCall))
                        .appendQueryParameter("queueid", queueidCall);
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