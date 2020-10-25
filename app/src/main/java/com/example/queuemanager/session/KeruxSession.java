package com.example.queuemanager.session;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class KeruxSession {

    private SharedPreferences prefs;

    public KeruxSession(Context cntx) {
        // TODO Auto-generated constructor stub
        prefs = PreferenceManager.getDefaultSharedPreferences(cntx);
    }

    public void setinstanceid(String instanceid) {
        prefs.edit().putString("instanceid", instanceid).commit();
    }

    public String getinstanceid() {
        String instanceid = prefs.getString("instanceid","");
        return instanceid;
    }

    public void setemail(String email) {
        prefs.edit().putString("email", email).commit();
    }

    public String getemail() {
        String email = prefs.getString("email","");
        return email;
    }


    public void setpassword(String password) {
        prefs.edit().putString("password", password).commit();
    }

    public String getpassword() {
        String password = prefs.getString("password","");
        return password;
    }
    public void setqueuemanagerid(String queuemanagerid) {
        prefs.edit().putString("queuemanagerid", queuemanagerid).commit();
    }

    public String getqueuemanagerid() {
        String queuemanagerid = prefs.getString("queuemanagerid","");
        return queuemanagerid;
    }

    public void setclinicid(String clinicid) {
        prefs.edit().putString("clinicid", clinicid).commit();
    }

    public String getclinicid() {
        String clinicid = prefs.getString("clinicid","");
        return clinicid;
    }

    public void setusername(String username) {
        prefs.edit().putString("username", username).commit();
    }

    public String getusername() {
        String username = prefs.getString("username","");
        return username;
    }

    public void setfirstname(String firstname) {
        prefs.edit().putString("firstname", firstname).commit();
    }

    public String getfirstname() {
        String firstname = prefs.getString("firstname","");
        return firstname;
    }

    public void setlastname(String lastname) {
        prefs.edit().putString("lastname", lastname).commit();
    }

    public String getlastname() {
        String lastname = prefs.getString("lastname","");
        return lastname;
    }
}

