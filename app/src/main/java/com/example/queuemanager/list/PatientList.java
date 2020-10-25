package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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

    public PatientList(Context c, ListView lv, String queueid) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.queueid=queueid;
        this.patientsList  = new ArrayList<>();
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
            queueAdapter.updateAdapter(patientsList);
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
            Connection con = connectionClass.CONN();
            Security sec =new Security();
            if (con == null) {
                z=null;
            } else {

                String query=SELECT_PATIENT_LIST;

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, queueid);
                // stmt.executeUpdate(query);


                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    patientsList.add(new Patient(R.drawable.briefcase, Integer.parseInt(rs.getString(1)), Integer.parseInt(rs.getString(2)), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getString(7)));
                }

                z="1";

            }
        }
        catch (Exception ex)
        {
            z=null;
        }

        return z;

    }
}
