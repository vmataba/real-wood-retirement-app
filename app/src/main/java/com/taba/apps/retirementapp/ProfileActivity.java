package com.taba.apps.retirementapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.local.SharedPreference;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profilePicture;

    private CardView financialRequests;
    private CardView retirements;

    private Employee employee;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        /*
         * Profile Picture initialization*/
        profilePicture = findViewById(R.id.profilePicture);
        /*



         * Financial Requests*/
        financialRequests = findViewById(R.id.financialRequests);
        financialRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, FinancialRequestActivity.class);
                startActivity(intent);
            }
        });
        /*
         * Retirements*/
        retirements = findViewById(R.id.retirements);
        retirements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getBaseContext(),"Development is still in progress",Toast.LENGTH_SHORT);
                Intent intent = new Intent(ProfileActivity.this, RetirementActivity.class);
                startActivity(intent);
            }
        });


        //toolbar.setBackgroundResource(getResources().get);

        setSupportActionBar(toolbar);

        //Get Stored Employee
        employee = SharedPreference.getEmployee(getBaseContext());

        //Display Employee name
        getSupportActionBar().setTitle(Html.fromHtml("<small>"+employee.getFullName()+"</small>"));
        //toolbar.setSubtitle("Employee");
        // toolbar.setTitle("Test Title");

        //Set Employee Picture
        Picasso.with(getBaseContext()).load(employee.getPhoto()).into(profilePicture);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuEditProfile:

                Intent profileUpdateIntent = new Intent(ProfileActivity.this, ProfileUpdateActivity.class);
                startActivity(profileUpdateIntent);

                break;

            case R.id.menuLogout:

                SharedPreference.clearData(getBaseContext());
                Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
                startActivity(intent);
                Toast.makeText(getBaseContext(),"Goodbye "+employee.getFullName(),Toast.LENGTH_LONG).show();
                finish();



        }

        return super.onOptionsItemSelected(item);
    }
}
