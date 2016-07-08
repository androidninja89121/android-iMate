package com.tieyouin.pager;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.util.AttributeSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.tieyouin.R;

/**
 * The Class OutlineContainer.
 */
public class OutlineContainer extends FrameLayout implements Animatable
{

	/** The m outline paint. */
	private Paint mOutlinePaint;

	/** The m is running. */
	private boolean mIsRunning = false;

	/** The m start time. */
	private long mStartTime;

	/** The m alpha. */
	private float mAlpha = 1.0f;

	/** The Constant ANIMATION_DURATION. */
	private static final long ANIMATION_DURATION = 500;

	/** The Constant FRAME_DURATION. */
	private static final long FRAME_DURATION = 1000 / 60;

	/** The m interpolator. */
	private final Interpolator mInterpolator = new Interpolator() {
		@Override
		public float getInterpolation(float t)
		{
			t -= 1.0f;
			return t * t * t + 1.0f;
		}
	};

	/**
	 * Instantiates a new outline container.
	 * 
	 * @param context
	 *            the context
	 */
	public OutlineContainer(Context context)
	{
		super(context);
		init();
	}

	/**
	 * Instantiates a new outline container.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 */
	public OutlineContainer(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	/**
	 * Instantiates a new outline container.
	 * 
	 * @param context
	 *            the context
	 * @param attrs
	 *            the attrs
	 * @param defStyle
	 *            the def style
	 */
	public OutlineContainer(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * Inits the.
	 */
	private void init()
	{
		mOutlinePaint = new Paint();
		mOutlinePaint.setAntiAlias(true);
		mOutlinePaint.setStrokeWidth(Util.dpToPx(getResources(), 2));
		mOutlinePaint.setColor(getResources().getColor(R.color.holo_blue));
		mOutlinePaint.setStyle(Style.STROKE);

		int padding = Util.dpToPx(getResources(), 10);
		setPadding(padding, padding, padding, padding);
	}

	/* (non-Javadoc)
	 * @see android.view.ViewGroup#dispatchDraw(android.graphics.Canvas)
	 */
	@Override
	protected void dispatchDraw(Canvas canvas)
	{
		super.dispatchDraw(canvas);
		int offset = Util.dpToPx(getResources(), 5);
		if (mOutlinePaint.getColor() != JazzyViewPager.sOutlineColor)
		{
			mOutlinePaint.setColor(JazzyViewPager.sOutlineColor);
		}
		mOutlinePaint.setAlpha((int) (mAlpha * 255));
		Rect rect = new Rect(offset, offset, getMeasuredWidth() - offset,
				getMeasuredHeight() - offset);
		canvas.drawRect(rect, mOutlinePaint);
	}

	/**
	 * Sets the outline alpha.
	 * 
	 * @param alpha
	 *            the new outline alpha
	 */
	public void setOutlineAlpha(float alpha)
	{
		mAlpha = alpha;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Animatable#isRunning()
	 */
	@Override
	public boolean isRunning()
	{
		return mIsRunning;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Animatable#start()
	 */
	@Override
	public void start()
	{
		if (mIsRunning)
			return;
		mIsRunning = true;
		mStartTime = AnimationUtils.currentAnimationTimeMillis();
		post(mUpdater);
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Animatable#stop()
	 */
	@Override
	public void stop()
	{
		if (!mIsRunning)
			return;
		mIsRunning = false;
	}

	/** The m updater. */
	private final Runnable mUpdater = new Runnable() {
		@Override
		public void run()
		{
			long now = AnimationUtils.currentAnimationTimeMillis();
			long duration = now - mStartTime;
			if (duration >= ANIMATION_DURATION)
			{
				mAlpha = 0.0f;
				invalidate();
				stop();
				return;
			}
			else
			{
				mAlpha = mInterpolator.getInterpolation(1 - duration
						/ (float) ANIMATION_DURATION);
				invalidate();
			}
			postDelayed(mUpdater, FRAME_DURATION);
		}
	};

}
