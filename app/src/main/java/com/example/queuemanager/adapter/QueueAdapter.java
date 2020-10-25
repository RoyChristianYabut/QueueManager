package com.example.queuemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.queuemanager.R;
import com.example.queuemanager.model.Doctor;
import com.example.queuemanager.model.Patient;

import java.util.ArrayList;

public class QueueAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Patient> patients;

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
        Patient patient = patients.get(position);
        images.setImageResource(patient.getPatientimage());
        patNum.setText("Patient # "+patient.getQueueNumber());
        status.setText(patient.getStatus());
        return convertView;
    }

    public void updateAdapter(ArrayList<Patient> arrylst) {
        this.patients= arrylst;

        //and call notifyDataSetChanged
        notifyDataSetChanged();
    }
}
