package com.tieyouin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;
import com.tieyouin.R;
import com.tieyouin.Service.GpsInfo;
import com.tieyouin.utils.Common;
import com.tieyouin.utils.Constant;

/**
 * The Class SplashScreen will launched at the start of the application. It will
 * be displayed for 3 seconds and than finished automatically and it will also
 * start the next activity of app.
 */
public class SplashScreen extends Activity
{

	/** Check if the app is running. */
	private boolean isRunning;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		if(!Common.isGpsServiceRunning(this, "com.tieyouin.Service.GpsInfo")) {
			Intent serviceIntent = new Intent(this, GpsInfo.class);
			startService(serviceIntent);
		}

		isRunning = true;

        loadUserSetting();

		startSplash();
	}

	private void loadUserSetting() {

		Common.isRegister = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_IS_REGISTER, false);
		Common.isLogin = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_IS_LOGIN, false);

        // load from preference, set as 1 for test.
        Common.language_id = 1;

        Common.userMacIp = Common.getMacAddress(this);

		// load user information fron user preference
		if(Common.isRegister || Common.isLogin) {

			Common.userID = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_ID, "");
			Common.userName = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_USERNAME, "");
			Common.userPassword = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_PASSWORD, "");
			Common.access_token = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_ACCESS_TOKEN, "");
			Common.token_type = UserPreference.getInstance().getSharedPreference(this, PrefConst.PREF_TOKEN_TYPE, "");
		}

	}

	/**
	 * Starts the count down timer for 3-seconds. It simply sleeps the thread
	 * for 3-seconds.
	 */
	private void startSplash()
	{

		new Thread(new Runnable() {
			@Override
			public void run()
			{

				try
				{
					Thread.sleep(Constant.SPLASH_TIME);

				} catch (Exception e)
				{
					e.printStackTrace();
				} finally
				{
					runOnUiThread(new Runnable() {
						@Override
						public void run()
						{
							doFinish();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * If the app is still running than this method will start the MainActivity
	 * activity and finish the Splash.
	 */
	private synchronized void doFinish()
	{

		if (isRunning)
		{
			isRunning = false;

			// check if expired
//			if()

			if(Common.isLogin || Common.isRegister) {
				gotoMainActivity();
			} else {
				gotoLoginActivity();
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK)
		{
			isRunning = false;
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void gotoMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
	}

	private void gotoLoginActivity() {
		Intent intnet = new Intent(SplashScreen.this, Login.class);
        intnet.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intnet);
		finish();
	}
}