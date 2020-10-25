package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.queuemanager.dateutility.DateUtility;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.list.PatientList;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.QueueSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Queue extends AppCompatActivity implements DBUtility {

    private TextView qname;
    private QueueSession qs;

    private Button beginbutton;
    private Button endbutton;
    private Button callnextpatient;
    private ListView listView;

    ProgressDialog progressDialog; //
    ConnectionClass connectionClass; //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue);

        connectionClass = new ConnectionClass();//
        progressDialog = new ProgressDialog(this);//

        qs = new QueueSession(getApplicationContext());

        qname = (TextView) findViewById(R.id.queueName);
        String queuename = qs.getdoctorfirstname() + " " + qs.getdoctorlastname();
        qname.setText(queuename);

        beginbutton = (Button) findViewById(R.id.beginQueue);
        endbutton = (Button) findViewById(R.id.endQueue);
        callnextpatient = (Button) findViewById(R.id.callNextPatient);
        listView =(ListView) findViewById(R.id.patientList);

        beginbutton.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                BeginQueue beginqueue = new BeginQueue();
                beginqueue.execute();
            }
        });
        Toast.makeText(getBaseContext(), "" + qs.getqueueid(), Toast.LENGTH_LONG).show();
        try{
            PatientList pl = new PatientList(Queue.this, listView, qs.getqueueid());
            pl.execute();

        }
        catch(Exception e){
            e.printStackTrace();
        }

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

                    String query = SELECT_QUEUE;

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setString(1, qs.getdoctorid());
                    ps.setString(2, qs.getdepartmentid());


                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        qs.setqueueid(rs.getString(1));
                        isSuccess = true;
                        Log.d("From session Queue", qs.getqueueid());
                    }
                    if (!isSuccess) {
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
                            }
                        }

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
}