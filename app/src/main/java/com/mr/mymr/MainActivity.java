package com.mr.mymr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton btnLogout;
    LinearLayout layoutCreateInvoice;
    FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    TextView welcomeTxtvw;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

//        welcomeTxtvw = findViewById(R.id.welcomeTxtvw);
//        btnLogout = findViewById(R.id.btnLogout);
        layoutCreateInvoice = findViewById(R.id.bthCreateInvoice);



//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(MainActivity.this, Login.class));
//            }
//        });


        layoutCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Create Invoice invoked", Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this, CreateInvoiceActivity.class));
            }
        });
        Intent i = getIntent();
        String userName = (String) i.getSerializableExtra(getString(R.string.USER_DTL));
        if (userName != null) {
//            welcomeTxtvw.setText("Welcome " + userName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back_to_home:
//                onAddItemsClicked();
                break;
            case R.id.menu_sign_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, Login.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        // Don't respond to back button from the main page.
    }
}