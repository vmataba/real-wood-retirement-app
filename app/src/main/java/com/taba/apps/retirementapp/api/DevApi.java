package com.taba.apps.retirementapp.api;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.taba.apps.retirementapp.LoginActivity;
import com.taba.apps.retirementapp.ProfileActivity;
import com.taba.apps.retirementapp.employee.Employee;
import com.taba.apps.retirementapp.extra.Tool;
import com.taba.apps.retirementapp.local.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DevApi {

    public static final int RESPONSE_TYPE_SUCCESS = 1;


    public static final int RESPONSE_TYPE_ERROR = 0;

    //public static final String SERVER_ADDRESS = "192.168.8.100/realwood-retirement/web/ine";


    public static final String PROTOCOL = "http";

    public static final String SERVER_ADDRESS = PROTOCOL + "://192.168.8.101/" + "realwood-retirement/web/index.php?r=";

    public static final String BASE_URL = PROTOCOL + "://192.168.8.101/" + "realwood-retirement/web/";

    public static final String LOGIN_URL = SERVER_ADDRESS + "api/auth/login";

    public static final String ACCOUNT_SYNC_URL = SERVER_ADDRESS + "api/auth/sync&key=" + Tool.getCurrentTimeStamp();

    public static final String FINANCIAL_REQUESTS = SERVER_ADDRESS + "api/financial-request/index&key=" + Tool.getCurrentTimeStamp();

    public static final String VIEW_FINANCIAL_REQUEST = SERVER_ADDRESS + "api/financial-request/view&key=" + Tool.getCurrentTimeStamp();

    public static final String CREATE_FINANCIAL_REQUEST = SERVER_ADDRESS + "api/financial-request/create";

    public static final String FINANCIAL_REQUEST_REMARKS = SERVER_ADDRESS + "api/financial-request/remarks&key=" + Tool.getCurrentTimeStamp();

    public static final String FINANCIAL_REQUEST_ADD_REMARK = SERVER_ADDRESS + "api/financial-request/add-remark";

    public static final String FINANCIAL_REQUEST_ITEMS = SERVER_ADDRESS + "api/financial-request/items&key=" + Tool.getCurrentTimeStamp();

    public static final String RETIREMENT_ADD = SERVER_ADDRESS + "api/retirement/retire";

    public static final String PROFILE_UPDATE = SERVER_ADDRESS + "api/auth/update";

    public static final String SECURITY_KEY = "";

    public static void login(final Context context, final String username, final String password, final EditText passwordInput, final LoginActivity activity, final ProgressBar progressBar) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = LOGIN_URL;

        progressBar.setVisibility(View.VISIBLE);

        StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(response);

                    int responseType = object.getInt("type");
                    String message = object.getString("message");

                    if (responseType == 1) {

                        JSONObject employeeObject = object.getJSONObject("employee");

                        Employee employee = new Employee();
                        employee.setId(employeeObject.getInt("id"));
                        employee.setSalutation(employeeObject.getString("text_salutation"));
                        employee.setFirstName(employeeObject.getString("first_name"));
                        employee.setMiddleName(employeeObject.getString("middle_name"));
                        employee.setLastName(employeeObject.getString("last_name"));
                        employee.setPhone(employeeObject.getString("phone"));
                        employee.setEmail(employeeObject.getString("email"));
                        employee.setPassword(password);
                        employee.setPhoto(employeeObject.getString("photo"));

                        if (SharedPreference.updateEmployee(context, employee)) {
                            Toast.makeText(context, "Welcome " + employee.getFullName(), Toast.LENGTH_LONG).show();
                            SharedPreference.markNonFirstRun(context);
                            Intent intent = new Intent(context, ProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.finish();
                        }


                    } else {
                        passwordInput.setError(message);
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                //Snackbar.make(progressBar, "Network or Server Error", Snackbar.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("username", username);
                params.put("password", password);


                return params;
            }
        };

        queue.add(loginRequest);

    }


    public static void syncAccountDetails(final Context context) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = ACCOUNT_SYNC_URL;

        StringRequest loginRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject object = new JSONObject(response);

                    int responseType = object.getInt("type");

                    if (responseType == 1) {

                        JSONObject employeeObject = object.getJSONObject("employee");

                        Employee employee = new Employee();
                        employee.setId(employeeObject.getInt("id"));
                        employee.setSalutation(employeeObject.getString("text_salutation"));
                        employee.setFirstName(employeeObject.getString("first_name"));
                        employee.setMiddleName(employeeObject.getString("middle_name"));
                        employee.setLastName(employeeObject.getString("last_name"));
                        employee.setPhone(employeeObject.getString("phone"));
                        employee.setEmail(employeeObject.getString("email"));
                        employee.setPhoto(employeeObject.getString("photo"));

                        SharedPreference.updateEmployee(context, employee);

                    }

                } catch (JSONException e) {
                    // Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "Server or Network Errors", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                params.put("username", SharedPreference.getEmployee(context).getEmail());

                return params;
            }
        };

        queue.add(loginRequest);

    }

}
