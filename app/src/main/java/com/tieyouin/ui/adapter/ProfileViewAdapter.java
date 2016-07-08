package com.tieyouin.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by Morning on 11/28/2015.
 */

@SuppressWarnings("de[recatopm")
public class ProfileViewAdapter extends PagerAdapter{

    LayoutInflater _inflater;

    public ProfileViewAdapter(LayoutInflater inflater,  Context context) {

        this._inflater = inflater;


    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }


}
