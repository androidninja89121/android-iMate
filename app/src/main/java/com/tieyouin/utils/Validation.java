package com.tieyouin.utils;

import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Morning on 11/24/2015.
 */
public class Validation {

    public final static boolean isValidEmail(CharSequence target) {

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public final static boolean isValidPhoneNumber(CharSequence target){
        return Patterns.PHONE.matcher(target).matches();
    }

    public final static boolean isUserNameValid(String str) {
        //TODO: Replace this with your own logic
        if(str.length() < 3 || str.contains(" ")) {
            return false;
        }
        return true;
    }

    public final static boolean isPasswordValid(String str) {

        return str.length() > 4;
    }

    public final static boolean isValidOld(String _old) {

        String year = _old.substring(6,10);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String now = df.format(new Date());
        String nowYear = now.substring(0, 4);

        int age = Integer.parseInt(nowYear) - Integer.parseInt(year);

        if(age < 13) return false;

        return true;
    }

//    public static int getElapsedTime(final String _time) {
//        Calendar calendar = Calendar.getInstance();
//        DateFormat stringFormat = new SimpleDateFormat("yyyy-MM-dd");
//
//        long olderTime = 0;
//        // change string to date
//        try {
//            Date tempDate = stringFormat.parse(_time);
//            calendar.setTime(tempDate);
//            olderTime = calendar.getTimeInMillis();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        // current time
//        long currentTime = System.currentTimeMillis();
//
//        // elapsed time
//        long elapsedTime = currentTime - olderTime;
//
//        Date formateTime = new Date(elapsedTime);
//        calendar.setTime(formateTime);
//
//        int year = calendar.get(Calendar.YEAR);
//
//        return year;
//    }
}
