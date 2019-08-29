package com.taba.apps.retirementapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.local.SharedPreference;
import com.taba.apps.retirementapp.extra.Device;

public class LoginActivity extends AppCompatActivity {

    public static final int ACCESS_NETWORK_STATE_PERMISSION = 1;


    private EditText username;
    private EditText password;
    private Button login;
    public ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.handlePermissions();


        if (!SharedPreference.isFirstRun(getBaseContext())){
            Api.syncAccountDetails(getBaseContext());
            Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        this.init();

        this.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateCredentials()){
                    //Toast.makeText(getBaseContext(),"Everything is good to go", Toast.LENGTH_SHORT).show();

                    Api.login(getBaseContext(),username.getText().toString(),password.getText().toString(),password,LoginActivity.this, progressBar);

                }
            }
        });

    }

    private void init() {
        this.getSupportActionBar().setTitle(Html.fromHtml("<small>Welcome to Graduates Retirement APP</small>"));

        this.username = findViewById(R.id.username);
        this.password = findViewById(R.id.password);
        this.login = findViewById(R.id.login);
        this.progressBar = findViewById(R.id.progressBar);
    }

    private boolean validateCredentials() {

        String username = this.username.getText().toString();
        String password = this.password.getText().toString();

        if (TextUtils.isEmpty(username)) {
            this.username.setError("Username can not be blank");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            this.password.setError("Password can not be blank");
            return false;
        }

        //Other validation will require checking if username and password exists
        //From the server

        return true;
    }

    private void handlePermissions() {
        //Access Network State Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, ACCESS_NETWORK_STATE_PERMISSION);
        }
    }

}
