package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.queuemanager.adapter.DepartmentAdapter;
import com.example.queuemanager.list.ActiveQueueList;
import com.example.queuemanager.list.DepartmentList;
import com.example.queuemanager.model.ActiveQueue;
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

    private Button create_new_queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        session = new KeruxSession(getApplicationContext());
        qs = new QueueSession(getApplicationContext());
        qs.setqueueid(null);
        qmname = (TextView)findViewById(R.id.txtView_qmname);
        qmname.setText(session.getfirstname()+" "+session.getlastname());

        create_new_queue=(Button)findViewById(R.id.btnCreateNewQueue);

        listView =(ListView) findViewById(R.id.listView_ActiveQueues);
        ActiveQueueList aql = new ActiveQueueList(Dashboard.this, listView, session.getqueuemanagerid());
        aql.execute();
        Log.d("HELP", session.getclinicid());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int position, long arg) {
                Intent intent = new Intent(Dashboard.this, Queue.class);
                ActiveQueue item = (ActiveQueue) listView.getAdapter().getItem(position);
                qs.setqueueid(Integer.toString(item.getQueue_id()));
                qs.setqueuename(item.getQueueName());
                startActivity(intent);
            }
        });

        create_new_queue.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this,DepartmentSelect.class);
                startActivity(intent);
            }
        });

    }


}