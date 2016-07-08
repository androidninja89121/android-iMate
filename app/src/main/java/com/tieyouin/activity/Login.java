package com.tieyouin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.R;
import com.tieyouin.application.iMateApplication;
import com.tieyouin.custom.CustomActivity;
import com.tieyouin.utils.Common;
import com.tieyouin.utils.Constant;
import com.tieyouin.utils.ReqConst;
import com.tieyouin.utils.Validation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * The class Login is an Activity class that is launched after Splash screen at
 * shows the interface for Login to the app with the options for Login using
 * Facebook and Twitter and also includes the options for Register and recover
 * password.
 */
public class Login extends CustomActivity
{

    private EditText ui_edit_username, ui_edit_password;
    private CheckBox ui_check_refresh;
    private Button fbButton, twButton;

    /*
    creating facebook CallBackManager variable
     */
    public static CallbackManager callbackManager;

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		loadLayout();
	}

    private void loadLayout() {

        ui_edit_username = (EditText) findViewById(R.id.user_email);
        ui_edit_password = (EditText) findViewById(R.id.user_password);

//        ui_edit_username.setText(UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_USERNAME, ""));
//        ui_edit_password.setText(UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_PASSWORD, ""));

//        ui_check_refresh = (CheckBox) findViewById(R.id.check_refresh_token);

        TextView lbl = (TextView) setTouchNClick(R.id.lblReg);
//		lbl.setText(Html.fromHtml(lbl.getText().toString()));

        fbButton = (Button) findViewById(R.id.btnFb);
//        twButton = (Button) findViewById(R.id.btnTw);

        setTouchNClick(R.id.btnLogin);
        setTouchNClick(R.id.btnFb);
        setTouchNClick(R.id.lblForgot);
    }

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);

		switch (v.getId()) {

			case R.id.lblReg:
                gotoRegisterActivity();
				break;

			case R.id.btnLogin:
//                attemptLogin();
                gotoMainActivity();
				break;

			case R.id.btnFb:
                doFbLogin();
//                doTempFbLogin();
				break;

			case R.id.lblForgot:
                gotoForgotActivity();
				break;
		}
	}

    @Override
    protected void onResume() {
        ui_edit_username.setText(UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_USERNAME, ""));
        super.onResume();
    }

    private void attemptLogin() {

        boolean cancel = false;

        ui_edit_username.setError(null);
        ui_edit_password.setError(null);

        String _username = ui_edit_username.getText().toString();
        String _password = ui_edit_password.getText().toString();

        View focusView = null;

        //check if username is longer than 3 letters
        if(TextUtils.isEmpty(_username)) {
            ui_edit_username.setError(getString(R.string.error_field_required));
            focusView = ui_edit_username;
            cancel = true;
        } else if(!Validation.isUserNameValid(_username)) {
            ui_edit_username.setError(getString(R.string.error_invalid_username));
            focusView = ui_edit_username;
            cancel = true;
        }

        // check if password is longer than 5 letters
        if(!TextUtils.isEmpty(_password) && !Validation.isPasswordValid(_password)) {
            ui_edit_password.setError(getString(R.string.error_invalid_password));
            focusView = ui_edit_password;
            cancel = true;
        } else if(!_password.equals(Common.userPassword) && !Common.userPassword.equals("")) {

            ui_edit_password.setError(getString(R.string.error_incorrect_password));
            focusView = ui_edit_password;
            cancel = true;
        }

        if(cancel) {

            focusView.requestFocus();

        } else {
//            if(UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_IS_REGISTER, false)) {
//
//                if(ui_check_refresh.isChecked()) {
//
//                    if(iMateApplication.gIsLoggedIn)
//                        doRefreshToken();
//
//                } else {
                    doLogin(_username, _password);
//                }
//
//            } else {
//                showAlertDialog("Register now.");
//            }
        }
	}

    // perform login
    private void doLogin(final String _user, final String _pass) {

        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_TOKEN;

        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String json) {
                        // TODO Auto-generated method stub
                        parseLogInResponse(json);
                    }

                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError arg0) {

                        // TODO Auto-generated method stub

                        Log.d("Error", "------------" + arg0);

                        closeProgress();

                        showToast(getString(R.string.user_not_exist));

//                showAlertDialog(getString(R.string.error));
            }

        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put(ReqConst.CLIENT_ID, ReqConst.DEF_CLIENT_ID);
                params.put(ReqConst.USERNAME, _user);
                params.put(ReqConst.PASSWORD, _pass);
                params.put(ReqConst.GRANT_TYPE, ReqConst.GRANT);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        iMateApplication.getInstance().addToRequestQueue(strReq, url);
    }

    private void parseTempFBLogInResponse(String str) {

        Log.d("Response", "-----------" + str);

        try {

            JSONObject json = new JSONObject(str);
//            String strID = json.getString(ReqConst.ID);
            String strClientID = json.getString(ReqConst.AS_CLIENTT_ID);
//            String strUserEmail = json.getString(ReqConst.USERNAME2);           // username@domain.com
            String strUserName = json.getString(ReqConst.NAME);                 // username
            String strAccessToken = json.getString(ReqConst.ACCESS_TOKEN);
            String strRefreshToken = json.getString(ReqConst.REFRESSH_TOKEN);
            String strTokenType = json.getString(ReqConst.TOKEN_TYPE);
            String strExpiresIn = json.getString(ReqConst.EXPIRES_IN);
            String strIssued = json.getString(ReqConst.ISSUED);
            String  strExpires = json.getString(ReqConst.EXPIRES);

//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ID, strID);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_AS_CLIENT_ID, strClientID);
//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, strUserEmail);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_NAME, strUserName);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, strAccessToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_REFRESH_TOKEN, strRefreshToken);

            Log.d("refresh:", "--------------------------------" + strRefreshToken);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, strTokenType);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES_IN, strExpiresIn);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ISSUED, strIssued);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES, strExpires);

            String _username = ui_edit_username.getText().toString();
            String _password = ui_edit_password.getText().toString();
            // save user name and password for checking when user log in
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, _username);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_PASSWORD, _password);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_LOGIN, true);

            closeProgress();

            showToast("Success to Login");

            gotoMainActivity();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseLogInResponse(String str) {

        Log.d("Response", "-----------" + str);

        try {

            JSONObject json = new JSONObject(str);
//            String strID = json.getString(ReqConst.ID);
            String strClientID = json.getString(ReqConst.AS_CLIENTT_ID);
//            String strUserEmail = json.getString(ReqConst.USERNAME2);           // username@domain.com
            String strUserName = json.getString(ReqConst.NAME);                 // username
            String strAccessToken = json.getString(ReqConst.ACCESS_TOKEN);
            String strRefreshToken = json.getString(ReqConst.REFRESSH_TOKEN);
            String strTokenType = json.getString(ReqConst.TOKEN_TYPE);
            String strExpiresIn = json.getString(ReqConst.EXPIRES_IN);
            String strIssued = json.getString(ReqConst.ISSUED);
            String  strExpires = json.getString(ReqConst.EXPIRES);

//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ID, strID);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_AS_CLIENT_ID, strClientID);
//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, strUserEmail);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_NAME, strUserName);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, strAccessToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_REFRESH_TOKEN, strRefreshToken);

            Log.d("refresh:", "--------------------------------" + strRefreshToken);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, strTokenType);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES_IN, strExpiresIn);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ISSUED, strIssued);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES, strExpires);


            String _username = ui_edit_username.getText().toString();
            String _password = ui_edit_password.getText().toString();
            // save user name and password for checking when user log in
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, _username);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_PASSWORD, _password);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_LOGIN, true);

            closeProgress();

            showToast("Success to Login");

            gotoMainActivity();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // refresh access token
    private void doRefreshToken() {

        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_TOKEN;

        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String json) {
                        // TODO Auto-generated method stub
                        parseRefreshResponse(json);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

                // TODO Auto-generated method stub

                Log.d("Error", "------------" + arg0);

                closeProgress();

                showToast(getString(R.string.need_log_in));

//                showAlertDialog(getString(R.string.error));
            }

        }) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();

                params.put(ReqConst.CLIENT_ID, ReqConst.DEF_CLIENT_ID);
                params.put(ReqConst.GRANT_TYPE, ReqConst.REFRESSH_TOKEN);

                String refresh_token = UserPreference.getInstance().getSharedPreference(Login.this, PrefConst.PREF_REFRESH_TOKEN, "");
                params.put(ReqConst.REFRESSH_TOKEN, refresh_token);

                Log.d("send : ", "----------------------" + params.toString());

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        iMateApplication.getInstance().addToRequestQueue(strReq, url);
    }

    private void parseRefreshResponse(String str) {

        closeProgress();

        Log.d("Response", "-----------" + str);

        try {

            JSONObject json = new JSONObject(str);
            String strID = json.getString(ReqConst.ID);
            String strAccessToken = json.getString(ReqConst.ACCESS_TOKEN);
            String strRefreshToken = json.getString(ReqConst.REFRESSH_TOKEN);
            String strTokenType = json.getString(ReqConst.TOKEN_TYPE);
            String strIssued = json.getString(ReqConst.ISSUED);
            String  strExpires = json.getString(ReqConst.EXPIRES);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ID, strID);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, strAccessToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_REFRESH_TOKEN, strRefreshToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, strTokenType);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ISSUED, strIssued);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES, strExpires);

        } catch (Exception e) {
            e.printStackTrace();
        }

        showToast("Success to Refresh");
    }

    private void doTempFbLogin(){
        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_FB_LOGIN + "?Provider=facebook&ExternalAccessToken=CAAYIcmECxPwBAH4" +
                "54CAiO8X5efcOlFTFlClP8m5PM0W4mMHJJ3776dVrlOjmdW76fLOcdwCYR0zWlJ4ob161eg9CYHeIulw5FY2m2hFuVwSwNwoD2a6s9XRQ40hS6rjA1NT9MIzUzLutjeCn1KfHpyttoMwxjUnb4kS3bbSK8SXJ5iC5";

        StringRequest strReq = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String json) {
                        // TODO Auto-generated method stub
                        parseTempFBLogInResponse(json);
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError arg0) {

                // TODO Auto-generated method stub

                Log.d("Error", "------------" + arg0);

                closeProgress();

                showToast(getString(R.string.need_log_in));

//                showAlertDialog(getString(R.string.error));
            }

        });

        strReq.setRetryPolicy(new DefaultRetryPolicy(Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        iMateApplication.getInstance().addToRequestQueue(strReq, url);
    }

    private void doFbLogin() {

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(
                        "email",
                        "user_birthday",
                        "user_relationships",
                        "user_location",
                        "user_education_history",
                        "user_work_history",
                        "user_photos",
                        "user_about_me"
                ));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        System.out.println("Success");

                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            System.out.println("ERROR");
                                        } else {
                                            System.out.println("Success");

                                            try {

                                                String jsonresult = String.valueOf(json);
                                                System.out.println("JSON Result" + jsonresult);

                                                String str_id = json.getString("id");
                                                String str_name = json.getString("name");

                                                gotoMainActivityFromFb();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("FB Cancel", "On cancel");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("FB Error", error.toString());
                    }
                });
        }

//    private void doTwLogin() {
//
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoMainActivityFromFb() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void gotoMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    private void gotoRegisterActivity() {

        Intent intent = new Intent(this, Register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }

    private void gotoForgotActivity(){
        Intent intent = new Intent(this, Forgot.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }
}
