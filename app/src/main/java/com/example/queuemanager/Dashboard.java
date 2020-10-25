package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.queuemanager.adapter.DepartmentAdapter;
import com.example.queuemanager.list.DepartmentList;
import com.example.queuemanager.model.Department;
import com.example.queuemanager.session.KeruxSession;
import com.example.queuemanager.session.QueueSession;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Department> departments;
    private DepartmentAdapter departmentAdapter;

    private TextView qmname;

    private KeruxSession session;
    private QueueSession qs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        session = new KeruxSession(getApplicationContext());
        qs = new QueueSession(getApplicationContext());

        qmname = (TextView)findViewById(R.id.txtView_qmname);
        qmname.setText(session.getfirstname()+" "+session.getlastname());

        listView =(ListView) findViewById(R.id.listView_Departments);
        DepartmentList dl = new DepartmentList(Dashboard.this, listView, session.getclinicid());
        dl.execute();
        Log.d("HELP", session.getclinicid());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(Dashboard.this, DoctorSelect.class);
                Department item = (Department)listView.getAdapter().getItem(position);
                qs.setdepartmentid(Integer.toString(item.getDepartmentId()));
                qs.setdepartmentname(item.getDepartmentName());
                startActivity(intent);
            }
        });

    }
}