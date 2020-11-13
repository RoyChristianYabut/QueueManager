package com.example.queuemanager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.security.Security;
import com.example.queuemanager.session.KeruxSession;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GenerateReport extends AppCompatActivity implements DBUtility {

    private String queueid;
    private Button generateReport;
    File myFile;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;

    DrawerLayout drawerLayout;
    KeruxSession session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);
        drawerLayout = findViewById(R.id.drawer_layout);
        session=new KeruxSession(getApplicationContext());

        progressDialog=new ProgressDialog(this);
        connectionClass =new ConnectionClass();

        Intent i= getIntent();
        queueid=i.getStringExtra("queueid");

        generateReport=(Button)findViewById(R.id.btnGenerateReport);
        generateReport.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                GenerateRep generateRep = new GenerateRep();
                generateRep.execute();
                insertAudit();
            }
        });

    }

    //insert to audit logs
    public void insertAudit(){

        Security sec = new Security();

        try {
            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/InsertAuditAdminServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("first", sec.encrypt("Generate Reports"))
                    .appendQueryParameter("second", sec.encrypt("Queue Manager generate queue reports"))
                    .appendQueryParameter("third", sec.encrypt("Queue Manager generating queue reports"))
                    .appendQueryParameter("fourth", sec.encrypt("none"))
                    .appendQueryParameter("fifth", sec.encrypt("Queue Manager ID: " + session.getqueuemanagerid()))
                    .appendQueryParameter("sixth", session.getqueuemanagerid());
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, StandardCharsets.UTF_8));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String returnString="";
            ArrayList<String> output=new ArrayList<String>();
            while ((returnString = in.readLine()) != null)
            {
                Log.d("returnString", returnString);
                output.add(returnString);
            }
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClickMenu (View view){
        //open drawer
        Dashboard.openDrawer(drawerLayout);
    }

    public void ClickLogo (View view){
        //Close drawer
        Dashboard.closeDrawer(drawerLayout);
    }

    public void ClickEditProfile(View view){
        //Redirect activity to dashboard
        Dashboard.redirectActivity(this, EditProfile.class);
    }

    public void ClickDashboard(View view){
        //Redirect activity to dashboard
        Dashboard.redirectActivity(this, Dashboard.class);
    }

    public void ClickCurrentQueue(View view){
        //Redirect activity to manage accounts
        Dashboard.redirectActivity(this, Queue.class);
    }

    public void ClickLogout(View view){
        Dashboard.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        Dashboard.closeDrawer(drawerLayout);
    }

    private class GenerateRep extends AsyncTask<String,String,String> {
        String z="";
        @Override
        protected void onPreExecute() {

            progressDialog.setMessage("Loading...");
            progressDialog.show();

            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            z="";

            try {
                Connection con = connectionClass.CONN();
                Security sec =new Security();
                if (con == null) {
                    z = "Please check your internet connection";
                } else {
                    createPdf();
                    emailNote();

                    z="Report Generated";
                }
            }
            catch (Exception ex)
            {
                z = "Exceptions"+ex;
            }
            return z;
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(getBaseContext(),""+z,Toast.LENGTH_LONG).show();

            progressDialog.dismiss();

        }
    }


    private void createPdf() throws FileNotFoundException, DocumentException {

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
            Log.i("LOG_TAG", "Pdf Directory created");
        }

        //Create time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        myFile = new File(pdfFolder + timeStamp + ".pdf");

        OutputStream output = new FileOutputStream(myFile);

        Document document = new Document(PageSize.LETTER);

        ArrayList<String> getData=retrieveData();

        PdfWriter.getInstance(document, output);

        document.open();
        document.add(new Paragraph("KERUX QUEUE REPORT"));
        int counter=1;
        String line="";
        for (int i=0;i<getData.size();i++){
//            document.add(new Paragraph(getData.get(i)));
            if(counter==3){
                document.add(new Paragraph(line));
                line="";
                counter=1;
            }


            if(counter!=3){
                if (counter==2){
                    line+=getData.get(i);
                }
                else{
                    line+=getData.get(i)+" | ";
                }
                counter++;
            }
        }



        document.close();
    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(GenerateReport.this,
                BuildConfig.APPLICATION_ID + ".provider",
                myFile);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void emailNote()
    {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,"SUBJECT");
        email.putExtra(Intent.EXTRA_TEXT, "TEXT");
        Uri uri = FileProvider.getUriForFile(GenerateReport.this,
                BuildConfig.APPLICATION_ID + ".provider",
                myFile);

        Log.d("EMAIL", String.valueOf(uri));

        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    private ArrayList<String> retrieveData()
    {
        ArrayList<String> data=new ArrayList<>();
        String error="";
        try {


            URL url = new URL("https://isproj2a.benilde.edu.ph/Sympl/GeneratePDFDataQMServlet");
            URLConnection connection = url.openConnection();

            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("queueid", queueid);
            String query = builder.build().getEncodedQuery();

            OutputStream os = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String returnString="";

            while ((returnString = in.readLine()) != null)
            {
                Log.d("returnString", returnString);
                data.add(returnString);
            }

            in.close();


        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        if(!error.equals("")){
            Toast.makeText(getBaseContext(),error,Toast.LENGTH_LONG).show();
        }
        return data;

    }

//    private void promptForNextAction()
//    {
//        final String[] options = { getString(R.string.label_email), getString(R.string.label_preview),
//                getString(R.string.label_cancel) };
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        builder.setTitle("Note Saved, What Next?");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (options[which].equals(getString(R.string.label_email))){
//                    emailNote();
//                }else if (options[which].equals(getString(R.string.label_preview))){
//                    viewPdf();
//                }else if (options[which].equals(getString(R.string.label_cancel))){
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        builder.show();
//
//    }
}