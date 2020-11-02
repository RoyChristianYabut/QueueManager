package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.queuemanager.adapter.QueueAdapter;
import com.example.queuemanager.dateutility.DateUtility;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.list.PatientList;
import com.example.queuemanager.model.ActiveQueue;
import com.example.queuemanager.model.Department;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.model.Patient;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.QueueSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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


    private QueueAdapter queueAdapter;
    private int queueid;

    ProgressDialog progressDialog; //
    ConnectionClass connectionClass; //

    private int instanceidCall;
    private String queueidCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        connectionClass = new ConnectionClass();//
        progressDialog = new ProgressDialog(this);//

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
                    Log.d("EEN", "WENT HERE");
                    Toast.makeText(getBaseContext(), "1: "+queueid+" 2: "+item.getQueueNumber()+" 3: "+item.getInstanceid(), Toast.LENGTH_LONG).show();
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
//                        Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(Queue.this,GenerateReport.class);
                intent.putExtra("queueid", qs.getqueueid());
                startActivity(intent);
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
                Connection con = connectionClass.CONN();
                Security sec = new Security();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {

                    if(qs.getqueueid()==null||qs.getqueueid().isEmpty()){
                        String query2 = INSERT_INTO_QUEUE;
                        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
                        String mCurrentTime = DateUtility.getDateTimeFromTimeStamp(System.currentTimeMillis(), DATE_FORMAT);


                        PreparedStatement ps1 = con.prepareStatement(query2);
                        ps1.setString(1, qs.getdoctorfirstname() + " " + qs.getdoctorlastname());
                        ps1.setString(2, mCurrentTime);
                        ps1.setString(3, qs.getdoctorid());
                        ps1.setString(4, qs.getdepartmentid());

                        int i = ps1.executeUpdate();
                        if (i == 1) {
                            String query3 = GET_NEW_QUEUE_ID;

                            PreparedStatement ps2 = con.prepareStatement(query3);

                            ResultSet rs1 = ps2.executeQuery();

                            while (rs1.next()) {
                                qs.setqueueid(rs1.getString(1));
                                isSuccess = true;
                                Log.d("Insert Queue", qs.getqueueid());
                                z= "Opening queue...";
                            }
                        }
                    }
                    else{
                        isSuccess = true;
                        Log.d("From session Queue", qs.getqueueid());
                        z= "Queue already active, opening active queue...";
                    }


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
                Connection con = connectionClass.CONN();
                Security sec =new Security();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {

                    String query = UPDATE_MARK_PATIENT_AS_CALLED;

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, instanceidCall);

                    String query2= UPDATE_QUEUELIST_AS_CALLED;
                    PreparedStatement ps2=con.prepareStatement(query2);
                    ps2.setInt(1, instanceidCall);
                    ps2.setString(2, queueidCall);
                    // stmt.executeUpdate(query);
                    isSuccess =true;

                    ps.executeUpdate();
                    ps2.executeUpdate();
                    z="Patient Served";
                }
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