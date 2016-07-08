package com.tieyouin.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Morning on 11/27/2015.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

         if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

//             Log.d("boot completed", "-----" + "android.intent.action.BOOT_COMPLETED");
//
//             ComponentName _cName = new ComponentName(context.getPackageName(), GpsInfo.class.getName());
//
//             ComponentName _svcName = context.startService(new Intent().setComponent(_cName));
//
//             if(_svcName == null) {
//                 Log.d("error", "-------------" + "service not auto-run");
//             } else {
//                 Log.d("error", "--------------" + "service auto-run");
//             }
         }
    }
}
