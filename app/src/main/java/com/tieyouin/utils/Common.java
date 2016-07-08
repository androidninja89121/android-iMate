package com.tieyouin.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.tieyouin.Preference.PrefConst;
import com.tieyouin.Preference.UserPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Morning on 11/26/2015.
 */
public class Common {

    public static String userID = "";
    public static String userName = "";
    public static String userPassword = "";
    public static String access_token = "";
    public static String token_type = "";

    public static float user_lat = .0f;
    public static float user_lon = .0f;

    public static String user_city = "";
    public static String user_country = "";

    public static int language_id = 0;

    public static String userMacIp = "";

    public static String userGcmRegId = "";

    public static boolean isLogin = false;
    public static boolean isRegister = false;

    private static Locale myLocale;

    public static boolean isExpired(Context context) {

        long _savedTime = 0;
        long _currentTime = System.currentTimeMillis();

        String _expire = UserPreference.getInstance().getSharedPreference(context, PrefConst.PREF_EXPIRES, "");

        if(_expire.equals("")) return false;

       try {
           Date date = new SimpleDateFormat("dd m yy hh:mm:ss", Locale.ENGLISH).parse(_expire);
           _savedTime = date.getTime();

       } catch(ParseException e) {
           e.printStackTrace();
       }

        if(_savedTime <= _currentTime) return true;

        return false;
    }

    public static void changeLanguage(Context context, String lang) {
        if(lang.equalsIgnoreCase(""))
            return;

        myLocale = new Locale(lang);
        saveLocale(context, lang);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        context.getApplicationContext().getResources().updateConfiguration(config, null);
//        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    // save the current locale
    public static void saveLocale(Context context, String lang) {
        UserPreference.getInstance().putSharedPreference(context, PrefConst.PREF_LANGUAGE, lang);
    }

    public void loadLocale(Context context) {
        String language = UserPreference.getInstance().getSharedPreference(context, PrefConst.PREF_LANGUAGE, "");
        changeLanguage(context, language);
    }

    public static String getMacAddress(Context context) { // media access control

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        String macAddress = wInfo.getMacAddress();
        return macAddress;
    }

    public static boolean isGpsServiceRunning(Context context, String service_name) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(service_name.equals(service.service
            .getClassName())) {
                return true;
            }
        }

        return false;
    }
}
