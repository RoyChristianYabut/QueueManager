package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

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

import com.example.queuemanager.dbutility.DBUtility;
import com.example.queuemanager.model.Patient;
import com.example.queuemanager.security.Security;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GenerateReport extends AppCompatActivity implements DBUtility {

    private String queueid;
    private Button generateReport;
    File myFile;
    ProgressDialog progressDialog;
    ConnectionClass connectionClass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);

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
            }
        });

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
            Connection con = connectionClass.CONN();
            if (con == null) {
                error="Please check your internet connection";
            } else {

                String queryCOUNT=SELECT_REPORT_QUEUE_NUMBER;

                PreparedStatement ps = con.prepareStatement(queryCOUNT);
                ps.setString(1, queueid);

                ResultSet rs=ps.executeQuery();

                while (rs.next())
                {
                    data.add("Total Number of Patients:         ");
                    data.add(rs.getString(1));
                }

                String querySERVED=SELECT_REPORT_QUEUE_SERVED;

                PreparedStatement ps2 = con.prepareStatement(querySERVED);
                ps2.setString(1, queueid);

                ResultSet rs2=ps2.executeQuery();

                while (rs2.next())
                {
                    data.add("Number of Patients Served:      ");
                    data.add(rs2.getString(1));
                }

                String queryCANCELLED=SELECT_REPORT_QUEUE_CANCELLED;

                PreparedStatement ps3 = con.prepareStatement(queryCANCELLED);
                ps3.setString(1, queueid);

                ResultSet rs3=ps3.executeQuery();

                while (rs3.next())
                {
                    data.add("Number of Patients Cancelled: ");
                    data.add(rs3.getString(1));
                }

                String queryLIST=SELECT_REPORT_QUEUE_LIST;

                PreparedStatement ps4 = con.prepareStatement(queryLIST);
                ps4.setString(1, queueid);

                ResultSet rs4=ps4.executeQuery();

                data.add("------------");
                data.add("------------");
                data.add("Queue Number");
                data.add("Status");

                while (rs4.next())
                {
                    data.add(rs4.getString(1));
                    data.add(rs4.getString(2));
                }

            }
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