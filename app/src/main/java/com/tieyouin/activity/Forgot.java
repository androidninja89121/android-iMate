package com.tieyouin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.R;
import com.tieyouin.application.iMateApplication;
import com.tieyouin.custom.CustomActivity;
import com.tieyouin.utils.Constant;
import com.tieyouin.utils.ReqConst;
import com.tieyouin.utils.Validation;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * The class Login is an Activity class that is launched after Splash screen at
 * shows the interface for Login to the app with the options for Login using
 * Facebook and Twitter and also includes the options for Register and recover
 * password.
 */
public class Forgot extends CustomActivity
{

    private EditText ui_edit_username;

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

		setContentView(R.layout.activity_forgot);

		loadLayout();
	}

    private void loadLayout() {

        ui_edit_username = (EditText) findViewById(R.id.user_email);

        setTouchNClick(R.id.lblReg);
        setTouchNClick(R.id.lblLogin);

        setTouchNClick(R.id.btnSend);
    }

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);

		switch (v.getId()) {

			case R.id.btnSend:
                attemptSend();
                break;

            case R.id.lblLogin:
                gotoLoginActivity();
                break;

            case R.id.lblReg:
                gotoRegisterActivity();
                break;
		}
	}

    @Override
    protected void onResume() {
        ui_edit_username.setText(UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_USERNAME, ""));
        super.onResume();
    }

    private void attemptSend() {

        boolean cancel = false;

        ui_edit_username.setError(null);

        String _username = ui_edit_username.getText().toString();

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

        if(cancel) {

            focusView.requestFocus();

        } else {
            doSend(_username);
        }
	}

    // perform login
    private void doSend(final String _user) {

        showProgress();

        String url = ReqConst.SERVER_URL + ReqConst.REQ_TOKEN;

        StringRequest strReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String json) {
                        // TODO Auto-generated method stub
                        parseSendResponse(json);
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
                params.put(ReqConst.GRANT_TYPE, ReqConst.GRANT);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(Constant.VOLLEY_TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        iMateApplication.getInstance().addToRequestQueue(strReq, url);
    }

    private void parseSendResponse(String str) {

        Log.d("Response", "-----------" + str);

        try {

            JSONObject json = new JSONObject(str);
            String strID = json.getString(ReqConst.ID);
            String strClientID = json.getString(ReqConst.AS_CLIENTT_ID);
            String strUserEmail = json.getString(ReqConst.USERNAME2);           // username@domain.com
            String strUserName = json.getString(ReqConst.NAME);                 // username
            String strAccessToken = json.getString(ReqConst.ACCESS_TOKEN);
            String strRefreshToken = json.getString(ReqConst.REFRESSH_TOKEN);
            String strTokenType = json.getString(ReqConst.TOKEN_TYPE);
            String strExpiresIn = json.getString(ReqConst.EXPIRES_IN);
            String strIssued = json.getString(ReqConst.ISSUED);
            String  strExpires = json.getString(ReqConst.EXPIRES);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ID, strID);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_AS_CLIENT_ID, strClientID);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_USERNAME, strUserEmail);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_NAME, strUserName);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, strAccessToken);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_REFRESH_TOKEN, strRefreshToken);

            Log.d("refresh:", "--------------------------------" + strRefreshToken);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, strTokenType);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES_IN, strExpiresIn);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_ISSUED, strIssued);
            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_EXPIRES, strExpires);

//            String _password = ui_edit_password.getText().toString();
            // save password for checking when user log in
//            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_PASSWORD, _password);

            UserPreference.getInstance().putSharedPreference(this, PrefConst.PREF_IS_LOGIN, true);

            closeProgress();

            showToast("Success to Sending");

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

                String refresh_token = UserPreference.getInstance().getSharedPreference(Forgot.this, PrefConst.PREF_REFRESH_TOKEN, "");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoLoginActivity() {

        Intent intent = new Intent(this, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        finish();
    }

    private void gotoRegisterActivity() {

        Intent intent = new Intent(this, Register.class);
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
