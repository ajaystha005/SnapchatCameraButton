package com.ajaystha.snapchatcamerabutton;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;

public class SnapChatCameraButtonActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final int MIN_DURATION = 100;

    private boolean isRecording = false;

    private ViewGroup mBtnRecord;

    private ProgressBar mProgressBar;
    private View mRecordView; //center red button appears while holding

    private boolean isLongHoldActive;
    private AnimationProgressBar mProgressBarAnimation;

    private ObjectAnimator scaleDownX;
    private ObjectAnimator scaleDownY;

    private long mHoldStartingTime;
    private long mHoldReleaseTime;


    private boolean isPhotoMode = true; // this is used to detect single tap
    private boolean isAnimationStarted = false;
    private boolean isProgressStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        initUI();
    }

    private void initUI() {
        mBtnRecord = (ViewGroup) findViewById(R.id.layoutRecord);
        mBtnRecord.setOnTouchListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.record_video_progressbar);
        mProgressBar.setMax(100 * 100);

        mRecordView = (View) findViewById(R.id.record_view);
    }

    /**
     * center red view when appears on hold
     */
    private void showRecordViewWithAnimation() {
        mRecordView.setVisibility(View.VISIBLE);
        mRecordView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale_up));
    }

    /**
     * hide then center red view when appears on hold
     */
    private void hideRecordView() {
        mRecordView.setVisibility(View.GONE);
    }


    private void applyRequiredAnimationForRecordView() {

        scaleDownX = ObjectAnimator.ofFloat(mBtnRecord, "scaleX", 1f, 1.2f);
        scaleDownY = ObjectAnimator.ofFloat(mBtnRecord, "scaleY", 1f, 1.2f);

        Interpolator bounceInterpolator = AnimationUtils.loadInterpolator(this, android.R.anim.bounce_interpolator);
        scaleDownX.setInterpolator(bounceInterpolator);
        scaleDownY.setInterpolator(bounceInterpolator);

        scaleDownX.setDuration(MIN_DURATION * 7);
        scaleDownY.setDuration(MIN_DURATION * 7);

        scaleDownX.start();
        scaleDownY.start();

    }

    private void animateProgressBarOnLongHold() {
        mProgressBarAnimation = new AnimationProgressBar(mProgressBar, 100);
        mProgressBarAnimation.setDuration(MIN_DURATION * 10);
        mProgressBarAnimation.start();
    }

    private void resetOnReleaseLongHold() {
        if (mProgressBarAnimation != null) {
            mProgressBarAnimation.stop();
            mProgressBarAnimation.reset();

            hideRecordView();

            mBtnRecord.clearAnimation();

            Interpolator linearInterpolator = AnimationUtils.loadInterpolator(this, android.R.anim.linear_interpolator);
            scaleDownX.setInterpolator(linearInterpolator);
            scaleDownY.setInterpolator(linearInterpolator);
            scaleDownX.setDuration(MIN_DURATION * 2);
            scaleDownY.setDuration(MIN_DURATION * 2);
            scaleDownX.reverse();
            scaleDownY.reverse();
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mHoldStartingTime = System.currentTimeMillis();

                if (isLongHoldActive == false) {
                    isLongHoldActive = true;
                }

                isPhotoMode = true;

                isAnimationStarted = false;
                isProgressStarted = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mHoldReleaseTime = System.currentTimeMillis();

                //TODO: detect single tap
                if (mHoldReleaseTime - mHoldStartingTime > 100) {
                    isPhotoMode = false;
                }

                if (isLongHoldActive == true && !isPhotoMode && !isAnimationStarted) {

                    isLongHoldActive = false;
                    isAnimationStarted = true;
                    if (!isRecording) {
                        //TODO:Start your media recorder here
                        isRecording = true;

                        applyRequiredAnimationForRecordView();
                        showRecordViewWithAnimation();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isAnimationStarted) {
                                    animateProgressBarOnLongHold();
                                    isProgressStarted = true;
                                }
                            }
                        }, 800);
                    }
                }
                break;


            case MotionEvent.ACTION_UP:

                if (!isPhotoMode) {
                    isLongHoldActive = false;
                    isAnimationStarted = false;
                    if (isRecording) {

                        resetOnReleaseLongHold();

                        isRecording = false;
                        if (isProgressStarted) {
                            //TODO: start playing recorded video
                            //startVideoPlayBackActivity();
                        }
                    }
                }
                mHoldStartingTime = 0;
                mHoldReleaseTime = 0;
                break;
        }
        return true;
    }
}
