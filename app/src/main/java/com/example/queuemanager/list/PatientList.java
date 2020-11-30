package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.queuemanager.ConnectionClass;
import com.example.queuemanager.R;
import com.example.queuemanager.adapter.DoctorAdapter;
import com.example.queuemanager.adapter.QueueAdapter;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.model.Patient;
import com.example.queuemanager.security.Security;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class PatientList extends AsyncTask<Void,Void,String> implements DBUtility {
    Context c;
    ListView lv;
    ProgressDialog pd;
    String queueid;

    ArrayList<Patient> patientsList;
    QueueAdapter queueAdapter;
    ConnectionClass connectionClass;

    public PatientList(Context c, ListView lv, String queueid, QueueAdapter queueAdapter) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.queueid=queueid;
        this.queueAdapter=queueAdapter;
        this.patientsList  = new ArrayList<>();

    }
    public QueueAdapter getQueueAdapter(){
        return queueAdapter;
    }
    public ArrayList<Patient> getPatientsList(){
        this.patientsList  = new ArrayList<>();
        this.retrieveData();
        return patientsList;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        pd=new ProgressDialog(c);
        pd.setTitle("Downloading..");
        pd.setMessage("Retrieving data...Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... params) {
        return this.retrieveData();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pd.dismiss();

        lv.setAdapter(null);

        if(s != null)
        {
            queueAdapter = new QueueAdapter(c, patientsList);
            lv.setAdapter(queueAdapter);

        }else {
            Toast.makeText(c,"No data retrieved", Toast.LENGTH_SHORT).show();
        }
    }

    private String retrieveData()
    {
        String z;
        z=null;
        try {
            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/PatientListQMServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
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
                z = "1";
                Log.d("returnString", returnString);
                output.add(returnString);
            }
            for (int i = 0; i < output.size(); i++) {
                String line=output.get(i);
                String notnullline = line.replaceAll("null", "0");
                String [] words=notnullline.split("\\s\\|\\s");
                Log.d("perror", words[0]+"?"+words[1]+"?"+words[2]+"?"+words[3]+"?"+words[4]+"?"+words[5]+"?"+words[6]);
                String qType="";
                if(words[2].equals("1")){
                    qType="Regular";
                }
                if(words[2].equals("2")){
                    qType="HMO";
                }
                if(words[2].equals("3")){
                    qType="PWD & Senior";
                }
                patientsList.add(new Patient(R.drawable.briefcase, Integer.parseInt(words[0]), Integer.parseInt(words[1]), qType, words[3], words[4], words[5], words[6]));

            }
            in.close();


        }
        catch (Exception ex)
        {
            Log.d("DEXCEPT", ex.getMessage());
            z=null;
        }
        return z;

    }
}
