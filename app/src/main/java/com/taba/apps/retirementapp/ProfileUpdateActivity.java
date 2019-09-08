package com.taba.apps.retirementapp;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.extra.Device;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.financial_request.FinancialRequestAdapter;
import com.taba.apps.retirementapp.local.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileUpdateActivity extends AppCompatActivity {

    private ImageView profilePicture;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmationPassword;
    private Button btnUpdate;
    private ProgressBar progressBar;

    private Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_update);

        getSupportActionBar().setTitle(Html.fromHtml("<small>Update Profile</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.init();

        this.loadUserInfo();

        this.handleProfileUpdate();
    }

    private void init(){
        this.profilePicture = findViewById(R.id.profilePicture);
        this.currentPassword = findViewById(R.id.currentPassword);
        this.newPassword = findViewById(R.id.newPassword);
        this.confirmationPassword = findViewById(R.id.newPasswordConfirm);
        this.btnUpdate = findViewById(R.id.btnUpdateProfile);
        this.progressBar = findViewById(R.id.progressBar);

        this.employee = SharedPreference.getEmployee(getBaseContext());
    }

    private void loadUserInfo(){
        Picasso.with(getBaseContext()).load(employee.getPhoto()).fit().into(this.profilePicture);
    }

    private boolean validate(){

        if (TextUtils.isEmpty(currentPassword.getText().toString().trim())){
            currentPassword.setError("Current password is required");
            currentPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(newPassword.getText().toString().trim())){
            newPassword.setError("New password is required");
            newPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmationPassword.getText().toString().trim())){
            confirmationPassword.setError("Confirmation password is required");
            confirmationPassword.requestFocus();
            return false;
        }

        if (!currentPassword.getText().toString().equals(employee.getPassword())){
            currentPassword.setError("Incorrect password");
            currentPassword.requestFocus();
            return false;
        }

        if (!confirmationPassword.getText().toString().trim().equals(newPassword.getText().toString().trim())){
            confirmationPassword.setError("Confirmation password should match new password");
            confirmationPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void handleProfileUpdate(){
        this.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){
                    updateProfile();
                }
            }
        });
    }

    private void updateProfile(){
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());

        String url = Api.PROFILE_UPDATE;

        this.progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject responseObject = new JSONObject(response);


                    int responseType = responseObject.getInt("type");
                    String message = responseObject.getString("message");
                    if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                        SharedPreference.clearData(getBaseContext());
                        Toast.makeText(getBaseContext(),message,Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileUpdateActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();


                    } else {
                        //Temporary
                        // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {

                    //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

                //Set number of requests to actionbar


        }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                // Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                if (!Device.hasInternet(getBaseContext())) {
                    Snackbar.make(btnUpdate, "No Internet connection", Snackbar.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params = new HashMap<>();

                params.put("username",employee.getEmail());
                params.put("password",newPassword.getText().toString().trim());

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
