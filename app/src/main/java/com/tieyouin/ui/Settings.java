package com.tieyouin.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tieyouin.R;
import com.tieyouin.custom.CustomFragment;

/**
 * The Class Settings is the Fragment class that shows a few options related to
 * Settings and it's launched when user taps on Settings option in left
 * navigation panel.
 */
public class Settings extends CustomFragment
{

	/** The current view for theme options. */
	private View current;

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.setting, null);

		View g = setTouchNClick(v.findViewById(R.id.themeGray));
		View b = setTouchNClick(v.findViewById(R.id.themeBlue));
		View w = setTouchNClick(v.findViewById(R.id.themeWhite));

		int theme = getAppTheme();
		if (theme == THEME_GRAY)
		{
			g.setEnabled(false);
			current = g;
		}
		else if (theme == THEME_WHITE)
		{
			w.setEnabled(false);
			current = w;
		}
		else
		{
			b.setEnabled(false);
			current = b;
		}
		return v;
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);

		if (v.getId() == R.id.themeGray || v.getId() == R.id.themeWhite
				|| v.getId() == R.id.themeBlue)
		{
			current.setEnabled(true);
			current = v;
			current.setEnabled(false);

			int theme;
			if (v.getId() == R.id.themeGray)
				theme = THEME_GRAY;
			else if (v.getId() == R.id.themeWhite)
				theme = THEME_WHITE;
			else
				theme = THEME_RED;
			saveAppTheme(theme);
		}
	}
}
