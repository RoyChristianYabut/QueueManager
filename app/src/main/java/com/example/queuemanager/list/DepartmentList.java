package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.queuemanager.ConnectionClass;
import com.example.queuemanager.Dashboard;
import com.example.queuemanager.MainActivity;
import com.example.queuemanager.R;
import com.example.queuemanager.adapter.DepartmentAdapter;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.Department;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.spinner.Connector;
import com.example.queuemanager.spinner.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DepartmentList extends AsyncTask<Void,Void,String> implements DBUtility {

    Context c;
    ListView lv;
    ProgressDialog pd;
    String clinicid;
    ArrayList<Department> departmentsList;
    DepartmentAdapter departmentAdapter;
    ConnectionClass connectionClass;

    public DepartmentList(Context c, ListView lv, String clinicid) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.clinicid=clinicid;
        this.departmentsList  = new ArrayList<>();
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
            departmentAdapter = new DepartmentAdapter(c, departmentsList);
            lv.setAdapter(departmentAdapter);

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

                String query=SELECT_DEPARTMENT_LIST;

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, clinicid);
                // stmt.executeUpdate(query);


                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    departmentsList.add(new Department(R.drawable.briefcase, Integer.parseInt(rs.getString(1)), rs.getString(2)));
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
