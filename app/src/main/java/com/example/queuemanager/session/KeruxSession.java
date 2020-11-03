package com.example.queuemanager.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KeruxSession {

    SharedPreferences.Editor editor;
    private SharedPreferences prefs;

    public KeruxSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
        editor = prefs.edit();
    }

    public void setinstanceid(String instanceid) {
        editor.putString("instanceid", instanceid).commit();
    }

    public String getinstanceid() {
        String instanceid = prefs.getString("instanceid","");
        return instanceid;
    }

    public void setemail(String email) {
        editor.putString("email", email).commit();
    }

    public String getemail() {
        String email = prefs.getString("email","");
        return email;
    }


    public void setpassword(String password) {
        editor.putString("password", password).commit();
    }

    public String getpassword() {
        String password = prefs.getString("password","");
        return password;
    }
    public void setqueuemanagerid(String queuemanagerid) {
        editor.putString("queuemanagerid", queuemanagerid).commit();
    }

    public String getqueuemanagerid() {
        String queuemanagerid = prefs.getString("queuemanagerid","");
        return queuemanagerid;
    }

    public void setclinicid(String clinicid) {
        editor.putString("clinicid", clinicid).commit();
    }

    public String getclinicid() {
        String clinicid = prefs.getString("clinicid","");
        return clinicid;
    }

    public void setusername(String username) {
        editor.putString("username", username).commit();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }

    public void setfirstname(String firstname) {
        editor.putString("firstname", firstname).commit();
    }

    public String getfirstname() {
        String firstname = prefs.getString("firstname","");
        return firstname;
    }

    public void setlastname(String lastname) {
        editor.putString("lastname", lastname).commit();
    }

    public String getlastname() {
        String lastname = prefs.getString("lastname","");
        return lastname;
    }

    public void destroySession() {
        editor.clear();
        editor.commit();
    }

}

