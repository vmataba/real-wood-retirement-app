package com.taba.apps.retirementapp;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.extra.Tool;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.financial_request.FinancialRequestAdapter;
import com.taba.apps.retirementapp.financial_request.FinancialRequestItem;
import com.taba.apps.retirementapp.local.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewRequestActivity extends AppCompatActivity {


    private EditText inputTotalAmount;
    private EditText inputRemarks;
    private ImageButton btnNewItem;
    private LinearLayout requestItems;
    private TextView requestItemsCount;
    private Button btnSendRequest;
    private ProgressBar progressBar;

    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_request);

        getSupportActionBar().setTitle(Html.fromHtml("<small>Add Cash Requisition</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.init();


        //Handle Items
        this.handleNewItem();

        //Process financial request
        this.sendRequest();
    }

    private void init(){
        this.inputTotalAmount = findViewById(R.id.inputAmount);
        this.inputRemarks = findViewById(R.id.inputDescription);
        this.btnNewItem = findViewById(R.id.btnNewItem);
        this.requestItems = findViewById(R.id.requestItems);
        this.requestItemsCount = findViewById(R.id.requestItemsCount);
        this.btnSendRequest = findViewById(R.id.btnSendRequest);
        this.progressBar = findViewById(R.id.progressBar);
    }


    private void handleNewItem() {
        this.btnNewItem.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                LinearLayout singleItem = (LinearLayout) getLayoutInflater().inflate(R.layout._request_item, null);
                if (requestItems.getChildCount() < 10) {
                    requestItems.addView(singleItem);
                    requestItemsCount.setText("Request Items (" + requestItems.getChildCount() + ")");
                } else {
                    Toast.makeText(getBaseContext(), "You have reached maximum Items count", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getBaseContext(), "Create another request", Toast.LENGTH_SHORT).show();
                }

                handleItemEvents();

            }
        });


    }


    private boolean validate() {

        String amount = inputTotalAmount.getText().toString();
        String remarks = inputRemarks.getText().toString();

        if (TextUtils.isEmpty(amount)) {
            inputTotalAmount.requestFocus();
            inputTotalAmount.setError("This field can not be blank");
            return false;
        }

        if (TextUtils.isEmpty(remarks)) {
            inputRemarks.requestFocus();
            inputRemarks.setError("This field can not be blank");
            return false;
        }

        for (int index = 0; index < requestItems.getChildCount(); index++) {

            LinearLayout parent = (LinearLayout) requestItems.getChildAt(index);
            LinearLayout amountLayout = (LinearLayout) parent.getChildAt(0);
            final EditText inputAmount = (EditText) amountLayout.getChildAt(1);
            if (TextUtils.isEmpty(inputAmount.getText().toString())) {
                inputAmount.requestFocus();
                inputAmount.setError("This is required");
                return false;
            }

            LinearLayout descriptionLayout = (LinearLayout) parent.getChildAt(1);
            EditText inputDescription = (EditText) descriptionLayout.getChildAt(1);
            if (TextUtils.isEmpty(inputDescription.getText().toString())) {
                inputDescription.requestFocus();
                inputDescription.setError("This field is required");
                return false;
            }

        }

        if (requestItems.getChildCount() < 1) {
            Toast.makeText(getBaseContext(), "You must create at least 1 Item", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void handleItemEvents() {

        for (int index = 0; index < requestItems.getChildCount(); index++) {

            LinearLayout parent = (LinearLayout) requestItems.getChildAt(index);
            LinearLayout amountLayout = (LinearLayout) parent.getChildAt(1);
            final EditText inputAmount = (EditText) amountLayout.getChildAt(1);

            inputAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                }

                @Override
                public void afterTextChanged(Editable editable) {

                    double totalAmount = 0;

                    for (int innerIndex = 0; innerIndex < requestItems.getChildCount(); innerIndex++) {

                        LinearLayout parent = (LinearLayout) requestItems.getChildAt(innerIndex);
                        LinearLayout amountLayout = (LinearLayout) parent.getChildAt(1);
                        final EditText inputAmount = (EditText) amountLayout.getChildAt(1);


                        String currentText = inputAmount.getText().toString();
                        if (currentText.equals("")) {
                            currentText = "0";
                        }
                        double currentValue = Double.parseDouble(currentText);
                        totalAmount += currentValue;

                    }
                    inputTotalAmount.setText(totalAmount + "");

                }
            });


        }
    }

    private void sendRequest() {

        this.btnSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    progressBar.setVisibility(View.VISIBLE);
                    btnSendRequest.setVisibility(View.GONE);

                    RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());
                    final Employee employee = SharedPreference.getEmployee(getBaseContext());
                    String url = Api.CREATE_FINANCIAL_REQUEST;

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            progressBar.setVisibility(View.GONE);

                            try {
                                JSONObject responseObject = new JSONObject(response);
                                int responseType = responseObject.getInt("type");
                                String message = responseObject.getString("message");

                                if (responseType == Api.RESPONSE_TYPE_SUCCESS) {
                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getBaseContext(), FinancialRequestActivity.class);
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
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(requestItems,"Network or Server Error",Snackbar.LENGTH_LONG).show();
                            //Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            btnSendRequest.setVisibility(View.VISIBLE);
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();


                            params.put("amount", inputTotalAmount.getText().toString());
                            params.put("remarks", inputRemarks.getText().toString());
                            params.put("requested_by", employee.getId() + "");

                            ArrayList<FinancialRequestItem> items = new ArrayList<>();

                            for (int index = 0; index < requestItems.getChildCount(); index++) {
                                LinearLayout parentLayout = (LinearLayout)requestItems.getChildAt(index);

                                LinearLayout descriptionLayout = (LinearLayout)parentLayout.getChildAt(0);
                                LinearLayout amountLayout = (LinearLayout)parentLayout.getChildAt(1);

                                EditText inputDescription = (EditText)descriptionLayout.getChildAt(1);
                                EditText inputAmount = (EditText)amountLayout.getChildAt(1);

                                FinancialRequestItem requestItem = new FinancialRequestItem();
                                requestItem.setDescription(inputDescription.getText().toString());
                                requestItem.setAmount(Double.parseDouble(inputAmount.getText().toString()));

                                items.add(requestItem);

                            }


                            Gson gson = new Gson();
                            String serialItems = gson.toJson(items);
                            params.put("items", serialItems);

                            return params;

                        }
                    };

                    requestQueue.add(stringRequest);

                }
            }
        });

    }


    public void removeItem(View view) {
        RelativeLayout parent1 = (RelativeLayout) view.getParent();
        LinearLayout parent2 = (LinearLayout) parent1.getParent();
        LinearLayout parent3 = (LinearLayout) parent2.getParent();
        requestItems.removeView(parent3);
        requestItemsCount.setText("Request Items (" + requestItems.getChildCount() + ")");

        double totalAmount = 0;
        //Update Total Amount
        for (int index = 0; index < requestItems.getChildCount(); index++) {

            totalAmount = 0;

            for (int innerIndex = 0; innerIndex < requestItems.getChildCount(); innerIndex++) {

                LinearLayout innerParent = (LinearLayout) requestItems.getChildAt(innerIndex);
                LinearLayout innerAmountLayout = (LinearLayout) innerParent.getChildAt(1);
                final EditText innerInputAmount = (EditText) innerAmountLayout.getChildAt(1);


                String currentText = innerInputAmount.getText().toString();
                if (currentText.equals("")) {
                    currentText = "0";
                }
                double currentValue = Double.parseDouble(currentText);
                totalAmount += currentValue;

            }
            inputTotalAmount.setText(totalAmount + "");
        }
        inputTotalAmount.setText(totalAmount + "");

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
