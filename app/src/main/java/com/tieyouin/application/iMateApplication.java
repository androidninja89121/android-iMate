package com.tieyouin.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.tieyouin.utils.LruBitmapCache;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Morning on 11/24/2015.
 */
public class iMateApplication extends Application {

    public static final String TAG = iMateApplication.class.getSimpleName();

    public RequestQueue _requestQueue;
    private ImageLoader _imageLoader;

    private static iMateApplication _instance;

    @Override
    public void onCreate() {

        super.onCreate();
        _instance = this;

        FacebookSdk.sdkInitialize(getApplicationContext());

        getUserInfo();
    }

    /**
     *  return application instance
     * @return
     */
    public static synchronized iMateApplication getInstance() {
        return _instance;
    }

    public RequestQueue getRequestQueue() {

        if (_requestQueue == null) {
            _requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return _requestQueue;
    }

    public ImageLoader getImageLoader() {

        getRequestQueue();
        if (_imageLoader == null) {
            _imageLoader = new ImageLoader(this._requestQueue,
                    new LruBitmapCache());
        }
        return this._imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if(_requestQueue != null) {
            _requestQueue.cancelAll(tag);
        }
    }

    // get sha1 and then print logo
    public void getUserInfo(){
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

}
