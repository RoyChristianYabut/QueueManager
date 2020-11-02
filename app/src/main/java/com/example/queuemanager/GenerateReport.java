package com.example.queuemanager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GenerateReport extends AppCompatActivity {

    private String queueid;
    private Button generateReport;
    File myFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_report);


        Intent i= getIntent();
        queueid=i.getStringExtra("queueid");

        generateReport=(Button)findViewById(R.id.btnGenerateReport);
        generateReport.setOnClickListener(new View.OnClickListener() {//
            @Override
            public void onClick(View v) {
                try {
                    createPdf();
//                    viewPdf();
                    emailNote();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
            }
        });

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

        PdfWriter.getInstance(document, output);

        document.open();
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));
        document.add(new Paragraph("Sample YANYAN"));

        document.close();
    }

    private void viewPdf(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(FileProvider.getUriForFile(GenerateReport.this,
                BuildConfig.APPLICATION_ID + ".provider",
                myFile), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
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