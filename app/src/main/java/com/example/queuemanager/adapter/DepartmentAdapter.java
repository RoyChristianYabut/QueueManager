package com.example.queuemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.queuemanager.R;
import com.example.queuemanager.model.Department;

import java.util.ArrayList;

public class DepartmentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Department> departments;

    public DepartmentAdapter(Context context, ArrayList<Department> departments) {
        this.context = context;
        this.departments = departments;
    }

    @Override
    public int getCount() {
        return departments.size();
    }

    @Override
    public Object getItem(int position) {
        return departments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context , R.layout.list_items ,null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.departmentimageView);
        TextView depName = (TextView) convertView.findViewById(R.id.departmentName);
        Department department = departments.get(position);
        images.setImageResource(department.getDepartmentImage());
        depName.setText(department.getDepartmentName());

        return convertView;
    }
}
