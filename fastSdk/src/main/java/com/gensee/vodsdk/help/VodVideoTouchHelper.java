package com.gensee.vodsdk.help;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

import com.gensee.vodsdk.VodActivity;

/**
 * Created by wangtao on 2018/12/28.
 */
public class VodVideoTouchHelper {

    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_VOLUME = 1;
    private static final int TOUCH_BRIGHTNESS = 2;
    private static final int TOUCH_SEEK = 3;

    private VodActivity mActivity;

    private int mTouchAction = TOUCH_NONE;
    private float mInitTouchY, mTouchY = -1f, mTouchX = -1f;
    private long mTime;//当前时长
    private long mLength;//总时长

    public VodVideoTouchHelper(Activity activity) {
        this.mActivity = (VodActivity) activity;
    }

    public void updateTimeLength(long time, long length) {
        this.mTime = time;
        this.mLength = length;
    }

    /**
     * 修改 进度
     */
    private void doSeekTouch(int coef, float gesturesize, boolean seek) {
        if (coef == 0) {
            coef = 1;
        }
        if (Math.abs(gesturesize) < 1) {
            return;
        }

        if (mTouchAction != TOUCH_NONE && mTouchAction != TOUCH_SEEK) {
            return;
        }

        mTouchAction = TOUCH_SEEK;
        int jump = (int) ((Math.signum(gesturesize) * ((600000 * Math.pow((gesturesize / 8), 4)) + 3000)) / coef);

        if ((jump > 0) && ((mTime + jump) > mLength))
            jump = (int) (mLength - mTime);
        if ((jump < 0) && ((mTime + jump) < 0))
            jump = (int) -mTime;

        int jumpPosition = (int) (mTime + jump);
        if (seek && mLength > 0) {
            mActivity.setSeek(jumpPosition);
        }
        mActivity.showSeekChange(jumpPosition);
    }

    public boolean onTouchEvent(MotionEvent event) {
        DisplayMetrics screen = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(screen);

        float x_changed, y_changed;
        if (mTouchX != -1f && mTouchY != -1f) {
            y_changed = event.getRawY() - mTouchY;
            x_changed = event.getRawX() - mTouchX;
        } else {
            x_changed = 0f;
            y_changed = 0f;
        }

        float coef = Math.abs(y_changed / x_changed);
        float xgesturesize = ((x_changed / screen.xdpi) * 2.54f);
        float delta_y = Math.max(1f, (Math.abs(mInitTouchY - event.getRawY()) / screen.xdpi + 0.5f) * 2f);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Audio
                mTouchY = mInitTouchY = event.getRawY();
                mTouchAction = TOUCH_NONE;
                // Seek
                mTouchX = event.getRawX();
                break;

            case MotionEvent.ACTION_MOVE:
                if (mTouchAction != TOUCH_SEEK && coef > 2) {

                } else {
                    doSeekTouch(Math.round(delta_y), xgesturesize, false);
                }
                break;

            case MotionEvent.ACTION_UP:
                if (mTouchAction == TOUCH_NONE) {
                    return false;
                }
                if (mTouchAction == TOUCH_SEEK) {
                    doSeekTouch(Math.round(delta_y), xgesturesize, true);
                }
                mTouchX = -1f;
                mTouchY = -1f;
                break;
        }
        return mTouchAction != TOUCH_NONE;
    }
}
