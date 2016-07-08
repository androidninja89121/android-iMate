package com.tieyouin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tieyouin.R;
import com.tieyouin.custom.CustomFragment;

/**
 * The Class FindMatch is the Fragment class that is launched when the user
 * clicks on Location option in Left navigation panel. This simply shows a dummy
 * user image. This also applies the current selected theme on the layout.
 */
public class FindMatch extends CustomFragment
{

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.find_match, null);

		View scan = setTouchNClick(v.findViewById(R.id.btnScan));
		View invite = setTouchNClick(v.findViewById(R.id.btnInvite));
		int theme = getAppTheme();
		if (theme == THEME_GRAY)
		{
			v.setBackgroundColor(getResources().getColor(R.color.gray_dk));

			invite.setBackgroundColor(getResources().getColor(
					R.color.main_color_red));

			TextView lbl = (TextView) v.findViewById(R.id.lblInvite);
			lbl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_invite,
					0, 0, 0);
			lbl.setTextColor(getResources().getColor(R.color.white));

			v.findViewById(R.id.img).setBackgroundResource(
					R.drawable.rings_gray);
		}
		else if (theme == THEME_WHITE)
		{
			v.setBackgroundColor(getResources().getColor(R.color.white));

			invite.setBackgroundColor(getResources().getColor(
					R.color.main_color_red));
			scan.setBackgroundResource(R.drawable.gray_border_rect);

			TextView lbl = (TextView) v.findViewById(R.id.lblInvite);
			lbl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_invite,
					0, 0, 0);
			lbl.setTextColor(getResources().getColor(R.color.white));

			lbl = (TextView) v.findViewById(R.id.lblScan);
			lbl.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.ic_scan_gray, 0, 0, 0);
			lbl.setTextColor(getResources().getColor(R.color.gray_dk));

			lbl = (TextView) v.findViewById(R.id.lbl1);
			lbl.setTextColor(getResources().getColor(R.color.gray_dk));

			v.findViewById(R.id.img).setBackgroundResource(
					R.drawable.rings_white);
		}
		return v;
	}

}
