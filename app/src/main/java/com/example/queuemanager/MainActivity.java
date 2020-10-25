package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.KeruxSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MainActivity extends AppCompatActivity implements DBUtility {

    private EditText username;
    private EditText password;
    private TextView attempt;
    private Button login_button;
    int attempt_counter=5;

    ProgressDialog progressDialog; //
    ConnectionClass connectionClass; //

    private KeruxSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionClass=new ConnectionClass();//

        progressDialog=new ProgressDialog(this);//

        username = (EditText)findViewById(R.id.editText_user);
        password = (EditText)findViewById(R.id.editText_password);
        login_button = (Button)findViewById(R.id.button_login);

        session = new KeruxSession(getApplicationContext());

        login_button.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                Dologin dologin=new Dologin();
                dologin.execute();
            }
        });
    }


    private class Dologin extends AsyncTask<String,String,String> {



        String usernam=username.getText().toString();
        String passstr=password.getText().toString();
        String z="";
        boolean isSuccess=false;

        String qmid, clinicid,  un, fn, ln, e;


        @Override
        protected void onPreExecute() {


            progressDialog.setMessage("Loading...");
            progressDialog.show();


            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            if(usernam.trim().equals("")||passstr.trim().equals(""))
                z = "Please enter all fields....";
            else
            {
                try {
                    Connection con = connectionClass.CONN();
                    Security sec =new Security();
                    if (con == null) {
                        z = "Please check your internet connection";
                    } else {

                        String query=LOGIN_CRED;

                        PreparedStatement ps = con.prepareStatement(query);
                        ps.setString(1, usernam);
                        ps.setString(2, passstr);
                        // stmt.executeUpdate(query);


                        ResultSet rs=ps.executeQuery();

                        while (rs.next())
                        {
                            qmid=rs.getString(1);
                            clinicid=rs.getString(2);
                            un=rs.getString(3);
                            fn=rs.getString(4);
                            ln=rs.getString(5);
                            e=rs.getString(6);
                            isSuccess=true;
                            z = "Login successfull";

                            session.setqueuemanagerid(qmid);
                            session.setclinicid(clinicid);
                            session.setusername(un);
                            session.setfirstname(fn);
                            session.setlastname(ln);
                            session.setemail(e);

                        }



                    }
                }
                catch (Exception ex)
                {
                    isSuccess = false;
                    z = "Exceptions"+ex;
                }
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();
            if(isSuccess) {

                Intent intent=new Intent(MainActivity.this,Dashboard.class);

                // intent.putExtra("name",usernam);

                startActivity(intent);
                finish();
            }




        }
    }
}