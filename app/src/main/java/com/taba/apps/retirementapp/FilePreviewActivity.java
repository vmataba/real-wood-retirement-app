package com.taba.apps.retirementapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.github.barteksc.pdfviewer.PDFView;
import com.taba.apps.retirementapp.financial_request.FinancialRequest;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class FilePreviewActivity extends AppCompatActivity {

    private PDFView pdfView;
    private FinancialRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_preview);

        this.getSupportActionBar().setTitle(Html.fromHtml("<small>Cash offer Attachment</small>"));
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.init();
        this.setWebContents();
    }


    private void init(){
        this.pdfView = findViewById(R.id.pdfView);
        this.request = (FinancialRequest)getIntent().getSerializableExtra("request");
    }

    private void setWebContents() {

        String testUrl = "https://bigcatrescue.org/edu/catinfo/Lion.pdf";

        File file = new File(testUrl);

        Uri uri = Uri.parse(testUrl);

        pdfView.fromUri(uri);
        pdfView.loadPages();


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
