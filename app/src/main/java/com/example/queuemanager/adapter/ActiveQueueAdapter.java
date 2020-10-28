package com.example.queuemanager.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.queuemanager.R;
import com.example.queuemanager.model.ActiveQueue;
import com.example.queuemanager.model.Patient;

import java.util.ArrayList;

public class ActiveQueueAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ActiveQueue> queues;

    public ActiveQueueAdapter(Context context, ArrayList<ActiveQueue> queues) {
        this.context = context;
        this.queues = queues;
    }

    @Override
    public int getCount() {
        return queues.size();
    }

    @Override
    public Object getItem(int position) {
        return queues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = View.inflate(context , R.layout.dashboardselect ,null);
        }

        ImageView images = (ImageView) convertView.findViewById(R.id.activequeueimageView);
        TextView patNum = (TextView) convertView.findViewById(R.id.activequeueName);
        TextView status = (TextView)convertView.findViewById(R.id.activequeueDesc);
        ActiveQueue queue = queues.get(position);
        images.setImageResource(queue.getQueue_image());
        patNum.setText(queue.getQueueName());
        status.setText(queue.getPatientCount()+" patients in line");
        return convertView;
    }

    public void updateAdapter(ArrayList<ActiveQueue> arrylst) {
        this.queues= arrylst;

        //and call notifyDataSetChanged
        notifyDataSetChanged();
    }
}
