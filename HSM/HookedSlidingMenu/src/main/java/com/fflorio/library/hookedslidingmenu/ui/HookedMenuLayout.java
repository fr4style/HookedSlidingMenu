/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Francesco Florio
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * Francesco Florio
 * Email: floriofrancesco@gmail.com
 * Twitter: @fr4style
 * Linkedin: it.linkedin.com/pub/francesco-florio/21/62/a68/
 *
 * Made with love in the south of Italy (Cosenza, Italy)
 *
 * This class is based on Flavien Laurent article: http://flavienlaurent.com/blog/2013/08/28/each-navigation-drawer-hides-a-viewdraghelper
 */

package com.fflorio.library.hookedslidingmenu.ui;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fflorio.library.hookedslidingmenu.R;

import java.util.ArrayList;
import java.util.List;

public class HookedMenuLayout extends ViewGroup{

    //Obj
    private ViewDragHelper mDragHelper;
    private List<HookedMenuEventListener> listeners = new ArrayList<HookedMenuEventListener>();

    //UI
    private View hookView; //the view to drag
    private View menuContent;

    //Values
    /** menu status */private boolean isOpen = false;
    private boolean startClosed = true;

    private float mInitialMotionX;
    private float mInitialMotionY;

    private int displayW;
    private int mLeft = Integer.MIN_VALUE;
    private int mDragRange;

    /** Value: 0 if the menu is closed, 1 otherwise */
    private float mDragOffset;

//-----------------------------------------------------------
// Constructors && Init
//-----------------------------------------------------------
    public HookedMenuLayout(Context context) { super(context); init(); }
    public HookedMenuLayout(Context context, AttributeSet attrs) { super(context, attrs); init();}
    public HookedMenuLayout(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); init();}

    private void init(){
        mDragHelper = ViewDragHelper.create(this, 1.0f, callback);
        displayW = getResources().getDisplayMetrics().widthPixels;
    }

    @Override protected void onFinishInflate() {
        hookView = findViewById(R.id.hook);
        menuContent = findViewById(R.id.menuContent);
    }

//-----------------------------------------------------------
// EventListener
//-----------------------------------------------------------
    public void addHookedMenuEventListener(HookedMenuEventListener listener){ listeners.add(listener); }
    public void removeHookedMenuEventListener(HookedMenuEventListener listener){ listeners.remove(listener); }

    public void updateListeners(boolean newStatus){
        if(isOpen == newStatus) return;
        for(HookedMenuEventListener listener: listeners)
            listener.onStatusChanged(newStatus);
    }

    public boolean isOpen(){ return isOpen; }

//-----------------------------------------------------------
// CustomLayout implementation
//-----------------------------------------------------------

    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        int maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxHeight = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                             resolveSizeAndState(maxHeight,heightMeasureSpec, 0));
    }

    @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int hookMeasuredWidth = hookView.getMeasuredWidth();
        final int menuMeasuredWidth = menuContent.getMeasuredWidth();

        if(mLeft == Integer.MIN_VALUE)
            mLeft = (startClosed) ? displayW - hookMeasuredWidth
                                  : displayW - hookMeasuredWidth - menuMeasuredWidth;

        mDragRange = menuMeasuredWidth;

        hookView.layout(mLeft,
                        0,
                        mLeft + hookMeasuredWidth,
                        b);

        menuContent.layout(mLeft+hookMeasuredWidth,
                           0,
                           mLeft+hookMeasuredWidth+menuMeasuredWidth,
                           b);
    }

//-----------------------------------------------------------
// Manage touchEvents
//-----------------------------------------------------------
    @Override public void computeScroll(){
        if(mDragHelper.continueSettling(true))
            ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if(action != MotionEvent.ACTION_DOWN){ //????
            mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        }

        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP){
            mDragHelper.cancel();
            return false;
        }

        final float x = ev.getX();
        final float y = ev.getY();
        boolean interceptTap = false;

        switch(action){
            case MotionEvent.ACTION_DOWN:{
                mInitialMotionX = x;
                mInitialMotionY = y;
                interceptTap = mDragHelper.isViewUnder(hookView, (int)x, (int)y);
            }break;

            case MotionEvent.ACTION_MOVE:{
                final float adx = Math.abs(x - mInitialMotionX);
                final float ady = Math.abs(y - mInitialMotionY);
                final int slop = mDragHelper.getTouchSlop();
                if(adx > slop && ady > adx){
                    mDragHelper.cancel();
                    return false;
                }
            }
        }

        return mDragHelper.shouldInterceptTouchEvent(ev) || interceptTap;
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        final int action = event.getAction();
        final float x = event.getX();
        final float y = event.getY();

        boolean isHeaderViewUnder = mDragHelper.isViewUnder(hookView, (int) x, (int) y);
        switch(action & MotionEventCompat.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:{
                mInitialMotionX = x;
                mInitialMotionY = y;
            }break;

            case MotionEvent.ACTION_UP:{
                final float dx = x - mInitialMotionX;
                final float dy = y - mInitialMotionY;
                final int slop = mDragHelper.getTouchSlop();
                if((dx * dx + dy * dy) < (slop * slop) && isHeaderViewUnder){
                    if(mDragOffset == 0) smoothSlideTo(1f);
                    else smoothSlideTo(0f);
                }
            }break;
        }
        return isHeaderViewUnder &&
               isViewHit(hookView, (int)x, (int)y) ||
               isViewHit(menuContent, (int)x, (int)y) ;
    }

    private boolean isViewHit(View view, int x, int y){
        int[] viewLocation = new int[2];
        view.getLocationOnScreen(viewLocation);
        int[] parentLocation = new int[2];
        this.getLocationOnScreen(parentLocation);

        int screenX = parentLocation[0] + x;
        int screenY = parentLocation[1] + y;

        return  screenX >= viewLocation[0] &&
                screenX < viewLocation[0] + view.getWidth() &&
                screenY >= viewLocation[1] &&
                screenY < viewLocation[1] + view.getHeight();
    }


    public void open(){ smoothSlideTo(1.0f); }
    public void close(){ smoothSlideTo(0.0f);}

    /**
     *
     * @param slideOffset - the percentage of movement
     * @return
     */
    protected boolean smoothSlideTo(float slideOffset){
       final int leftBound = displayW - hookView.getWidth();
       int x = (int)(leftBound - slideOffset * mDragRange);
       if(mDragHelper.smoothSlideViewTo(hookView, x, hookView.getTop())){
           ViewCompat.postInvalidateOnAnimation(this);
           return true;
       }

       return false;
    }

//-----------------------------------------------------------
// Callbacks
//-----------------------------------------------------------
    /**
     * Object that configure VDH behaviors
     */
    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        @Override public boolean tryCaptureView(View child, int pointerId) { return child == hookView; }
        @Override public int getViewHorizontalDragRange(View child) { return mDragRange;  }

        //Enable the horizontal scrolling
        @Override public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int rightBound = getWidth() - hookView.getWidth();
            final int leftBound = rightBound - menuContent.getWidth();


            final int newLeft = Math.min(Math.max(left, leftBound), rightBound);

            return newLeft;
        }

        @Override public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            mLeft = left;

            //0 if the menu is closed, 1 otherwise
            mDragOffset = (float) Math.abs(left + hookView.getWidth() - displayW) / mDragRange;
            //menuContent.setAlpha(mDragOffset); Add an alpha behaviour to the contentview

            requestLayout();

            if(mDragOffset == 0){
                boolean newStatus = false;
                updateListeners(newStatus);
                isOpen = newStatus;
            }else if(mDragOffset == 1){
                boolean newStatus = true;
                updateListeners(newStatus);
                isOpen = newStatus;
            }
        }

        @Override public void onViewReleased(View releasedChild, float xvel, float yvel) {
            boolean toOpen;

            if (xvel < 0) toOpen = true;
            else if (xvel > 0) toOpen = false;
            else toOpen = mDragOffset > 0.5f;

            int left = (toOpen) ?  displayW - hookView.getWidth() - mDragRange
                                :  displayW - hookView.getWidth();

            mDragHelper.settleCapturedViewAt(left, releasedChild.getTop());
            invalidate();
        }
    };
}