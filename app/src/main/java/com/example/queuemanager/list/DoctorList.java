package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.example.queuemanager.ConnectionClass;
import com.example.queuemanager.R;
import com.example.queuemanager.adapter.DepartmentAdapter;
import com.example.queuemanager.adapter.DoctorAdapter;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.Department;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.security.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DoctorList extends AsyncTask<Void,Void,String> implements DBUtility {
    //FINISH THIS
    Context c;
    ListView lv;
    ProgressDialog pd;
    String clinicid;

    ArrayList<Doctor> doctorsList;
    DoctorAdapter doctorAdapter;
    ConnectionClass connectionClass;

    public DoctorList(Context c, ListView lv, String clinicid) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.clinicid=clinicid;
        this.doctorsList  = new ArrayList<>();
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
            doctorAdapter = new DoctorAdapter(c, doctorsList);
            lv.setAdapter(doctorAdapter);

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

                String query=SELECT_DOCTOR_LIST;

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, clinicid);
                // stmt.executeUpdate(query);


                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    doctorsList.add(new Doctor(R.drawable.briefcase, Integer.parseInt(rs.getString(1)), rs.getString(4), rs.getString(5)));
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
