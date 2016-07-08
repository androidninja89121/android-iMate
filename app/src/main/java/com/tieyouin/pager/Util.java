package com.tieyouin.pager;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * The Class Util can be used to write Utility methods to be used in Jazzy view
 * pager.
 */
public class Util
{

	/**
	 * Convert Dp to px.
	 * 
	 * @param res
	 *            the resources
	 * @param dp
	 *            the dp
	 * @return the int
	 */
	public static int dpToPx(Resources res, int dp)
	{
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}

}
