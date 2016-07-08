package com.tieyouin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tieyouin.activity.MainActivity;
import com.tieyouin.R;
import com.tieyouin.custom.CustomFragment;

/**
 * The Class Match is the Fragment class that shows interface for a Match
 * between two users.
 */
public class Match extends CustomFragment
{

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.match, null);

		setTouchNClick(v.findViewById(R.id.btnConversation));
		setTouchNClick(v.findViewById(R.id.btnSurf));
		return v;
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnConversation)
			((MainActivity) getActivity()).launchFragment(1);
		else
			((MainActivity) getActivity()).launchFragment(0);
	}
}
