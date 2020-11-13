package com.example.queuemanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.queuemanager.adapter.DoctorAdapter;
import com.example.queuemanager.list.DoctorList;
import com.example.queuemanager.model.Doctor;
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

public class DoctorSelect extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Doctor> doctors;
    private DoctorAdapter doctorAdapter;

    private TextView depname;

    private KeruxSession session;
    private QueueSession qs;

    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_select);
        drawerLayout = findViewById(R.id.drawer_layout);

        session = new KeruxSession(getApplicationContext());
        qs=new QueueSession(getApplicationContext());
        qs.setqueueid(null);
        depname = (TextView)findViewById(R.id.txtView_departmentName);
        depname.setText(qs.getdepartmentname());

        listView =(ListView) findViewById(R.id.listView_Doctors);
        DoctorList dl = new DoctorList(DoctorSelect.this, listView, session.getclinicid());
        dl.execute();
        Log.d("HELP", session.getclinicid());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intentG = getIntent();
                Intent intent = new Intent(DoctorSelect.this, Queue.class);
                Bundle extras=intentG.getExtras();
                Doctor item = (Doctor)listView.getAdapter().getItem(position);
                extras.putString("DocID", Integer.toString(item.getDoctorid()) );
                qs.setdoctorid(Integer.toString(item.getDoctorid()));
                qs.setdoctorfirstname(item.getDoctorfirstname());
                qs.setdoctorlastname(item.getDoctorlastname());
                qs.setqueuename(item.getDoctorfirstname()+" "+item.getDoctorlastname());
                intent.putExtras(extras);
                startActivity(intent);
                finish();
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
                    .appendQueryParameter("first", sec.encrypt("Select Doctor"))
                    .appendQueryParameter("second", sec.encrypt("Doctor Select"))
                    .appendQueryParameter("third", sec.encrypt("Queue Manager selecting doctors to start queue"))
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
}