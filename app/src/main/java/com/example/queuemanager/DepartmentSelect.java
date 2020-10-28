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

public class DepartmentSelect extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Department> departments;
    private DepartmentAdapter departmentAdapter;

    private TextView qmname;

    private KeruxSession session;
    private QueueSession qs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_select);

        session = new KeruxSession(getApplicationContext());
        qs = new QueueSession(getApplicationContext());
        qs.setqueueid(null);



        listView =(ListView) findViewById(R.id.listview_Departments);
        DepartmentList dl = new DepartmentList(DepartmentSelect.this, listView, session.getclinicid());
        dl.execute();
        Log.d("HELP", session.getclinicid());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(DepartmentSelect.this, DoctorSelect.class);
                Bundle extras = new Bundle();

                Department item = (Department)listView.getAdapter().getItem(position);
                qs.setdepartmentid(Integer.toString(item.getDepartmentId()));
                qs.setdepartmentname(item.getDepartmentName());

                extras.putString("DeptID",Integer.toString(item.getDepartmentId()));
                intent.putExtras(extras);
                startActivity(intent);
            }
        });
    }
}