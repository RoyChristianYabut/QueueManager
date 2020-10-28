package com.example.queuemanager.list;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.queuemanager.ConnectionClass;
import com.example.queuemanager.R;
import com.example.queuemanager.adapter.ActiveQueueAdapter;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.ActiveQueue;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.security.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ActiveQueueList extends AsyncTask<Void,Void,String> implements DBUtility {
    //FINISH THIS
    Context c;
    ListView lv;
    ProgressDialog pd;

    ArrayList<ActiveQueue> activequeuesList;
    ActiveQueueAdapter activequeueAdapter;
    ConnectionClass connectionClass;
    String queuemanager_id;

    public ActiveQueueList(Context c, ListView lv, String qmid) {
        this.c = c;
        this.lv = lv;
        this.connectionClass = new ConnectionClass();
        this.activequeuesList  = new ArrayList<>();
        this.queuemanager_id=qmid;
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
            activequeueAdapter = new ActiveQueueAdapter(c, activequeuesList);
            lv.setAdapter(activequeueAdapter);

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

                String query=SELECT_ACTIVE_QUEUE;

                PreparedStatement ps = con.prepareStatement(query);
                ps.setString(1, queuemanager_id);
                // stmt.executeUpdate(query);


                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    String qid=rs.getString(1);
                    String qname=rs.getString(2);
                    String pcount="";

                    String query1=SELECT_ACTIVE_QUEUE_NUMBER;

                    PreparedStatement ps1 = con.prepareStatement(query1);
                    ps1.setString(1, qid);
                    // stmt.executeUpdate(query);


                    ResultSet rs1=ps1.executeQuery();
                    while(rs1.next()){
                       pcount=rs1.getString(1);
                    }
                    activequeuesList.add(new ActiveQueue(R.drawable.briefcase, Integer.parseInt(qid), qname, pcount));
                }

                z="1";

            }
        }
        catch (Exception ex)
        {
            z=null;
            Log.d("Exceptionsss:", ex.getMessage());
        }

        return z;

    }
}
