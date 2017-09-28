/*
 * Copyright 2017 jason. https://github.com/jas0nchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.jas0nchen.horizontalscrollweek;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import org.joda.time.DateTime;

import java.util.List;

import io.github.jas0nchen.horizontalscrollweek.utils.Utils;

/**
 * Author: jason
 * Time: 2017/8/11
 */
public class HorizontalScrollWeek extends View {

    private static final int TYPE_SMALL = 1;

    private static final int TYPE_NORMAL = 2;

    private int mSize = TYPE_NORMAL;

    private int mNormalTextColor;

    private int mSelectedTextColor;

    private int mIndicatorColor;

    private int mWhiteTextColor = Color.parseColor("#FFFFFF");

    private int mWeekTextSize;

    private int mDayTextSize;

    private int mWeekTextHeight;

    private int mCellWidth;

    private int mInitSelectAt;

    private int mCurrentIndex;

    private int mLoadingOffset = 6; // 可以调用插入更多日期的阈值

    private int mTopPadding = (int) dp2px(10);

    private int mMidMargin = (int) dp2px(10);

    private int mBottomPadding = (int) dp2px(7);

    private int mBgCircleRadius = (int) dp2px(13.5f);

    private int mIndicatorWidth = (int) dp2px(13);

    private int mIndicatorHeight = (int) dp2px(8);

    private float mXOffset;

    private boolean showTodayOnDay;

    private Paint mPaint, mBgPaint, mIndicatorPaint;

    /**
     * is scrolling
     */
    private boolean isScrolling;

    /**
     * if need scroll animation
     */
    private boolean needScrollAnim = true;

    /**
     * Path of indicator
     */
    private Path mIndicatorPath;

    /**
     * The date of first selected day
     */
    private DateTime mOrigin;

    /**
     * The date of today
     */
    private DateTime mToday;

    /**
     * The viewpager relate to calendar
     */
    private ViewPager mViewPager;

    /**
     * Source date list
     */
    private List<Selectable> mDates;

    /**
     * Gesture detector
     */
    private GestureDetector mGestureDetector;

    /**
     * The listener of page scroll
     */
    private OnPageScrollStateChangeListener mSelectedListener;

    /**
     * Current state of page scroll
     */
    private int mPageScrollState = OnPageScrollStateChangeListener.STATE_IDLE;

    public HorizontalScrollWeek(Context context) {
        this(context, null);
    }

    public HorizontalScrollWeek(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollWeek(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HorizontalScrollWeek);
        mNormalTextColor = array.getColor(R.styleable.HorizontalScrollWeek_hsw_normal_text_color, Color.parseColor("#8E8E8E"));
        mSelectedTextColor = array.getColor(R.styleable.HorizontalScrollWeek_hsw_selected_text_color, Color.parseColor("#343434"));
        mIndicatorColor = array.getColor(R.styleable.HorizontalScrollWeek_hsw_indicator_color, Color.parseColor("#FFFFFF"));
        mWeekTextSize = array.getDimensionPixelSize(R.styleable.HorizontalScrollWeek_hsw_week_text_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13, getResources().getDisplayMetrics()));
        mDayTextSize = array.getDimensionPixelSize(R.styleable.HorizontalScrollWeek_hsw_day_text_size, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));
        showTodayOnDay = array.getBoolean(R.styleable.HorizontalScrollWeek_hsw_show_today_on_day, false);
        mSize = array.getInt(R.styleable.HorizontalScrollWeek_hsw_size, 2);
        array.recycle();

        init();
    }

    private void init() {
        this.mToday = DateTime.now();

        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setTextSize(mWeekTextSize);

        this.mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mBgPaint.setStyle(Paint.Style.FILL);

        this.mIndicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mIndicatorPaint.setColor(mIndicatorColor);

        this.mIndicatorPath = new Path();
        this.mWeekTextHeight = (int) Math.ceil(mPaint.descent() - mPaint.ascent());
        this.mCellWidth = (int) (getResources().getDisplayMetrics().widthPixels * 1.0f / 7);

        if (mSize == TYPE_SMALL) {
            mTopPadding = (int) dp2px(3);
            mMidMargin = (int) dp2px(3);
            mBottomPadding = (int) dp2px(3);
        }

        initGestureDetector();
    }

    public HorizontalScrollWeek withListener(OnPageScrollStateChangeListener listener) {
        this.mSelectedListener = listener;
        return this;
    }

    public HorizontalScrollWeek initSelectAt(int initSelectAt) {
        this.mInitSelectAt = initSelectAt;
        return this;
    }

    @SuppressWarnings("unchecked")
    public HorizontalScrollWeek withDates(List dates) {
        this.mDates = dates;
        return this;
    }

    public HorizontalScrollWeek setLoadingOffset(int loadingOffset) {
        this.mLoadingOffset = loadingOffset;
        return this;
    }

    public void setup(ViewPager viewPager) {
        if (mInitSelectAt < 0 || mInitSelectAt >= mDates.size()) {
            throw new RuntimeException("Please setup correct index of origin day!");
        }
        this.mViewPager = viewPager;
        if (mViewPager != null && mViewPager.getAdapter().getCount() != mDates.size()) {
            throw new RuntimeException("The size of DateList is not equal to the size of ViewPager!");
        }
        setupViewPager();
        invalidate();
    }

    private void setupViewPager() {
        if (mViewPager == null) {
            return;
        }
        this.mXOffset = 0;
        this.mViewPager.setCurrentItem(mInitSelectAt, false);
        this.mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private int lastPosition = mInitSelectAt;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (needScrollAnim) {
                    startScroll(position, false);
                } else {
                    startScrollWithoutAnim(position);
                    needScrollAnim = true;
                }

                boolean toRight = lastPosition < position;
                boolean needLoad;
                if (toRight) {
                    needLoad = Math.abs(position - (mDates.size() - 1)) <= mLoadingOffset;
                } else {
                    needLoad = position <= mLoadingOffset;
                }
                if (mPageScrollState == OnPageScrollStateChangeListener.STATE_IDLE && mSelectedListener != null && needLoad) {
                    mPageScrollState = OnPageScrollStateChangeListener.STATE_LOADING;
                    mSelectedListener.onScrollStateChange(toRight);
                }
                lastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightSize = mTopPadding + mWeekTextHeight + mMidMargin + mBgCircleRadius * 2 + mBottomPadding + mIndicatorHeight;

        initIndicatorPath(heightSize);
        setMeasuredDimension(getMeasuredWidth(), heightSize);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDates == null || mDates.size() == 0) {
            return;
        }

        drawDayByDayFromLeft(canvas);
        drawIndicator(canvas);
    }

    private void drawDayByDayFromLeft(Canvas canvas) {
        int startX, startY;
        int currentDrawingDay;

        mCurrentIndex = mInitSelectAt - (int) (mXOffset / mCellWidth);
        float offset = mXOffset - (int) (mXOffset / mCellWidth) * mCellWidth;

        for (int i = -4; i <= 4; i++) {
            currentDrawingDay = mCurrentIndex + i;
            if (getCurrentDrawingDate(currentDrawingDay) == null) {
                continue;
            }

            mPaint.setTextSize(mWeekTextSize);
            if (!isScrolling && currentDrawingDay == mCurrentIndex) {
                mPaint.setColor(mSelectedTextColor);
            } else {
                mPaint.setColor(mNormalTextColor);
            }
            // 先绘制周
            startX = (int) ((mCellWidth - getTextWidth(mPaint, getCurrentDrawingWeek(currentDrawingDay))) * 1.0f / 2 + offset + (i + 3) * mCellWidth);
            startY = (int) (mTopPadding + mWeekTextHeight * 1.0f / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            canvas.drawText(getCurrentDrawingWeek(currentDrawingDay), startX, startY, mPaint);

            // 绘制天
            mPaint.setTextSize(mDayTextSize);
            startX = (int) (mCellWidth * 1.0f / 2 + offset + (i + 3) * mCellWidth); // 此处实际为背景圆的中心点X坐标
            startY = mTopPadding + mWeekTextHeight + mMidMargin + mBgCircleRadius; // 此处实际为背景圆的中心点Y坐标
            if (getCurrentDrawingDayBackgroundColor(currentDrawingDay) != -1) {
                // 绘制背景圆
                mBgPaint.setColor(getCurrentDrawingDayBackgroundColor(currentDrawingDay));
                canvas.drawCircle(startX, startY, mBgCircleRadius, mBgPaint);
                mPaint.setColor(mWhiteTextColor);
            }
            startX -= getTextWidth(mPaint, getCurrentDrawingDay(currentDrawingDay)) * 1.0f / 2;
            startY -= (mPaint.ascent() + mPaint.descent()) / 2;
            canvas.drawText(getCurrentDrawingDay(currentDrawingDay), startX, startY, mPaint);
        }
    }

    private void drawIndicator(Canvas canvas) {
        canvas.drawPath(mIndicatorPath, mIndicatorPaint);
    }

    private void initIndicatorPath(int heightSize) {
        mIndicatorPath.moveTo(getMeasuredWidth() / 2 - mIndicatorWidth / 2, heightSize);
        mIndicatorPath.lineTo(getMeasuredWidth() / 2, heightSize - mIndicatorHeight);
        mIndicatorPath.lineTo(getMeasuredWidth() / 2 + mIndicatorWidth / 2, heightSize);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                doClickAction((int) e.getX(), (int) e.getY());
                return true;
            }
        });
    }

    private void doClickAction(int x, int y) {
        if (isScrolling) {
            return;
        }

        if (y > getHeight()) {
            return;
        }

        int newCurrentDay = mCurrentIndex - (3 - findThePositionClicked(x));

        startScroll(newCurrentDay, true);
    }

    public void scrollToOriginDay() {
        startScroll(mInitSelectAt, true);
    }

    private void startScroll(int newCurrentDay, boolean needScrollViewpager) {
        if (getCurrentDrawingDate(newCurrentDay) == null) {
            return;
        }

        startAnim(newCurrentDay, needScrollViewpager);
    }

    private void startScrollWithoutAnim(int newCurrentDay) {
        if (getCurrentDrawingDate(newCurrentDay) == null) {
            return;
        }

        mXOffset = (mInitSelectAt - newCurrentDay) * mCellWidth;
        invalidate();
    }

    private int findThePositionClicked(int x) {
        return (int) (x * 1.0f / mCellWidth);
    }

    private void startAnim(final int newCurrentDay, final boolean needScrollViewpager) {
        float newXOffset = (mInitSelectAt - newCurrentDay) * mCellWidth;
        int gap = (int) (Math.abs(newXOffset - mXOffset) / mCellWidth);
        int duration = gap >= 3 ? 300 : gap * 100;
        ValueAnimator animator = ValueAnimator.ofFloat(mXOffset, newXOffset);
        animator.setDuration(duration);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                isScrolling = true;
                mXOffset = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isScrolling = false;
                if (needScrollViewpager && mViewPager != null) {
                    mViewPager.setCurrentItem(newCurrentDay);
                }
                postInvalidate();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    @SuppressWarnings("unchecked")
    public void addToLeft(List dateList) {
        mPageScrollState = OnPageScrollStateChangeListener.STATE_IDLE;
        needScrollAnim = false;
        mInitSelectAt = mInitSelectAt + dateList.size();
        mDates.addAll(0, dateList);
        mViewPager.getAdapter().notifyDataSetChanged();
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + dateList.size(), false);
    }

    @SuppressWarnings("unchecked")
    public void addToRight(List dateList) {
        mPageScrollState = OnPageScrollStateChangeListener.STATE_IDLE;
        mDates.addAll(dateList);
        mViewPager.getAdapter().notifyDataSetChanged();
    }

    public void setPageScrollIdle() {
        this.mPageScrollState = OnPageScrollStateChangeListener.STATE_IDLE;
    }

    private int getCurrentDrawingDayBackgroundColor(int day) {
        if (getCurrentDrawingDate(day) == null) {
            return -1;
        }
        return mDates.get(day).getBackgroundColor();
    }

    private DateTime getCurrentDrawingDate(int day) {
        DateTime currentDrawingDate;
        if (day < 0 || mDates == null || day >= mDates.size()) {
            currentDrawingDate = null;
        } else {
            if (mDates.get(day).getDate() == null) {
                throw new NullPointerException("Please implement the method getDate() of Selectable");
            }
            currentDrawingDate = mDates.get(day).getDate();
        }
        return currentDrawingDate;
    }

    private String getCurrentDrawingWeek(int day) {
        if (getCurrentDrawingDate(day) == null) {
            return "";
        }
        if (!showTodayOnDay) {
            if (Utils.isSameDay(mToday, getCurrentDrawingDate(day))) {
                return getContext().getString(R.string.today);
            }
        }
        return Utils.getChineseWeekOfTheDay(getContext(), getCurrentDrawingDate(day).getDayOfWeek());
    }

    private String getCurrentDrawingDay(int day) {
        if (getCurrentDrawingDate(day) == null) {
            return "";
        }
        if (showTodayOnDay) {
            if (Utils.isSameDay(mToday, getCurrentDrawingDate(day))) {
                return getContext().getString(R.string.today);
            }
        }
        return String.valueOf(getCurrentDrawingDate(day).getDayOfMonth());
    }

    private float getTextWidth(Paint paint, String text) {
        return paint.measureText(text);
    }

    public void notifyDataSetChanged() {
        invalidate();
    }

    private float dp2px(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }
}
