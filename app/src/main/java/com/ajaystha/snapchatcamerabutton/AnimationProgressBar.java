package com.ajaystha.snapchatcamerabutton;

import android.animation.ObjectAnimator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

/**
 * Created by ajayshrestha on 12/5/16.
 */

public class AnimationProgressBar {

    private ProgressBar mProgressBar;
    private ObjectAnimator mProgressAnimation;
    private long mDuration = 10000;//Default
    private int mProgressTo = 100;//Default


    public AnimationProgressBar(ProgressBar progressBar, int progressTo) {
        super();
        this.mProgressBar = progressBar;
        this.mProgressTo = progressTo;
    }

    /**
     * set the duration of progressbar in millisecond
     *
     * @param mDuration
     */
    public void setDuration(long mDuration) {
        this.mDuration = mDuration;
    }

    /**
     * start the progressbar
     */
    public void start() {
        mProgressAnimation = ObjectAnimator.ofInt(mProgressBar, "progress", mProgressBar.getProgress(), mProgressTo * 100);
        mProgressAnimation.setDuration(mDuration);
        mProgressAnimation.setInterpolator(new LinearInterpolator());
        mProgressAnimation.start();
    }

    /**
     * stop the progressbar
     */
    public void stop() {
        if (null != mProgressAnimation) {
            mProgressAnimation.cancel();
        }
    }

    public void reset() {
        if (mProgressBar != null) mProgressBar.setProgress(0);
    }
}