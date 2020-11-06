package com.mr.mymr.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.mr.mymr.R;

public class AppPermissionHelper {

    private static final int PERMISSION_REQUEST_CODE = 200;

    public static void verifyPerms(Activity activity) {
        if(hasStorageAccessPermission(activity.getApplicationContext())) {
            Snackbar.make(activity.findViewById(R.id.btnLogin), "Permission already granted.", Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(activity.findViewById(R.id.btnLogin), "Please request permission.", Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * Check if we have permission to access the external storage
     * @param context
     * @return
     */
    public static boolean hasStorageAccessPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

}
