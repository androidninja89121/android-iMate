package com.tieyouin.gcm;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.tieyouin.utils.Constant;

/**
 * Created by Administrator on 4/12/2016.
 */
public class GcmIntentService extends GCMBaseIntentService {

    public GcmIntentService() {
        super(Constant.SENDER_ID);
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Toast.makeText(context, "Message has been received", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onError(Context context, String s) {
        Toast.makeText(context, "Error has been received, content is " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRegistered(Context context, String s) {
        Toast.makeText(context, "Device has been registered, register id is " + s, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onUnregistered(Context context, String s) {
        Toast.makeText(context, "Device has been unregistered, register id is " + s, Toast.LENGTH_LONG).show();
    }
}
