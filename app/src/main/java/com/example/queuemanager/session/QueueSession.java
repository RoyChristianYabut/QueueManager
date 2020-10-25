package com.example.queuemanager.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueueSession {

    private SharedPreferences prefs;

    public QueueSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setdepartmentid(String departmentid) {
        prefs.edit().putString("departmentid", departmentid).commit();
    }

    public String getdepartmentid() {
        String departmentid = prefs.getString("departmentid","");
        return departmentid;
    }

    public void setdoctorid(String doctorid) {
        prefs.edit().putString("doctorid", doctorid).commit();
    }

    public String getdoctorid() {
        String doctorid = prefs.getString("doctorid","");
        return doctorid;
    }

    public void setdepartmentname(String departmentname) {
        prefs.edit().putString("departmentname", departmentname).commit();
    }

    public String getdepartmentname() {
        String departmentname = prefs.getString("departmentname","");
        return departmentname;
    }

    public void setdoctorfirstname(String doctorfirstname) {
        prefs.edit().putString("doctorfirstname", doctorfirstname).commit();
    }

    public String getdoctorfirstname() {
        String doctorfirstname = prefs.getString("doctorfirstname","");
        return doctorfirstname;
    }

    public void setdoctorlastname(String doctorlastname) {
        prefs.edit().putString("doctorlastname", doctorlastname).commit();
    }

    public String getdoctorlastname() {
        String doctorlastname = prefs.getString("doctorlastname","");
        return doctorlastname;
    }

    public void setqueueid(String queueid) {
        prefs.edit().putString("queueid", queueid).commit();
    }

    public String getqueueid() {
        String queueid = prefs.getString("queueid","");
        return queueid;
    }
}
