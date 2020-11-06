package com.mr.mymr;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mr.mymr.utils.MrUtils;

public class Signup extends AppCompatActivity {
    TextInputEditText txtEmailId;
    TextInputEditText password, txtCnfrmPassword;
    Button btnSignUp;
    TextView tvSignIn;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        txtEmailId = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPswd);
        txtCnfrmPassword = findViewById(R.id.txtCnfrmPswd);
        tvSignIn = findViewById(R.id.tvGoLogin);
        btnSignUp = findViewById(R.id.btnRegister);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmailId.getText().toString();
                String pwd = password.getText().toString();
                if (validate()) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this, R.string.registrationFailure, Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(Signup.this, R.string.correctAllErrors, Toast.LENGTH_SHORT).show();
                }
            }

            private boolean validate() {
                boolean isValid = true;
                String email = txtEmailId.getText().toString();
                String pwd = password.getText().toString();
                String cfrmPwd = txtCnfrmPassword.getText().toString();

                if (!MrUtils.isValidEmail(email)) {
                    txtEmailId.setError(getString(R.string.emailIsInvalid));
                    isValid = false;
                }

                if (pwd.isEmpty() || pwd.length() < 5 || pwd.length() > 8) {
                    password.setError(getString(R.string.passwordInvalid));
                    isValid = false;
                }
                if (cfrmPwd.isEmpty()) {
                    txtCnfrmPassword.setError(getString(R.string.passwordMismatch));
                    isValid = false;
                }
                return isValid;
            }
        });
        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });
    }
}