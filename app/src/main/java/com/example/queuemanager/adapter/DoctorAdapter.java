package com.example.queuemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.queuemanager.R;
import com.example.queuemanager.model.Doctor;

import java.util.ArrayList;

public class DoctorAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Doctor> doctors;

    public DoctorAdapter(Context context, ArrayList<Doctor> doctors) {
        this.context = context;
        this.doctors = doctors;
    }

    @Override
    public int getCount() {
        return doctors.size();
    }

    @Override
    public Object getItem(int position) {
        return doctors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context , R.layout.doctorselect ,null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.doctorimageView);
        TextView docName = (TextView) convertView.findViewById(R.id.doctorName);
        Doctor doctor = doctors.get(position);
        images.setImageResource(doctor.getDoctorimage());
        docName.setText(doctor.getDoctorfirstname()+" "+doctor.getDoctorlastname());

        return convertView;
    }
}
