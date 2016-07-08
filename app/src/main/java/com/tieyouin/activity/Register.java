package com.tieyouin.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.R;
import com.tieyouin.application.iMateApplication;
import com.tieyouin.custom.CustomActivity;
import com.tieyouin.utils.Common;
import com.tieyouin.utils.Constant;
import com.tieyouin.utils.ReqConst;
import com.tieyouin.utils.Validation;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Morning on 11/24/2015.
 */

@SuppressWarnings("deprecation")
public class Register extends CustomActivity {

    private final int DATE_DIALOG = 111;

    private EditText ui_editUserName;
    private EditText ui_editPwd;
    private EditText ui_editConfirmPwd;
    private EditText ui_editEmail;
    private EditText ui_editPhone;
    private EditText ui_editBirthday;
    private RadioButton ui_radioMail;
    private RadioButton ui_radioFemail;
    private GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loadLayout();
        registerGcm();
        checkLocationEnable();
    }

    private void checkLocationEnable(){
        LocationManager locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(!isGPSEnabled) {
            showSettingAlert();
        }
    }

    private void loadLayout() {

        ui_editUserName = (EditText) findViewById(R.id.user_name);
        ui_editPwd = (EditText)findViewById(R.id.password);
        ui_editConfirmPwd = (EditText) findViewById(R.id.confirm_password);
        ui_editEmail = (EditText) findViewById(R.id.user_email);
        ui_editPhone = (EditText) findViewById(R.id.user_phone);
        ui_editBirthday = (EditText) findViewById(R.id.user_birthday);
        ui_radioMail = (RadioButton) findViewById(R.id.radio_male);
        ui_radioFemail = (RadioButton) findViewById(R.id.radio_female);

        setTouchNClick(R.id.btn_register);
        setTouchNClick(R.id.user_birthday);
        setTouchNClick(R.id.lblLogin);

        ui_editUserName.setText("Taiseer2");
        ui_editPwd.setText("SuperPass");
        ui_editConfirmPwd.setText("SuperPass");
        ui_editEmail.setText("test@yahoo.ca2");
        ui_editPhone.setText("8769876567");
        ui_editBirthday.setText("12-12-1980");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch(v.getId()) {
            case R.id.user_birthday:
                showDataPicker();
                break;

            case R.id.btn_register:
                attemptRegister();
                break;

            case R.id.lblLogin:
                gotoLoginActivity();
        }
    }

    private void attemptRegister() {

        boolean cancel = false;

        ui_editUserName.setError(null);
        ui_editPwd.setError(null);
        ui_editConfirmPwd.setError(null);
        ui_editEmail.setError(null);
        ui_editPhone.setError(null);
        ui_editBirthday.setError(null);

        // store values at the time of the login attempt
        String _username = ui_editUserName.getText().toString();
        String _password = ui_editPwd.getText().toString();
        String _confirm_password = ui_editConfirmPwd.getText().toString();
        String _email = ui_editEmail.getText().toString();
        String _phone = ui_editPhone.getText().toString();
        String _birthday = ui_editBirthday.getText().toString();

        View focusView = null;

        //check if username is longer than 3 letters
        if(TextUtils.isEmpty(_username)) {
            ui_editUserName.setError(getString(R.string.error_field_required));
            focusView = ui_editUserName;
            cancel = true;
        } else if(!Validation.isUserNameValid(_username)) {
            ui_editUserName.setError(getString(R.string.error_invalid_username));
            focusView = ui_editUserName;
            cancel = true;
        }

        // check if password is longer than 5 letters
        if(!TextUtils.isEmpty(_password) && !Validation.isPasswordValid(_password)) {
            ui_editPwd.setError(getString(R.string.error_invalid_password));
            focusView = ui_editUserName;
            cancel = true;
        }

        if(!_password.equals(_confirm_password)) {

            ui_editConfirmPwd.setError(getString(R.string.error_incorrect_password));
            focusView = ui_editConfirmPwd;
            cancel = true;
        }

        // check if mail from is correct
        if (TextUtils.isEmpty(_email)) {
            ui_editEmail.setError(getString(R.string.error_field_required));
            focusView = ui_editEmail;
            cancel = true;
        } else if (!Validation.isValidEmail(_email)) {
            ui_editEmail.setError(getString(R.string.error_invalid_email));
            focusView = ui_editEmail;
            cancel = true;
        }

        // check if phone from is correct
        if (TextUtils.isEmpty(_phone)) {
            ui_editPhone.setError(getString(R.string.error_field_required));
            focusView = ui_editPhone;
            cancel = true;
        } else if (!Validation.isValidPhoneNumber(_phone)) {
            ui_editPhone.setError(getString(R.string.error_invalid_phone));
            focusView = ui_editPhone;
            cancel = true;
        }

        //check if birthday is empty
        if(TextUtils.isEmpty(_birthday)) {
            ui_editBirthday.setError(getString(R.string.error_field_required));
            focusView = ui_editBirthday;
            cancel = true;
        } else if (!TextUtils.isEmpty(_birthday) && !Validation.isValidOld(_birthday)) {
            ui_editBirthday.setError(getString(R.string.user_old_problem));
            focusView = ui_editBirthday;
            cancel = true;
        }

        String strLat = UserPreference.getInstance().getSharedPreference(Register.this, PrefConst.PREF_USER_LAT, "");
        String strLon = UserPreference.getInstance().getSharedPreference(Register.this, PrefConst.PREF_USER_LON, "");

        if(strLat.isEmpty() || strLon.isEmpty()){
            showSettingAlert();
            return;
        }

        if(Common.userGcmRegId.isEmpty()){
            Toast.makeText(getBaseContext(), "pls wait unitl getting the GCM Registeration ID", Toast.LENGTH_LONG).show();
            registerGcm();
            return;
        }

        if(cancel) {
            focusView.requestFocus();
        } else {
            doRegister();
        }
    }

    private void registerGcm(){
        if(checkPlayServices()){
            gcm = GoogleCloudMessaging.getInstance(this);
            try {
                Common.userGcmRegId = getRegisterationId(_context);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(Common.userGcmRegId.isEmpty()){
                registerGcmInBackground();
            }else{
                Toast.makeText(getBaseContext(), "Registeration ID already exists: " + Common.userGcmRegId, Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getBaseContext(), "No valid Google Play Services APK found", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            }else{
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show();
            }

            return  false;
        }

        return true;
    }

    private String getRegisterationId(Context context) throws Exception{
        Common.userGcmRegId = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_GCM_REG_ID, "");

        if(Common.userGcmRegId.isEmpty()){
            return "";
        }

        return Common.userGcmRegId;
    }

    private void registerGcmInBackground(){
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] params) {
                String msg;
                try{
                    if(gcm == null){
                        gcm = GoogleCloudMessaging.getInstance(_context);
                    }
                    Common.userGcmRegId = gcm.register(Constant.SENDER_ID);
                    msg = "Device registered, registeration ID: " + Common.userGcmRegId;

                    UserPreference.getInstance().putSharedPreference(_context, PrefConst.PREF_GCM_REG_ID, Common.userGcmRegId);
                } catch (IOException e) {
                    e.printStackTrace();
                    msg = "Error :" + e.getMessage();
                }

                return msg;
            }
        }.execute(null, null, null);
    }

    public void showSettingAlert(){

        AlertDialog.Builder alertDialog =  new AlertDialog.Builder(this);

        alertDialog.setTitle("GPS Setting");
        alertDialog.setMessage("Would you go to GPS Setting Window ?");
        alertDialog.setPositiveButton("Setting",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void doRegister() {

        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_REGISTER;

        Log.d("url", "-----------------" + url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String json) {
                        parseResponse(json);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                        Log.d("Error", "------------" + volleyError);

                        closeProgress();

                        showToast(getString(R.string.user_exit));
//                        showAlertDialog("Fail to register");
                    }
            }) {
                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<String, String>();

                    Common.user_lat = Float.parseFloat(UserPreference.getInstance().getSharedPreference(Register.this, PrefConst.PREF_USER_LAT, "0"));
                    Common.user_lon = Float.parseFloat(UserPreference.getInstance().getSharedPreference(Register.this, PrefConst.PREF_USER_LON, "0"));

                    params.put(ReqConst.USERNAME, ui_editUserName.getText().toString());
                    params.put(ReqConst.PASSWORD, ui_editPwd.getText().toString());
                    params.put(ReqConst.CONFIRM_PASSWORD, ui_editConfirmPwd.getText().toString());
                    params.put(ReqConst.EMAIL, ui_editEmail.getText().toString());
                    params.put(ReqConst.PHONE_NUMBER, ui_editPhone.getText().toString());
                    params.put(ReqConst.ADDRESS, Common.user_city + ", " + Common.user_country);
                    params.put(ReqConst.BIRTHDAY, ui_editBirthday.getText().toString());
                    params.put(ReqConst.USER_LAT, String.valueOf(Common.user_lat));
                    params.put(ReqConst.USER_LON, String.valueOf(Common.user_lon));
                    params.put(ReqConst.USER_LANGID, String.valueOf(Common.language_id));
                    params.put(ReqConst.DEVICE_ID, Common.userGcmRegId);
                    params.put(ReqConst.Client_ID, "ngAuthApp");

                    if(ui_radioMail.isChecked()) {
                        params.put(ReqConst.SEX, "1");
                    }

                    if(ui_radioFemail.isChecked()) {
                        params.put(ReqConst.SEX, "2");
                    }

                    return params;
                }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        iMateApplication.getInstance().addToRequestQueue(stringRequest, url);
    }

    public void parseResponse(String str) {

        closeProgress();

        Log.d("Response", "-----------" + str.toString());

        try {

            JSONObject json = new JSONObject(str);

//            String strID = json.getString(ReqConst.ID);
            String strUserEmail = json.getString(ReqConst.NAME);                // username@domain.com
//            String strUserName = json.getString(ReqConst.USERNAME);             // username
            String strAccessToken = json.getString(ReqConst.ACCESS_TOKEN);
            String strRefreshToken = json.getString(ReqConst.REFRESSH_TOKEN);
            String strTokenType = json.getString(ReqConst.TOKEN_TYPE);
            String strExpiresIn = json.getString(ReqConst.EXPIRES_IN);
            String strIssued = json.getString(ReqConst.ISSUED);
            String strExpires = json.getString(ReqConst.EXPIRES);

            String _password = ui_editPwd.getText().toString();

//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ID, strID);
//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_NAME, strUserName);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, strUserEmail);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, strAccessToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_REFRESH_TOKEN, strRefreshToken); //(can not use this value)
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, strTokenType);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES_IN, strExpiresIn);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ISSUED, strIssued);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES, strExpires);

            // save password for checking when user log in
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_PASSWORD, _password);

            showToast("Success to register");

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_REGISTER, true);

            gotoMainActivity();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showDataPicker() {
        showDialog(DATE_DIALOG);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DATE_DIALOG) {

//            Calendar now = Calendar.getInstance();
//            int year = now.get(Calendar.YEAR);
//            int month = now.get(Calendar.MONTH);
//            int day = now.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(this, _dateListener, 1989, 12, 12);
        }
        return super.onCreateDialog(id);
    }

    private DatePickerDialog.OnDateSetListener _dateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
        String date = String.format("%02d-%02d-%d", arg2 + 1,  arg3, arg1); //mon, day, year
        ui_editBirthday.setText(date);
        }
    };

    private void gotoMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    private void gotoLoginActivity() {

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            gotoLoginActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
