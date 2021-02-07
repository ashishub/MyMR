package com.mr.mymr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.util.CollectionUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mr.mymr.dto.InvoiceDTO;
import com.mr.mymr.dto.ItemDTO;
import com.mr.mymr.utils.MrUtils;
import com.mr.mymr.utils.PDFGenerator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withModifier;
import static org.reflections.ReflectionUtils.withParametersCount;
import static org.reflections.ReflectionUtils.withPrefix;

public class InvoiceActivity extends AppCompatActivity {

    private static String TAG = "InvoiceActivity";

    FloatingActionButton printButton, closeBtn;
    WebView myWebView;

    String NONE_DISPLAY_STYLE = "none";

    String DISPLAY_STYLE_TABLE_ROW = "table-row";

    String EMPTY_ITEM_ROW = "<tr>\n" +
            "                    <td colspan=\"14\" style=\"padding-bottom: {LENGTH}px;\"></td>\n" +
            "                </tr>";

    static String ITEM_ROW = "<tr>\n" +
            "    <td class=\"itemMiddleAlign\">${getSNo}</td>\n" +
            "    <td style=\"min-width: 170px;\">${getItemDesc}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getHsnCode}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getUom}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getQuantity}</td>\n" +
            "    <td class=\"amountRightAlign\">${getRate}</td>\n" +
            "    <td class=\"amountRightAlign\">${getTaxableValue}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getCGstPerctage}</td>\n" +
            "    <td class=\"amountRightAlign\">${getCsgt}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getSGstPerctage}</td>\n" +
            "    <td class=\"amountRightAlign\">${getSgst}</td>\n" +
            "    <td class=\"itemMiddleAlign\">${getIGstPerctage}</td>\n" +
            "    <td class=\"amountRightAlign\">${getIgst}</td>\n" +
            "    <td class=\"amountRightAlign\">${getLineTotal}</td>\n" +
            "</tr>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        myWebView = findViewById(R.id.myWebView);
        closeBtn = findViewById(R.id.closePDFBtn);
        printButton = findViewById(R.id.floatingPrintBtn);
        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Printing invoice");
                createWebPrintJob(myWebView);
                Log.d(TAG, "Saved invoice successfully!");
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //InvoiceActivity.super.onBackPressed();
                finish();
            }
        });

        webviewLoad(prepareInvoicePDFData());
    }

    //TODO - Set Default values


    private String prepareInvoicePDFData() {
        Intent i = getIntent();
        InvoiceDTO invoiceDTO = null;
        PDFGenerator pdfGenerator = PDFGenerator.getInstance();
        String methodName = null;
        String fieldValue = null;
        Method getMeth = null;
        String invoiceTemplate = null;

        try {
            invoiceDTO = (InvoiceDTO)i.getSerializableExtra(getString(R.string.INVOICE_DATA));
            if(invoiceDTO != null) {
                invoiceTemplate = pdfGenerator.readInvoiceTemplate(InvoiceActivity.this);
                String value = null;
                Set<Method> getters = getAllMethods(InvoiceDTO.class,
                        withModifier(Modifier.PUBLIC), withPrefix("get"), withParametersCount(0));
                Iterator<Method> itr = getters.iterator();
                while(itr.hasNext()) {
                    getMeth = itr.next();
                    methodName = getMeth.getName();
                    Log.d(TAG, "Name = " + methodName);
                    if (methodName.contains("getItems")) {
                        invoiceTemplate = prepareItems(invoiceDTO.getItems(), invoiceTemplate);
                        continue;
                    }
                    fieldValue = (String) getMeth.invoke(invoiceDTO);
                    Log.d(TAG, "Name = " + methodName + ", Value = " + fieldValue);
                    invoiceTemplate = invoiceTemplate.replace("${"+methodName+"}", MrUtils.returnString(fieldValue));
                }
            }
            //set Additional charges style
            if (!TextUtils.isEmpty(invoiceDTO.getFreightTaxableAmt())) {
                invoiceTemplate = invoiceTemplate.replace("${DISPLAY_STYLE}" , DISPLAY_STYLE_TABLE_ROW);
            } else {
                invoiceTemplate = invoiceTemplate.replace("${DISPLAY_STYLE}" , NONE_DISPLAY_STYLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoiceTemplate;
    }

    private String prepareItems(List<ItemDTO> itemDTOS, String invoiceTemplate) {
        String itemRow = null;
        String allItems = "";
        String methodName = null;
        String fieldValue = null;
        Object fieldObject = null;
        Method getMeth = null;
        Iterator<Method> itr;
        Set<Method> getters;
        try {
            if(!CollectionUtils.isEmpty(itemDTOS)) {
                // Add empty Rows as needed
                invoiceTemplate = invoiceTemplate.replace("${emptyRows}" , addEmptyRows(itemDTOS.size()));

                for (ItemDTO itemDTO : itemDTOS) {
                    itemRow = ITEM_ROW;
                    getters = getAllMethods(ItemDTO.class, withModifier(Modifier.PUBLIC), withPrefix("get"), withParametersCount(0));
                    itr = getters.iterator();
                    while(itr.hasNext()) {
                        getMeth = itr.next();
                        methodName = getMeth.getName();
                        Log.d(TAG, "Name = " + methodName);
                        if (methodName.contains("InterState")) {
                            continue;
                        }
                        fieldObject = getMeth.invoke(itemDTO);
                        if (fieldObject instanceof Double) {
                            itemRow = itemRow.replace("${"+methodName+"}", MrUtils.returnStringForDouble((Double) fieldObject));
                        } else {
                            fieldValue = (String) getMeth.invoke(itemDTO);
                            itemRow = itemRow.replace("${"+methodName+"}", MrUtils.returnString(fieldValue));
                        }
                        Log.d(TAG, "Name = " + methodName + ", Value = " + fieldValue);
                    }
                    allItems += itemRow;
                }
                invoiceTemplate = invoiceTemplate.replace("${allLineItems}" , allItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoiceTemplate;
    }

    private String addEmptyRows(int noOfItems) {
        int pixelPerLine = 25;
        int maxPixels = 275;
        int emptyRowPixels = 0;
        if (noOfItems <= 11) {
            emptyRowPixels = maxPixels - (noOfItems * pixelPerLine);
        }
        EMPTY_ITEM_ROW = EMPTY_ITEM_ROW.replace("{LENGTH}", String.valueOf(emptyRowPixels));
        return EMPTY_ITEM_ROW;
    }

    private void webviewLoad(String unencodedHtml) {
        if (unencodedHtml != null) {
            WebView webView = findViewById(R.id.myWebView);
            webView.clearHistory();
            webView.getSettings().setJavaScriptEnabled(true);
            String encodedHtml = Base64.encodeToString(unencodedHtml.getBytes(), Base64.NO_PADDING);
            webView.loadData(encodedHtml, "text/html", "base64");
        } else {
            Toast.makeText(this, "Invoice Data not present, cannot display invoice", Toast.LENGTH_LONG).show();
        }
    }

    //create a function to create the print job
    private void createWebPrintJob(WebView webView) {

        //create object of print manager in your device
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        //provide name to your newly generated pdf file
        String jobName = getString(R.string.app_name) + " Print Test";

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.UNKNOWN_LANDSCAPE)
                . build();

        //create object of print adapter
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter("Invoices.pdf");

        //open print dialog
        printManager.print(jobName, printAdapter, printAttributes);
    }
}