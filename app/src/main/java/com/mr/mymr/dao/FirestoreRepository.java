/*
package com.mr.mymr.dao;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mr.mymr.dto.InvoiceDTO;

import java.util.Date;

public class FirestoreRepository {
    private FirebaseFirestore mFirestore;

    private static String TAG = "FirestoreRepository";

    public FirestoreRepository() {
        initFirestore();
    }

    private void initFirestore() {
        if(mFirestore == null) {
            mFirestore = FirebaseFirestore.getInstance();
            // Enable Firestore logging
            FirebaseFirestore.setLoggingEnabled(true);
        }
    }



    public void saveInvoice(InvoiceDTO invoiceDTO) {
        // Get a reference to the restaurants collection
        if (invoiceDTO != null) {
            invoiceDTO.setSaveTimestamp(new Date().toString());
            CollectionReference invoices = mFirestore.collection("invoices");
            invoices.add(invoiceDTO);
//            invoices.add(invoiceDTO)
////                    .addOnSuccessListener(documentReference -> {
////                        Log.d(TAG, "Document saved successfully with Id:" + documentReference.getId());
////                    }).addOnFailureListener(e -> {
////                        Log.w(TAG, "Error adding Invoice", e);
////                    });

        }

    }


}
*/
