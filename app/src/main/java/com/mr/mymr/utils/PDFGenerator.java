package com.mr.mymr.utils;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

//import com.itextpdf.html2pdf.HtmlConverter;
import com.mr.mymr.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.mr.mymr.utils.AppPermissionHelper.hasStorageAccessPermission;

public class PDFGenerator {
    private static PDFGenerator instance = new PDFGenerator();

    private PDFGenerator(){
    }

    public static PDFGenerator getInstance() {
        return instance;
    }

    public void generateInvoice(Context context) {
        try {
            String invoiceTemplateString = readInvoiceTemplate(context);
//            HtmlConverter.convertToPdf(invoiceTemplateString, new FileOutputStream(context.getFilesDir() + "string-to-pdf.pdf"));

            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/invoices";
//            String path = context.getFilesDir() + "/invoices";
//            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/invoices";

            File dir = new File(path);
            if(!dir.exists())
                dir.mkdirs();

//            File file = new File(dir, "invoice.pdf");
            if (hasStorageAccessPermission(context)) {

//                ContextWrapper cw = new ContextWrapper(context);
//                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

//                File file = new File(Environment.getExternalStorageDirectory(), "/invoice.pdf");
                File file = new File(dir, "/invoice.pdf");
//                File file = new File(directory, "invoice.pdf");
                file.createNewFile();

//                HtmlConverter.convertToPdf(invoiceTemplateString, new FileOutputStream(file));

                System.out.println( "PDF Created!" );

//                viewPdf("invoice.pdf", "invoices", context);
            } else {
                Log.d("PDFGenerator", "Permission not available");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method for opening a pdf file
    private void viewPdf(String file, String directory, Context context) {

//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + directory + "/" + file);
//        File pdfFile = new File(context.getFilesDir() + "/" + directory + "/" + file);
        File pdfFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + directory + "/" + file);
//        File pdfFile = new File(Environment.getExternalStorageDirectory() + "/" + file);
        Uri path = Uri.fromFile(pdfFile);

        // Setting the intent for pdf reader
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        try {
            context.startActivity(pdfIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Can't read pdf file", Toast.LENGTH_SHORT).show();
        }
    }

    public String readInvoiceTemplate(Context context) throws IOException {
        InputStream is = context.getResources().openRawResource(R.raw.invoice);
        String invoiceTemplate = null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder out = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
        }
        invoiceTemplate = out.toString();
        Log.d("HTML_TEMPLATE", invoiceTemplate);
        reader.close();
        return invoiceTemplate;
    }
}
