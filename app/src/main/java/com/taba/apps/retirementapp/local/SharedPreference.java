package com.taba.apps.retirementapp.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.taba.apps.retirementapp.employee.Employee;

public class SharedPreference {

    public static final String FIRST_RUN = "first_run";

    public static final String SERVER_ADDRESS = "server_address";

    public static final String DEVICE_PHONE_NUMBER = "device_phone_number";

    public static final String EMPLOYEE = "employee";


    public static boolean markNonFirstRun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FIRST_RUN, 0);
        return preferences.edit().putBoolean(FIRST_RUN, false).commit();
    }

    public static boolean isFirstRun(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FIRST_RUN, 0);
        return preferences.getBoolean(FIRST_RUN, true);
    }

    public static boolean updateServerAddress(Context context, String address) {
        SharedPreferences preferences = context.getSharedPreferences(SERVER_ADDRESS, 0);
        return preferences.edit().putString(SERVER_ADDRESS, address).commit();

    }

    public static boolean updateEmployee(Context context, Employee employee) {
        Gson gson = new Gson();
        String serialEmployee = gson.toJson(employee);
        SharedPreferences preferences = context.getSharedPreferences(EMPLOYEE, 0);
        return preferences.edit().putString(EMPLOYEE, serialEmployee).commit();

    }

    public static boolean updateDevicePhoneNumber(Context context, String phone) {
        SharedPreferences preferences = context.getSharedPreferences(DEVICE_PHONE_NUMBER, 0);
        return preferences.edit().putString(DEVICE_PHONE_NUMBER, phone).commit();

    }

    public static String getServerAddress(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SERVER_ADDRESS, 0);
        return preferences.getString(SERVER_ADDRESS, "");
    }

    public static Employee getEmployee(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(EMPLOYEE, 0);
        String serialEmployee = preferences.getString(EMPLOYEE, "");
        Gson gson = new Gson();
        Employee employee = gson.fromJson(serialEmployee, Employee.class);
        return employee;
    }

    public static String getDevicePhoneNumber(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(DEVICE_PHONE_NUMBER, 0);
        return preferences.getString(DEVICE_PHONE_NUMBER, "");
    }

    public static void clearData(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(FIRST_RUN, 0);
        preferences.edit().putBoolean(FIRST_RUN, true).commit();
        preferences.edit().putString(EMPLOYEE, null).commit();
    }

}
