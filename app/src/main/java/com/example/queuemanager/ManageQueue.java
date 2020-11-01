package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.QueueSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_queue);
        connectionClass =new ConnectionClass();

        txtPatient =(TextView)findViewById(R.id.txtPatient);
        btnCallAgain=(Button)findViewById(R.id.btnCallAgain);
        btnMarkServed=(Button)findViewById(R.id.btnMarkPatientServed);
        btnMarkNoShow=(Button)findViewById(R.id.btnMarkPatientNoShow);

        progressDialog=new ProgressDialog(this);//
        qs = new QueueSession(getApplicationContext());

        Intent i= getIntent();
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
                String toSpeak = txtPatient.getText().toString();
                Toast.makeText(getApplicationContext(), toSpeak,Toast.LENGTH_SHORT).show();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        btnMarkServed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkServed markServed = new MarkServed();
                markServed.execute();
            }
        });

        btnMarkNoShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MarkNoShow markNoShow = new MarkNoShow();
                markNoShow.execute();
            }
        });

        Toast.makeText(getApplicationContext(), instanceid+" "+queuenumber+" "+queueid,Toast.LENGTH_SHORT).show();
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
                    Connection con = connectionClass.CONN();
                    Security sec =new Security();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query = UPDATE_MARK_PATIENT_AS_SERVED;

                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setInt(1, instanceid);

                        String query2= UPDATE_QUEUELIST_AS_SERVED;
                        PreparedStatement ps2=con.prepareStatement(query2);
                        ps2.setInt(1, instanceid);
                        ps2.setString(2, queueid);
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
                Connection con = connectionClass.CONN();
                Security sec =new Security();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {

                    String query = UPDATE_MARK_PATIENT_AS_NO_SHOW;

                    PreparedStatement ps = con.prepareStatement(query);
                    ps.setInt(1, instanceid);
                    // stmt.executeUpdate(query);

                    String query2= UPDATE_QUEUELIST_AS_NO_SHOW;
                    PreparedStatement ps2=con.prepareStatement(query2);
                    ps2.setInt(1, instanceid);
                    ps2.setString(2, queueid);
                    // stmt.executeUpdate(query);
                    isSuccess =true;

                    ps.executeUpdate();
                    ps2.executeUpdate();

                    z="Patient Cancelled";
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
            if(isSuccess) {

                Intent intent=new Intent(ManageQueue.this,Queue.class);
                qs.setqueueid(queueid);
                // intent.putExtra("name",usernam);

                startActivity(intent);
                finish();
            }




        }
    }
}