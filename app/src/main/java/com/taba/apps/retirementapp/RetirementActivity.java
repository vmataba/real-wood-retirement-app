package com.taba.apps.retirementapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.taba.apps.retirementapp.extra.Image;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;
import com.taba.apps.retirementapp.extra.Tool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RetirementActivity extends AppCompatActivity {


    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_IMAGE_PICK = 2;

    private FinancialRequest request;
    private TextView labelOfferedAmount;
    private TextView labelRetiredAmount;
    private TextView labelPendingAmount;
    private EditText retirementDescription;
    private EditText retirementAmount;
    private ImageView receiptImage;
    private ImageButton btnCaptureReceipt;
    private ImageButton btnPickReceipt;
    private TextView sendDetails;
    private Bitmap imageBitmap;
    private ProgressBar progressBar;
    private ImageView btnUndo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retirement);

        this.getSupportActionBar().setTitle(Html.fromHtml("<small>Cash Retirement</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.init();

        this.processReceipt();

        this.handleUndoing();

         this.processRetirement();
    }


    private void init() {
        Intent intent = getIntent();
        labelOfferedAmount = findViewById(R.id.labelOfferedAmount);
        labelRetiredAmount = findViewById(R.id.labelRetiredAmount);
        labelPendingAmount = findViewById(R.id.labelPendingAmount);
        request = (FinancialRequest) intent.getSerializableExtra("request");
        receiptImage = findViewById(R.id.receiptImage);
        btnCaptureReceipt = findViewById(R.id.btnCaptureReceipt);
        btnPickReceipt = findViewById(R.id.btnPickReceipt);
        sendDetails = findViewById(R.id.sendDetails);
        retirementDescription = findViewById(R.id.retirementDescription);
        retirementAmount = findViewById(R.id.retirementAmount);
        progressBar = findViewById(R.id.progressBar);
        btnUndo = findViewById(R.id.btnUndo);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            btnUndo.setVisibility(View.VISIBLE);
            btnPickReceipt.setVisibility(View.GONE);
            btnCaptureReceipt.setVisibility(View.GONE);

            int width = receiptImage.getWidth();
            int height = receiptImage.getHeight();

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:

                    Bundle extras = data.getExtras();
                    imageBitmap = (Bitmap) extras.get("data");
                    receiptImage.setImageBitmap(imageBitmap);
                    //receiptImage.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, width, height, true));
                    break;
                case REQUEST_IMAGE_PICK:

                    Uri path = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                        receiptImage.setImageBitmap(imageBitmap);
                        //receiptImage.setImageBitmap(Bitmap.createScaledBitmap(imageBitmap, width, height, true));
                    } catch (Exception e) {
                    }


                    break;
            }

        }

    }

    private void handleUndoing() {
        btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoSelection();
            }
        });
    }

    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void pickPicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void processReceipt() {
        this.btnCaptureReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePicture();
            }
        });

        this.btnPickReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pickPicture();
            }
        });
    }

    private void undoSelection() {
        receiptImage.setImageResource(android.R.color.transparent);
        btnUndo.setVisibility(View.GONE);
        btnPickReceipt.setVisibility(View.VISIBLE);
        btnCaptureReceipt.setVisibility(View.VISIBLE);
    }

    private void processRetirement() {

       if (request != null){
           retirementAmount.setHint("Should be less than " + request.getTextPendingAmount() + "/=");

           labelOfferedAmount.setText(request.getTextIssuedAmount() + "/=");
           labelPendingAmount.setText(request.getTextPendingAmount() + "/=");
           labelRetiredAmount.setText(request.getTextRetiredAmount() + "/=");
       }


        sendDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {

                    RequestQueue requestQueue = Volley.newRequestQueue(getBaseContext());


                    String url = Api.RETIREMENT_ADD + "&financialRequestId=" + request.getId();

                    progressBar.setVisibility(View.VISIBLE);

                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressBar.setVisibility(View.GONE);


                            try {
                                JSONObject responseObject = new JSONObject(response);

                                int responseType = responseObject.getInt("type");
                                String message = responseObject.getString("message");

                                if (responseType == Api.RESPONSE_TYPE_SUCCESS) {

                                    clearScreen();

                                    Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();

                                    onBackPressed();
                                    finish();

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
                            Toast.makeText(getBaseContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                            //Snackbar.make(receiptImage, "Network or Server Errors", Snackbar.LENGTH_LONG).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {

                            Map<String, String> params = new HashMap();

                            params.put("amount", retirementAmount.getText().toString());
                            params.put("description", retirementDescription.getText().toString());
                            params.put("encodedReceipt", Tool.getString(imageBitmap));


                            return params;
                        }
                    };

                    requestQueue.add(stringRequest);


                }
            }
        });

    }

    private boolean validate() {

        String description = retirementDescription.getText().toString();
        String amountText = retirementAmount.getText().toString();


        if (TextUtils.isEmpty(description)) {
            retirementDescription.requestFocus();
            retirementDescription.setError("This field can no be blank");
            return false;
        }

        if (TextUtils.isEmpty(amountText)) {
            retirementAmount.requestFocus();
            retirementAmount.setError("This field can not be blank");
            return false;
        }

        if (receiptImage.getDrawable() == null) {
            Snackbar.make(receiptImage, "Receipt Image is required", Snackbar.LENGTH_LONG).show();
            return false;
        }

        if (!TextUtils.isEmpty(retirementAmount.getText().toString())) {
            double amount = Double.parseDouble(amountText);
            if (amount > request.getPendingAmount()) {
                retirementAmount.requestFocus();
                retirementAmount.setError("Should not exceed " + request.getTextPendingAmount() + "/=");
                return false;
            }

        }


        return true;
    }

    private void clearScreen() {
        this.retirementDescription.setText(null);
        this.retirementAmount.setText(null);
        this.receiptImage.setImageDrawable(null);
    }
}
