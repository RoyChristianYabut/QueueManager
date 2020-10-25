package com.example.queuemanager;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;


import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionClass implements DBUtility {
    String classs = jdbcDriverName;

    String url= jdbcUrl;
    String un= dbUserName;
    String password= dbPassword;


    @SuppressLint("NewApi")
    public Connection CONN(){
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Security sec =new Security();
        Connection conn = null;
        String ConnURL = null;
        try{
            Class.forName(classs);

            conn= DriverManager.getConnection(url,un,password);

            //conn=DriverManager.getConnection(ConnURL);
        }catch(SQLException se){
            Log.e("ERRO", se.getMessage());
        }catch(ClassNotFoundException e){
            Log.e("ERROR", e.getMessage());
        }catch(Exception e){
            Log.e("ERRORS", e.getMessage());
        }
        return conn;
    }
}
