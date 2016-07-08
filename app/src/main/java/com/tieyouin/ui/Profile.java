package com.tieyouin.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tieyouin.R;
import com.tieyouin.custom.CustomFragment;

/**
 * The Class Profile is the Fragment class that shows Profile of a user which
 * includes basic details of user and a few images of user. You need to write
 * actual code for loading and displaying the user details.
 */
public class Profile extends CustomFragment
{

    private ViewPager _viewPager;

//    private List<ImageView>


	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
        loadUserImage();

		View v = inflater.inflate(R.layout.profile, null);
		return v;
	}

    private void loadUserImage() {

        // method get api/image/getmyimages?userid={userid}


    }

}






