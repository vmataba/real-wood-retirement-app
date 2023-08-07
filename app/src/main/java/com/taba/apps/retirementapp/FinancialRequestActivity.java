package com.taba.apps.retirementapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.extra.Device;
import com.taba.apps.retirementapp.extra.NetworkChangeReceiver;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.financial_request.FinancialRequestAdapter;
import com.taba.apps.retirementapp.local.SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FinancialRequestActivity extends AppCompatActivity {

    private RecyclerView financialRequestRecyclerView;
    private FinancialRequestAdapter adapter;
    private ArrayList<FinancialRequest> requests;
    private RelativeLayout.LayoutParams layoutParams;
    RecyclerView.LayoutManager layoutManager;
    private ImageButton btnNewRequest;
    private ProgressBar progressBar;
    //Receivers
    private BroadcastReceiver netWorkChangeReceiver;
    private BroadcastReceiver phoneStateChangedReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_request);

        this.getSupportActionBar().setTitle(Html.fromHtml("<small>My Cash Requisitions</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        /*Variables Initialization*/
        this.init();


        /*Load Requests From Server*/
        this.fetchRequests();

        /*New Financial Request*/
        this.issueNewRequest();


    }


    private void init() {
        this.financialRequestRecyclerView = findViewById(R.id.financialRequestsRecyclerView);
        this.btnNewRequest = findViewById(R.id.btnNewRequest);
        this.progressBar = findViewById(R.id.progressBar);
    }

    private void issueNewRequest() {
        this.btnNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FinancialRequestActivity.this, NewRequestActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_financial_request, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home://Go back home
                finish();
                onBackPressed();
                break;
            case R.id.menuRefresh://Refresh page
                this.refresh();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void refresh() {
        setContentView(R.layout.activity_financial_request);

        this.init();
        this.issueNewRequest();
        this.fetchRequests();

    }


    private void fetchRequests() {
        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());


        Employee employee = SharedPreference.getEmployee(getBaseContext());

        String url = Api.FINANCIAL_REQUESTS + "&employeeId=" + employee.getId();


        this.progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("REQUESTS RESPONSE",response);
                requests = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject responseObject = new JSONObject(response);


                    int responseType = responseObject.getInt("type");
                    String message = responseObject.getString("message");
                    if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                        JSONArray requestsCollection = responseObject.getJSONArray("requests");


                        //For each request do the needful
                        for (int index = 0; index < requestsCollection.length(); index++) {

                            JSONObject currentRequest = requestsCollection.getJSONObject(index);


                            FinancialRequest request = new FinancialRequest();
                            request.setId(currentRequest.getInt("id"));
                            request.setAmount(currentRequest.getDouble("amount"));
                            request.setTextAmount(currentRequest.getString("text_amount"));
                            request.setRequestRemarks(currentRequest.getString("remarks"));
                            request.setStatus(currentRequest.getInt("status"));
                            //Other more will be added here
                            request.setIssuedAmount(currentRequest.getDouble("issued_amount"));
                            request.setTextIssuedAmount(currentRequest.getString("text_issued_amount"));
                            request.setApproved(request.getStatus() == FinancialRequest.STATUS_APPROVED);
                            request.setOffered(request.getStatus() == FinancialRequest.STATUS_OFFERED);
                            request.setRetired(request.getStatus() == FinancialRequest.STATUS_RETIRED);
                            request.setUpdatedAt(currentRequest.getString("last_updated"));
                            request.setOfferAttachment(currentRequest.getString("offer_attachment"));
                            request.setPendingAmount(currentRequest.getDouble("pending_amount"));
                            request.setTextPendingAmount(currentRequest.getString("text_pending_amount"));
                            request.setRetiredAmount(currentRequest.getDouble("retired_amount"));
                            request.setTextRetiredAmount(currentRequest.getString("text_retired_amount"));
                            //Add request to array list
                            requests.add(request);
                        }



                        //View Financial requests list
                        adapter = new FinancialRequestAdapter(getBaseContext(), requests);
                        layoutManager = new LinearLayoutManager(getBaseContext());
                        financialRequestRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), LinearLayout.VERTICAL));
                        financialRequestRecyclerView.setLayoutManager(layoutManager);
                        financialRequestRecyclerView.setAdapter(adapter);


                    } else {
                        //Temporary
                        // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {

                    //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

                //Set number of requests to actionbar
                getSupportActionBar().setSubtitle(requests.size() + " Requests");

            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Log.d("REQUESTS ERROR",new String(error.networkResponse.data));
                // Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                if (!Device.hasInternet(getBaseContext())) {
                    Snackbar.make(financialRequestRecyclerView, "No Internet connection", Snackbar.LENGTH_LONG).show();
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return null;
            }
        };

        stringRequest.setShouldCache(false);
        requestQueue.getCache().clear();
        requestQueue.add(stringRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //this.refresh();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //Network state changed
        netWorkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                refresh();
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.registerReceiver(netWorkChangeReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(netWorkChangeReceiver);
    }
}
