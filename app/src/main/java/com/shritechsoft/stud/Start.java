package com.shritechsoft.stud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class Start extends AppCompatActivity {
    SessionManager sessionManager;
    private TextView name,email;
    Button logout,edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

   
        edit = findViewById(R.id.edit);
        logout = findViewById(R.id.logot);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        HashMap<String,String> user =sessionManager.getUserDetails();
        String mEmail = user.get(sessionManager.EMAIL);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Start.this,Profile.class));
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logOut();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        finishAffinity();
    }
}
