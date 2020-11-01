package com.example.queuemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.queuemanager.ConnectionClass;
import com.example.queuemanager.R;
import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.model.Patient;
import com.example.queuemanager.security.Security;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class QueueAdapter extends BaseAdapter implements DBUtility {
    private Context context;
    private ArrayList<Patient> patients;
    private int queueid;

    ArrayList<Patient> patientsList;
    ConnectionClass connectionClass;

    public QueueAdapter(Context context, ArrayList<Patient> patients) {
        this.context = context;
        this.patients = patients;
    }

    @Override
    public int getCount() {
        return patients.size();
    }

    @Override
    public Object getItem(int position) {
        return patients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context , R.layout.queueselect ,null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.patientimageView);
        TextView patNum = (TextView) convertView.findViewById(R.id.patientNumber);
        TextView status = (TextView)convertView.findViewById(R.id.patientStatus);
        TextView queuetype = (TextView)convertView.findViewById(R.id.patientType);
        Patient patient = patients.get(position);
        images.setImageResource(patient.getPatientimage());
        patNum.setText("Patient # "+patient.getQueueNumber());
        status.setText(patient.getStatus());
        queuetype.setText(patient.getQueuetype());
        return convertView;
    }

    public int getQueueid() {
        return queueid;
    }

    public void setQueueid(int queueid) {
        this.queueid = queueid;
    }

    public void updateAdapter(int qid ) {
        queueid=qid;
        this.patients= this.retrieveData();

        //and call notifyDataSetChanged
        notifyDataSetChanged();
    }

    private ArrayList<Patient> retrieveData()
    {
        patientsList=new ArrayList<>();
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
                ps.setInt(1, queueid);
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

        return patientsList;

    }
}
