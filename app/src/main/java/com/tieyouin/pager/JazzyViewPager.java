package com.tieyouin.pager;

import java.util.HashMap;
import java.util.LinkedHashMap;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tieyouin.R;
import com.nineoldandroids.view.ViewHelper;

/**
 * The Class JazzyViewPager is an efficient custom ViewPager that provide a
 * total of 9 kind of different page transition effects like cube in/out, zoom
 * in/out etc. You can also customize any of the transition effect as per your
 * need.
 */
public class JazzyViewPager extends ViewPager
{

	/** The Constant TAG. */
	public static final String TAG = "JazzyViewPager";

	/** The flag for is enabled. */
	private boolean mEnabled = true;

	/** The flag for is fade enabled. */
	private boolean mFadeEnabled = false;

	/** The flag for is outline enabled. */
	private boolean mOutlineEnabled = false;

	/** The outline color. */
	public static int sOutlineColor = Color.WHITE;

	/** The current transition effect. */
	private TransitionEffect mEffect = TransitionEffect.Standard;

	/** Map of View pager items. */
	private HashMap<Integer, Object> mObjs = new LinkedHashMap<Integer, Object>();

	/** The Constant SCALE_MAX. */
	private static final float SCALE_MAX = 0.5f;

	/** The Constant ZOOM_MAX. */
	private static final float ZOOM_MAX = 0.5f;

	/** The Constant ROT_MAX. */
	private static final float ROT_MAX = 15.0f;

	/**
	 * The Enum TransitionEffect.
	 */
	public enum TransitionEffect {

		/** The Standard. */
		Standard,

		/** The Tablet. */
		Tablet,

		/** The Cube in. */
		CubeIn,

		/** The Cube out. */
		CubeOut,

		/** The Flip vertical. */
		FlipVertical,

		/** The Flip horizontal. */
		FlipHorizontal,

		/** The Stack. */
		Stack,

		/** The Zoom in. */
		ZoomIn,

		/** The Zoom out. */
		ZoomOut,

		/** The Rotate up. */
		RotateUp,

		/** The Rotate down. */
		RotateDown,

		/** The Accordion. */
		Accordion
	}

	/** The Constant API_11. */
	private static final boolean API_11;
	static
	{
		API_11 = Build.VERSION.SDK_INT >= 11;
	}

	/**
	 * Instantiates a new jazzy view pager.
	 * 
	 * @param context
	 *            the context
	 */
	public JazzyViewPager(Context context)
	{
		this(context, null);
	}

	/**
	 * Instantiates a new jazzy view pager.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	@SuppressWarnings("incomplete-switch")
	public JazzyViewPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setClipChildren(false);
		// now style everything!
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.JazzyViewPager);
		int effect = ta.getInt(R.styleable.JazzyViewPager_style, 0);
		String[] transitions = getResources().getStringArray(
				R.array.jazzy_effects);
		setTransitionEffect(TransitionEffect.valueOf(transitions[effect]));
		setFadeEnabled(ta.getBoolean(R.styleable.JazzyViewPager_fadeEnabled,
				false));
		setOutlineEnabled(ta.getBoolean(
				R.styleable.JazzyViewPager_outlineEnabled, false));
		setOutlineColor(ta.getColor(R.styleable.JazzyViewPager_outlineColor,
				Color.WHITE));
		switch (mEffect)
		{
		case Stack:
		case ZoomOut:
			setFadeEnabled(true);
		}
		ta.recycle();
	}

	/**
	 * Sets the transition effect.
	 * 
	 * @param effect
	 *            the new transition effect
	 */
	public void setTransitionEffect(TransitionEffect effect)
	{
		mEffect = effect;
		// reset();
	}

	/**
	 * Sets the paging enabled.
	 * 
	 * @param enabled
	 *            the new paging enabled
	 */
	public void setPagingEnabled(boolean enabled)
	{
		mEnabled = enabled;
	}

	/**
	 * Sets the fade enabled.
	 * 
	 * @param enabled
	 *            the new fade enabled
	 */
	public void setFadeEnabled(boolean enabled)
	{
		mFadeEnabled = enabled;
	}

	/**
	 * Gets the fade enabled.
	 * 
	 * @return the fade enabled
	 */
	public boolean getFadeEnabled()
	{
		return mFadeEnabled;
	}

	/**
	 * Sets the outline enabled.
	 * 
	 * @param enabled
	 *            the new outline enabled
	 */
	public void setOutlineEnabled(boolean enabled)
	{
		mOutlineEnabled = enabled;
		wrapWithOutlines();
	}

	/**
	 * Sets the outline color.
	 * 
	 * @param color
	 *            the new outline color
	 */
	public void setOutlineColor(int color)
	{
		sOutlineColor = color;
	}

	/**
	 * Wrap with outlines.
	 */
	private void wrapWithOutlines()
	{
		for (int i = 0; i < getChildCount(); i++)
		{
			View v = getChildAt(i);
			if (!(v instanceof OutlineContainer))
			{
				removeView(v);
				super.addView(wrapChild(v), i);
			}
		}
	}

	/**
	 * Wrap child.
	 * 
	 * @param child
	 *            the child
	 * @return the view
	 */
	private View wrapChild(View child)
	{
		if (!mOutlineEnabled || child instanceof OutlineContainer)
			return child;
		OutlineContainer out = new OutlineContainer(getContext());
		out.setLayoutParams(generateDefaultLayoutParams());
		child.setLayoutParams(new OutlineContainer.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		out.addView(child);
		return out;
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#addView(android.view.View)
	 */
	@Override
	public void addView(View child)
	{
		super.addView(wrapChild(child));
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#addView(android.view.View, int)
	 */
	@Override
	public void addView(View child, int index)
	{
		super.addView(wrapChild(child), index);
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#addView(android.view.View, android.view.ViewGroup.LayoutParams)
	 */
	public void addView(View child, LayoutParams params)
	{
		super.addView(wrapChild(child), params);
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#addView(android.view.View, int, int)
	 */
	@Override
	public void addView(View child, int width, int height)
	{
		super.addView(wrapChild(child), width, height);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#addView(android.view.View, int, android.view.ViewGroup.LayoutParams)
	 */
	public void addView(View child, int index, LayoutParams params)
	{
		super.addView(wrapChild(child), index, params);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		return mEnabled ? super.onInterceptTouchEvent(arg0) : false;
	}

	/** The state. */
	private State mState;

	/** The old page. */
	private int oldPage;

	/** The view on left side of current page. */
	private View mLeft;

	/** The view on right side of current page. */
	private View mRight;

	/** The rotation angle. */
	private float mRot;

	/** The transition value. */
	private float mTrans;

	/** The scale factor. */
	private float mScale;

	/**
	 * The Enum State.
	 */
	private enum State {

		/** The idle. */
		IDLE,

		/** The going left. */
		GOING_LEFT,

		/** The going right. */
		GOING_RIGHT
	}

	/**
	 * Log state.
	 * 
	 * @param v
	 *            the v
	 * @param title
	 *            the title
	 */
	private void logState(View v, String title)
	{
		Log.v(TAG,
				title + ": ROT (" + ViewHelper.getRotation(v) + ", "
						+ ViewHelper.getRotationX(v) + ", "
						+ ViewHelper.getRotationY(v) + "), TRANS ("
						+ ViewHelper.getTranslationX(v) + ", "
						+ ViewHelper.getTranslationY(v) + "), SCALE ("
						+ ViewHelper.getScaleX(v) + ", "
						+ ViewHelper.getScaleY(v) + "), ALPHA "
						+ ViewHelper.getAlpha(v));
	}

	/**
	 * Animate scroll.
	 * 
	 * @param position
	 *            the position
	 * @param positionOffset
	 *            the position offset
	 */
	protected void animateScroll(int position, float positionOffset)
	{
		if (mState != State.IDLE)
		{
			mRot = (float) (1 - Math.cos(2 * Math.PI * positionOffset)) / 2 * 30.0f;
			ViewHelper.setRotationY(this, mState == State.GOING_RIGHT ? mRot
					: -mRot);
			ViewHelper.setPivotX(this, getMeasuredWidth() * 0.5f);
			ViewHelper.setPivotY(this, getMeasuredHeight() * 0.5f);
		}
	}

	/**
	 * Animate tablet.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 */
	protected void animateTablet(View left, View right, float positionOffset)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mRot = 30.0f * positionOffset;
				mTrans = getOffsetXForRotation(mRot, left.getMeasuredWidth(),
						left.getMeasuredHeight());
				ViewHelper.setPivotX(left, left.getMeasuredWidth() / 2);
				ViewHelper.setPivotY(left, left.getMeasuredHeight() / 2);
				ViewHelper.setTranslationX(left, mTrans);
				ViewHelper.setRotationY(left, mRot);
				logState(left, "Left");
			}
			if (right != null)
			{
				manageLayer(right, true);
				mRot = -30.0f * (1 - positionOffset);
				mTrans = getOffsetXForRotation(mRot, right.getMeasuredWidth(),
						right.getMeasuredHeight());
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setTranslationX(right, mTrans);
				ViewHelper.setRotationY(right, mRot);
				logState(right, "Right");
			}
		}
	}

	/**
	 * Animate cube.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param in
	 *            the in
	 */
	private void animateCube(View left, View right, float positionOffset,
			boolean in)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mRot = (in ? 90.0f : -90.0f) * positionOffset;
				ViewHelper.setPivotX(left, left.getMeasuredWidth());
				ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
				ViewHelper.setRotationY(left, mRot);
			}
			if (right != null)
			{
				manageLayer(right, true);
				mRot = -(in ? 90.0f : -90.0f) * (1 - positionOffset);
				ViewHelper.setPivotX(right, 0);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setRotationY(right, mRot);
			}
		}
	}

	/**
	 * Animate accordion.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 */
	private void animateAccordion(View left, View right, float positionOffset)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				ViewHelper.setPivotX(left, left.getMeasuredWidth());
				ViewHelper.setPivotY(left, 0);
				ViewHelper.setScaleX(left, 1 - positionOffset);
			}
			if (right != null)
			{
				manageLayer(right, true);
				ViewHelper.setPivotX(right, 0);
				ViewHelper.setPivotY(right, 0);
				ViewHelper.setScaleX(right, positionOffset);
			}
		}
	}

	/**
	 * Animate zoom.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param in
	 *            the in
	 */
	private void animateZoom(View left, View right, float positionOffset,
			boolean in)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mScale = in ? ZOOM_MAX + (1 - ZOOM_MAX) * (1 - positionOffset)
						: 1 + ZOOM_MAX - ZOOM_MAX * (1 - positionOffset);
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(left, mScale);
				ViewHelper.setScaleY(left, mScale);
			}
			if (right != null)
			{
				manageLayer(right, true);
				mScale = in ? ZOOM_MAX + (1 - ZOOM_MAX) * positionOffset : 1
						+ ZOOM_MAX - ZOOM_MAX * positionOffset;
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, right.getMeasuredHeight() * 0.5f);
				ViewHelper.setScaleX(right, mScale);
				ViewHelper.setScaleY(right, mScale);
			}
		}
	}

	/**
	 * Animate rotate.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param up
	 *            the up
	 */
	private void animateRotate(View left, View right, float positionOffset,
			boolean up)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mRot = (up ? 1 : -1) * (ROT_MAX * positionOffset);
				mTrans = (up ? -1 : 1)
						* (float) (getMeasuredHeight() - getMeasuredHeight()
								* Math.cos(mRot * Math.PI / 180.0f));
				ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(left, up ? 0 : left.getMeasuredHeight());
				ViewHelper.setTranslationY(left, mTrans);
				ViewHelper.setRotation(left, mRot);
			}
			if (right != null)
			{
				manageLayer(right, true);
				mRot = (up ? 1 : -1) * (-ROT_MAX + ROT_MAX * positionOffset);
				mTrans = (up ? -1 : 1)
						* (float) (getMeasuredHeight() - getMeasuredHeight()
								* Math.cos(mRot * Math.PI / 180.0f));
				ViewHelper.setPivotX(right, right.getMeasuredWidth() * 0.5f);
				ViewHelper.setPivotY(right, up ? 0 : right.getMeasuredHeight());
				ViewHelper.setTranslationY(right, mTrans);
				ViewHelper.setRotation(right, mRot);
			}
		}
	}

	/**
	 * Animate flip horizontal.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param positionOffsetPixels
	 *            the position offset pixels
	 */
	private void animateFlipHorizontal(View left, View right,
			float positionOffset, int positionOffsetPixels)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mRot = 180.0f * positionOffset;
				if (mRot > 90.0f)
				{
					left.setVisibility(View.INVISIBLE);
				}
				else
				{
					if (left.getVisibility() == View.INVISIBLE)
						left.setVisibility(View.VISIBLE);
					mTrans = positionOffsetPixels;
					ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(left, mTrans);
					ViewHelper.setRotationY(left, mRot);
				}
			}
			if (right != null)
			{
				manageLayer(right, true);
				mRot = -180.0f * (1 - positionOffset);
				if (mRot < -90.0f)
				{
					right.setVisibility(View.INVISIBLE);
				}
				else
				{
					if (right.getVisibility() == View.INVISIBLE)
						right.setVisibility(View.VISIBLE);
					mTrans = -getWidth() - getPageMargin()
							+ positionOffsetPixels;
					ViewHelper
							.setPivotX(right, right.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(right,
							right.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(right, mTrans);
					ViewHelper.setRotationY(right, mRot);
				}
			}
		}
	}

	/**
	 * Animate flip vertical.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param positionOffsetPixels
	 *            the position offset pixels
	 */
	private void animateFlipVertical(View left, View right,
			float positionOffset, int positionOffsetPixels)
	{
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				mRot = 180.0f * positionOffset;
				if (mRot > 90.0f)
				{
					left.setVisibility(View.INVISIBLE);
				}
				else
				{
					if (left.getVisibility() == View.INVISIBLE)
						left.setVisibility(View.VISIBLE);
					mTrans = positionOffsetPixels;
					ViewHelper.setPivotX(left, left.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(left, left.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(left, mTrans);
					ViewHelper.setRotationX(left, mRot);
				}
			}
			if (right != null)
			{
				manageLayer(right, true);
				mRot = -180.0f * (1 - positionOffset);
				if (mRot < -90.0f)
				{
					right.setVisibility(View.INVISIBLE);
				}
				else
				{
					if (right.getVisibility() == View.INVISIBLE)
						right.setVisibility(View.VISIBLE);
					mTrans = -getWidth() - getPageMargin()
							+ positionOffsetPixels;
					ViewHelper
							.setPivotX(right, right.getMeasuredWidth() * 0.5f);
					ViewHelper.setPivotY(right,
							right.getMeasuredHeight() * 0.5f);
					ViewHelper.setTranslationX(right, mTrans);
					ViewHelper.setRotationX(right, mRot);
				}
			}
		}
	}

	/**
	 * Animate stack.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param positionOffsetPixels
	 *            the position offset pixels
	 * @param position
	 *            the position
	 */
	protected void animateStack(View left, View right, float positionOffset,
			int positionOffsetPixels, int position)
	{
		View v = findViewFromObject(position + 2);
		if (v != null)
			v.setVisibility(mState == State.GOING_LEFT ? View.INVISIBLE
					: View.VISIBLE);
		if (mState != State.IDLE)
		{
			if (right != null)
			{
				manageLayer(right, true);
				mScale = (1 - SCALE_MAX) * positionOffset + SCALE_MAX;
				mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
				ViewHelper.setScaleX(right, mScale);
				ViewHelper.setScaleY(right, mScale);
				ViewHelper.setTranslationX(right, mTrans);
			}
			if (left != null)
			{
				left.bringToFront();
			}
		}
	}

	/**
	 * Animate stack j.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 * @param positionOffsetPixels
	 *            the position offset pixels
	 * @param position
	 *            the position
	 */
	protected void animateStackJ(View left, View right, float positionOffset,
			int positionOffsetPixels, int position)
	{

		View v = findViewFromObject(position + 2);
		if (v != null)
			v.setVisibility(mState == State.GOING_LEFT ? View.INVISIBLE
					: View.VISIBLE);

		v = findViewFromObject(position + 1);
		if (v != null)
			v.setVisibility(View.VISIBLE);
		if (mState != State.IDLE)
		{

			if (right != null)
			{
				manageLayer(right, true);
				mScale = (1 - SCALE_MAX) * positionOffset + SCALE_MAX;
				mTrans = -getWidth() - getPageMargin() + positionOffsetPixels;
				// ViewHelper.setScaleX(right, mScale);
				// ViewHelper.setScaleY(right, mScale);
				ViewHelper.setTranslationX(right, mTrans);

			}
			if (left != null)
			{
				left.bringToFront();
				manageLayer(left, true);

				mRot = -1 * (ROT_MAX * positionOffset);
				ViewHelper.setRotation(left, mRot);
			}
		}

	}

	/**
	 * Manage layer.
	 * 
	 * @param v
	 *            the v
	 * @param enableHardware
	 *            the enable hardware
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void manageLayer(View v, boolean enableHardware)
	{
		if (!API_11)
			return;
		int layerType = enableHardware ? View.LAYER_TYPE_HARDWARE
				: View.LAYER_TYPE_NONE;
		if (layerType != v.getLayerType())
			v.setLayerType(layerType, null);
	}

	/**
	 * Disable hardware layer.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void disableHardwareLayer()
	{
		if (!API_11)
			return;
		View v;
		for (int i = 0; i < getChildCount(); i++)
		{
			v = getChildAt(i);
			if (v.getLayerType() != View.LAYER_TYPE_NONE)
				v.setLayerType(View.LAYER_TYPE_NONE, null);
		}
	}

	/** The m matrix. */
	private Matrix mMatrix = new Matrix();

	/** The m camera. */
	private Camera mCamera = new Camera();

	/** The m temp float2. */
	private float[] mTempFloat2 = new float[2];

	/**
	 * Gets the offset x for rotation.
	 * 
	 * @param degrees
	 *            the degrees
	 * @param width
	 *            the width
	 * @param height
	 *            the height
	 * @return the offset x for rotation
	 */
	protected float getOffsetXForRotation(float degrees, int width, int height)
	{
		mMatrix.reset();
		mCamera.save();
		mCamera.rotateY(Math.abs(degrees));
		mCamera.getMatrix(mMatrix);
		mCamera.restore();

		mMatrix.preTranslate(-width * 0.5f, -height * 0.5f);
		mMatrix.postTranslate(width * 0.5f, height * 0.5f);
		mTempFloat2[0] = width;
		mTempFloat2[1] = height;
		mMatrix.mapPoints(mTempFloat2);
		return (width - mTempFloat2[0]) * (degrees > 0.0f ? 1.0f : -1.0f);
	}

	/**
	 * Animate fade.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 * @param positionOffset
	 *            the position offset
	 */
	protected void animateFade(View left, View right, float positionOffset)
	{
		if (left != null)
		{
			ViewHelper.setAlpha(left, 1 - positionOffset);
		}
		if (right != null)
		{
			ViewHelper.setAlpha(right, positionOffset);
		}
	}

	/**
	 * Animate outline.
	 * 
	 * @param left
	 *            the left
	 * @param right
	 *            the right
	 */
	protected void animateOutline(View left, View right)
	{
		if (!(left instanceof OutlineContainer))
			return;
		if (mState != State.IDLE)
		{
			if (left != null)
			{
				manageLayer(left, true);
				((OutlineContainer) left).setOutlineAlpha(1.0f);
			}
			if (right != null)
			{
				manageLayer(right, true);
				((OutlineContainer) right).setOutlineAlpha(1.0f);
			}
		}
		else
		{
			if (left != null)
				((OutlineContainer) left).start();
			if (right != null)
				((OutlineContainer) right).start();
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onPageScrolled(int, float, int)
	 */
	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels)
	{
		if (mState == State.IDLE && positionOffset > 0)
		{
			oldPage = getCurrentItem();
			mState = position == oldPage ? State.GOING_RIGHT : State.GOING_LEFT;
		}
		boolean goingRight = position == oldPage;
		if (mState == State.GOING_RIGHT && !goingRight)
			mState = State.GOING_LEFT;
		else if (mState == State.GOING_LEFT && goingRight)
			mState = State.GOING_RIGHT;

		float effectOffset = isSmall(positionOffset) ? 0 : positionOffset;

		// mLeft = getChildAt(position);
		// mRight = getChildAt(position+1);
		mLeft = findViewFromObject(position);
		mRight = findViewFromObject(position + 1);

		if (mFadeEnabled)
			animateFade(mLeft, mRight, effectOffset);
		if (mOutlineEnabled)
			animateOutline(mLeft, mRight);

		switch (mEffect)
		{
		case Standard:
			break;
		case Tablet:
			animateTablet(mLeft, mRight, effectOffset);
			break;
		case CubeIn:
			animateCube(mLeft, mRight, effectOffset, true);
			break;
		case CubeOut:
			animateCube(mLeft, mRight, effectOffset, false);
			break;
		case FlipVertical:
			animateFlipVertical(mLeft, mRight, positionOffset,
					positionOffsetPixels);
			break;
		case FlipHorizontal:
			animateFlipHorizontal(mLeft, mRight, effectOffset,
					positionOffsetPixels);
		case Stack:
			// animateStack(mLeft, mRight, effectOffset, positionOffsetPixels);
			animateStackJ(mLeft, mRight, effectOffset, positionOffsetPixels,
					position);
			break;
		case ZoomIn:
			animateZoom(mLeft, mRight, effectOffset, true);
			break;
		case ZoomOut:
			animateZoom(mLeft, mRight, effectOffset, false);
			break;
		case RotateUp:
			animateRotate(mLeft, mRight, effectOffset, true);
			break;
		case RotateDown:
			animateRotate(mLeft, mRight, effectOffset, false);
			break;
		case Accordion:
			animateAccordion(mLeft, mRight, effectOffset);
			break;
		}

		super.onPageScrolled(position, positionOffset, positionOffsetPixels);

		if (effectOffset == 0)
		{
			disableHardwareLayer();
			mState = State.IDLE;
		}

	}

	/**
	 * Checks if is small.
	 * 
	 * @param positionOffset
	 *            the position offset
	 * @return true, if is small
	 */
	private boolean isSmall(float positionOffset)
	{
		return Math.abs(positionOffset) < 0.0001;
	}

	/**
	 * Sets the object for position.
	 * 
	 * @param obj
	 *            the obj
	 * @param position
	 *            the position
	 */
	public void setObjectForPosition(Object obj, int position)
	{
		mObjs.put(Integer.valueOf(position), obj);
	}

	/**
	 * Find view from object.
	 * 
	 * @param position
	 *            the position
	 * @return the view
	 */
	public View findViewFromObject(int position)
	{
		Object o = mObjs.get(Integer.valueOf(position));
		if (o == null)
		{
			return null;
		}
		PagerAdapter a = getAdapter();
		View v;
		for (int i = 0; i < getChildCount(); i++)
		{
			v = getChildAt(i);
			if (a.isViewFromObject(v, o))
				return v;
		}
		return null;
	}

}