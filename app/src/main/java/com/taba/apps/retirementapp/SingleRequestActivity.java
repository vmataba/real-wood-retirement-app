package com.taba.apps.retirementapp;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.taba.apps.retirementapp.api.Api;
import com.taba.apps.retirementapp.finance.Retirement;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.financial_request.Remark;
import com.taba.apps.retirementapp.financial_request.RemarkAdapter;
import com.taba.apps.retirementapp.financial_request.RequestItemsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

public class SingleRequestActivity extends AppCompatActivity {

    private TextView requestAmount;
    private TextView requestStatus;
    private TextView requestRemarks;
    private TextView statusDate;
    private TextView requestItems;
    private TextView offerAttachment;
    private ImageButton btnReply;
    private ProgressBar progressBar;

    private RecyclerView remarksRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RemarkAdapter adapter;
    private ArrayList<Remark> remarks;
    private FinancialRequest request;

    private BroadcastReceiver networkChangeReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_request);

        getSupportActionBar().setTitle(Html.fromHtml("<small>Cash Requisition</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.init();

        this.renderRequest();

        this.renderItems();

        this.renderRemarks();

        this.handleReply();

        this.getOfferAttachment();

    }

    private void init() {

        remarksRecyclerView = findViewById(R.id.requestRemarksRecyclerView);

        request = (FinancialRequest) getIntent().getSerializableExtra("request");

        this.requestAmount = findViewById(R.id.requestAmount);
        this.requestStatus = findViewById(R.id.requestStatus);
        this.requestRemarks = findViewById(R.id.requestRemarks);
        this.statusDate = findViewById(R.id.statusDate);
        this.requestItems = findViewById(R.id.requestItems);
        this.offerAttachment = findViewById(R.id.offerDocument);
        this.btnReply = findViewById(R.id.btnReply);
        this.progressBar = findViewById(R.id.progressBar);

    }


    private void getOfferAttachment() {
        if (!request.isOffered()) {
            this.offerAttachment.setVisibility(View.INVISIBLE);
            return;
        }


        this.offerAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SingleRequestActivity.this, FilePreviewActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);

            }
        });

    }


    private void renderRequest() {

        this.requestAmount.setText(request.getTextAmount() + "/=");
        this.requestStatus.setText(request.getNamedStatus());
        this.requestStatus.setTextColor(getResources().getColor(request.getStatusColor()));
        this.requestRemarks.setText(request.getRequestRemarks());
        this.statusDate.setText(request.getUpdatedAt());

        if (request.isApproved()) {
            this.requestAmount.setText(request.getTextIssuedAmount());
            this.requestAmount.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_done), null, null, null);
        }

        if (request.isOffered()) {
            this.requestAmount.setText(request.getTextIssuedAmount());
            this.requestAmount.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_done_all), null, null, null);
        }

        if (request.isRetired()) {
            this.requestAmount.setText(request.getTextIssuedAmount());
            this.requestAmount.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_monetization_green), null, null, null);

        }

    }

    private void renderItems() {
        this.requestItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String[] items = {
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        ""
                };


                RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());


                String url = Api.FINANCIAL_REQUEST_ITEMS + "&id=" + request.getId();

                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject responseObject = new JSONObject(response);

                            int responseType = responseObject.getInt("type");
                            String message = responseObject.getString("message");
                            if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                                JSONArray itemsCollection = responseObject.getJSONArray("items");
                                ;
                                //For each remark do the needful
                                for (int index = 0; index < itemsCollection.length(); index++) {

                                    JSONObject currentItem = itemsCollection.getJSONObject(index);

                                    String description = currentItem.getString("description");
                                    String issuedAmount = currentItem.getString("issued_amount");

                                    String requestText = description + " : " + issuedAmount;

                                    items[index] = requestText;


                                }


                                AlertDialog.Builder builder = new AlertDialog.Builder(SingleRequestActivity.this);

                                // builder.setIcon(getResources().getDrawable(R.drawable.ic_view_list));

                                builder.setTitle(Html.fromHtml("<small>Requested Items</small>"));

                                builder.setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });


                                builder.setNegativeButton(Html.fromHtml("<small>OK</small>"), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Cancel Dialog
                                    }
                                });


                                builder.create().show();


                            } else {
                                //Temporary
                                Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {

                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

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

                        return null;
                    }
                };

                requestQueue.add(stringRequest);


            }
        });
    }


    private void updateCurrentRequest(){



        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());


        String url = Api.VIEW_FINANCIAL_REQUEST + "&id=" + request.getId();



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //Toast.makeText(getBaseContext(),response,Toast.LENGTH_LONG).show();

                try {
                    JSONObject responseObject = new JSONObject(response);



                    int responseType = responseObject.getInt("type");
                    if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                        JSONObject currentRequest = responseObject.getJSONObject("request");


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



                       refresh();


                    } else {
                        //Temporary
                        // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {

                    //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                 Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                //Snackbar.make(remarksRecyclerView, "Network or Server Errors", Snackbar.LENGTH_LONG).show();
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

    private void renderRemarks() {

        RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());


        String url = Api.FINANCIAL_REQUEST_REMARKS + "&id=" + request.getId();

        progressBar.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                remarks = new ArrayList<>();
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject responseObject = new JSONObject(response);

                    int responseType = responseObject.getInt("type");
                    String message = responseObject.getString("message");
                    if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                        JSONArray remarksCollection = responseObject.getJSONArray("remarks");
                        ;
                        //For each remark do the needful
                        for (int index = 0; index < remarksCollection.length(); index++) {

                            JSONObject currentRemark = remarksCollection.getJSONObject(index);

                            Remark remark = new Remark();
                            remark.setUserFullName(currentRemark.getString("userFullName"));
                            remark.setUserTitle(currentRemark.getString("userTitle"));
                            remark.setUserPhoto(currentRemark.getString("userPhoto"));
                            remark.setRemark(currentRemark.getString("remark"));
                            remark.setCreatedAt(currentRemark.getString("createdAt"));
                            remark.setStatus(currentRemark.getInt("status"));
                            //Other more will be added here

                            //Add request to array list
                            remarks.add(remark);
                        }

                        adapter = new RemarkAdapter(getBaseContext(), remarks);

                        layoutManager = new LinearLayoutManager(getBaseContext());
                        remarksRecyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext(), LinearLayout.VERTICAL));
                        remarksRecyclerView.setLayoutManager(layoutManager);
                        remarksRecyclerView.setAdapter(adapter);


                    } else {
                        //Temporary
                        // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {

                    //Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();

                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                // Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                Snackbar.make(remarksRecyclerView, "Network or Server Errors", Snackbar.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                return null;
            }
        };

        requestQueue.add(stringRequest);


    }

    private void handleReply() {

        if (request.getStatus() != FinancialRequest.STATUS_INPROGRESS) {
            this.btnReply.setVisibility(View.GONE);
        }


        this.btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingleRequestActivity.this, ReplyActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = this.getMenuInflater();

        if (request.isOffered()) {
            inflater.inflate(R.menu.menu_single_request_retirement, menu);
        } else {
            inflater.inflate(R.menu.menu_single_request, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:

                onBackPressed();
                finish();

                break;
            case R.id.menuRefresh://Refresh

                updateCurrentRequest();
                this.refresh();

                break;

            case R.id.menuRetirement://Retirement

                Intent intent = new Intent(SingleRequestActivity.this, RetirementActivity.class);
                intent.putExtra("request", request);
                startActivity(intent);


                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // this.refresh();
    }

    private void refresh() {
        setContentView(R.layout.activity_single_request);

        this.init();

        //this.updateCurrentRequest();

        this.renderRequest();

        this.renderItems();

        this.renderRemarks();

        this.handleReply();

    }

    @Override
    protected void onStart() {

        networkChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateCurrentRequest();
                refresh();
            }
        };
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);

        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReceiver);
    }
}
