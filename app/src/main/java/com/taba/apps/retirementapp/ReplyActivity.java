package com.taba.apps.retirementapp;

import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.financial_request.Remark;
import com.taba.apps.retirementapp.financial_request.RemarkAdapter;
import com.taba.apps.retirementapp.local.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReplyActivity extends AppCompatActivity {

    private EditText inputRemarks;
    private Button btnSendRemarks;
    private FinancialRequest request;
    private Employee employee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        getSupportActionBar().setTitle(Html.fromHtml("<small>Add Remarks</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.inputRemarks = findViewById(R.id.inputRemarks);
        this.btnSendRemarks = findViewById(R.id.btnSendRemarks);

        this.request = (FinancialRequest) getIntent().getSerializableExtra("request");
        this.employee = SharedPreference.getEmployee(getBaseContext());

        this.hanldeReply();

    }

    private boolean validateReply() {

        if (TextUtils.isEmpty(this.inputRemarks.getText().toString())) {
            this.inputRemarks.setError("Remarks can not be blank");
            return false;
        }

        return true;
    }

    private void hanldeReply() {

        this.btnSendRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validateReply()) {

                    RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                    String url = Api.FINANCIAL_REQUEST_ADD_REMARK + "&id=" + request.getId();


                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject responseObject = new JSONObject(response);
                                int responseType = responseObject.getInt("type");

                                String message = responseObject.getString("message");
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();

                                if (responseType == Api.RESPONSE_TYPE_SUCCESS) {
                                    Intent intent = new Intent(ReplyActivity.this, SingleRequestActivity.class);
                                    intent.putExtra("request", request);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap<>();

                            params.put("words", inputRemarks.getText().toString());
                            params.put("employee_id", employee.getId() + "");

                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);


                }

            }
        });


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
