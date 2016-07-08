package com.tieyouin.ui;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tieyouin.activity.MainActivity;
import com.tieyouin.R;
import com.tieyouin.custom.CustomFragment;
import com.tieyouin.pager.JazzyViewPager;
import com.tieyouin.pager.JazzyViewPager.TransitionEffect;
import com.tieyouin.pager.OutlineContainer;

/**
 * The Class MainFragment is the Fragment class which is the initial default
 * fragment for main activity screen. It shows a View pager that includes nice
 * Transition effects.
 */
public class MainFragment extends CustomFragment
{

	/** The jazzy view pager. */
	private JazzyViewPager mJazzy;

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.main_container, null);

		setupPager(v, TransitionEffect.Stack);

		setTouchNClick(v.findViewById(R.id.btnUnlikne));
		setTouchNClick(v.findViewById(R.id.btnLike));
		setTouchNClick(v.findViewById(R.id.btnInfo));
		return v;
	}

	/* (non-Javadoc)
	 * @see com.imate.custom.CustomFragment#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		if (v.getId() == R.id.btnLike)
			((MainActivity) getActivity()).launchFragment(-1);
		else if (v.getId() == R.id.btnInfo)
			((MainActivity) getActivity()).launchFragment(-2);
	}

	/**
	 * Setup view pager.
	 * 
	 * @param v
	 *            the root view
	 * @param effect
	 *            the Transition Effect
	 */
	private void setupPager(View v, TransitionEffect effect)
	{
		mJazzy = (JazzyViewPager) v.findViewById(R.id.jazzy_pager);
		mJazzy.setTransitionEffect(effect);
		mJazzy.setAdapter(new MainAdapter());
		mJazzy.setPageMargin(30);
	}

	/**
	 * The Class MainAdapter is the adapter class for View Pager that show a few
	 * dummy images. You need to write actual code for loading the images for
	 * view pager.
	 */
	private class MainAdapter extends PagerAdapter
	{

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
		 */
		@Override
		public Object instantiateItem(ViewGroup container, final int position)
		{

			int pic = R.drawable.pofile_pic1;
			if ((position - 1) % 4 == 0)
				pic = R.drawable.pofile_pic2;
			else if ((position - 2) % 4 == 0)
				pic = R.drawable.pofile_pic3;
			else if ((position - 3) % 4 == 0)
				pic = R.drawable.pofile_pic4;

			View v = LayoutInflater.from(getActivity()).inflate(
					R.layout.candidate_part, null);

			((ImageView)v.findViewById(R.id.imgvPhoto)).setBackgroundResource(pic);

			container.addView(v,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT);
			mJazzy.setObjectForPosition(v, position);
			return v;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object obj)
		{
			container.removeView(mJazzy.findViewFromObject(position));
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount()
		{
			return 20;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
		 */
		@Override
		public boolean isViewFromObject(View view, Object obj)
		{
			if (view instanceof OutlineContainer)
			{
				return ((OutlineContainer) view).getChildAt(0) == obj;
			}
			else
			{
				return view == obj;
			}
		}
	}
}
