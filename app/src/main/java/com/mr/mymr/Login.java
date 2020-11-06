package com.mr.mymr;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mr.mymr.utils.MrUtils;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Login extends AppCompatActivity {

    TextInputEditText txtEmailId;
    TextInputEditText password;
    Button btnLogin;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "LoginActivity";

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        txtEmailId = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPswd);
        btnLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.pBarLogin);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                if(firebaseUser != null) {
//                    Toast.makeText(Login.this, "You are logged in", Toast.LENGTH_SHORT);
                    launchMain(firebaseUser);
//                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    Toast.makeText(Login.this, "Login Invalid, please correct credentials.", Toast.LENGTH_LONG);
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmailId.getText().toString();
                String pwd = password.getText().toString();
                if (validate()) {
                    progressBar.setVisibility(View.VISIBLE);
                    Log.d(TAG, "BuildConfig ENV: " + BuildConfig.ENV);
//                    if (BuildConfig.ENV == "DEV") {
//                        Toast.makeText(Login.this, "Dev environment found!", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    }
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                Toast.makeText(Login.this, R.string.LoginFailure, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Login.this, "Welcome Again!", Toast.LENGTH_SHORT).show();
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(Login.this, R.string.correctAllErrors, Toast.LENGTH_SHORT).show();
                }
            }

            private boolean validate() {
                boolean isValid = true;
                String email = txtEmailId.getText().toString();
                String pwd = password.getText().toString();

                if (!MrUtils.isValidEmail(email)) {
                    txtEmailId.setError(getString(R.string.emailIsInvalid));
                    isValid = false;
                }

                if (pwd.isEmpty() || pwd.length() < 5 || pwd.length() > 8) {
                    password.setError(getString(R.string.passwordInvalid));
                    isValid = false;
                }
                return isValid;
            }
    });
    verifyPerms();
    }

    private void launchMain(FirebaseUser firebaseUser) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(getString(R.string.USER_DTL), firebaseUser.getDisplayName());
        startActivity(intent);
    }

    public void btn_signupForm(View view) {
        startActivity(new Intent(getApplicationContext(), Signup.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void verifyPerms() {
        if(hasStorageAccessPermission()) {
            Log.d(TAG, "Permission already granted.");
            Snackbar.make(btnLogin, "Permission already granted.", Snackbar.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "Please request the needed permission.");
            Snackbar.make(btnLogin, "Please request the needed permission.", Snackbar.LENGTH_LONG).show();
            requestPermission();
        }
    }

    /**
     * Check if we have permission to access the external storage
     * @return
     */
    public boolean hasStorageAccessPermission() {
        return ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    Snackbar.make(btnLogin, "Permission is granted, now you can save invoices.", Snackbar.LENGTH_LONG).show();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
                            showMessageOKCancel("You need to allow access to save invoices to external storage",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                return;
        }
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Login.this)
                .setMessage(message)
                .setNeutralButton("Ok", okListener)
//                .setPositiveButton("OK", okListener)
//                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}