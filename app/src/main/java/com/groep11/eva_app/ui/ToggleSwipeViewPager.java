package com.groep11.eva_app.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom ViewPager which is able to toggle the ability to swipe,
 * use the method setSwipingEnabled(:boolean).
 */
public class ToggleSwipeViewPager extends ViewPager {

        private boolean isSwipeEnabled;

        public ToggleSwipeViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.isSwipeEnabled = true;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (this.isSwipeEnabled) {
                return super.onTouchEvent(event);
            }

            return false;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            if (this.isSwipeEnabled) {
                return super.onInterceptTouchEvent(event);
            }

            return false;
        }

        public void setSwipingEnabled(boolean enabled) {
            this.isSwipeEnabled = enabled;
        }
}
