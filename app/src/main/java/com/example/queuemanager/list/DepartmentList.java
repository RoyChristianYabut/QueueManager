package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.example.queuemanager.model.ActiveQueue;
import com.example.queuemanager.model.Department;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.spinner.Connector;
import com.example.queuemanager.spinner.Parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
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


                URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/DepartmentListQMServlet");
                URLConnection connection = url.openConnection();

                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("clinicid", clinicid);
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
                    String [] words=line.split("\\s\\|\\s");
                    departmentsList.add(new Department(R.drawable.briefcase, Integer.parseInt(words[0]), words[1]));
                }
                in.close();

        }
        catch (Exception ex)
        {
            Log.d("ERROR", ex.getMessage());
            z=null;
        }

        return z;

    }
}
