package com.orangecoder.videorecord.camera.view;

import android.app.Activity;
import android.graphics.Rect;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.orangecoder.videorecord.camera.CameraFouceHelper;
import com.orangecoder.videorecord.camera.MyCameraManager;

import java.math.BigDecimal;



public class CameraPreview extends SurfaceView {

    private MyCameraManager mCameraManager;
    private CameraFocusRectView mCameraFocusRectView;
    private CameraFouceHelper mCameraFouceHelper;

    public CameraPreview(Activity activity,
                         MyCameraManager cameraManager,
                         CameraFocusRectView cameraFocusRectView) {
        super(activity);
        mCameraManager = cameraManager;
        mCameraFocusRectView = cameraFocusRectView;
        initHolder();
    }

    private void initHolder() {
        SurfaceHolder holder = getHolder();
        if (holder != null) {
            holder.addCallback(new MySurfaceHolderCallback());
            holder.setKeepScreenOn(true);
        }
    }

    private class MySurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mCameraManager.startPreview(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (holder.getSurface() == null) {
                return;
            }

            mCameraFouceHelper = new CameraFouceHelper(mCameraManager,
                    width, height, new MyCameraFouceHelperCallback());

            mCameraManager.initCamera(holder);

            setOnTouchListener(new MyTouchListener());
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mCameraManager.stopPreview();
        }
    }

    private class MyTouchListener implements OnTouchListener {

        private ScaleGestureDetector mScaleDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        private GestureDetector mTapDetector = new GestureDetector(getContext(), new TapListener());

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mCameraFocusRectView.hide();
            if(mCameraFouceHelper != null) {
                mCameraFouceHelper.clearCameraFocus();
            }

            if (event.getPointerCount() > 1) {
                mScaleDetector.onTouchEvent(event);
                return true;
            }
            if (mCameraManager.hasAutoFocus()) {
                mTapDetector.onTouchEvent(event);
                return true;
            }
            return true;
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            private static final int ACCURACY = 3;
            private float prevScaleFactor;

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale(detector.getScaleFactor());
                return true;
            }

            private void scale(float scaleFactor) {
                scaleFactor = BigDecimal.valueOf(scaleFactor).setScale(ACCURACY, BigDecimal.ROUND_HALF_UP).floatValue();
                if (Float.compare(scaleFactor, 1.0f) == 0 || Float.compare(scaleFactor, prevScaleFactor) == 0) {
                    return;
                }
                if (scaleFactor > 1f) {
                    mCameraManager.zoomIn();
                }
                if (scaleFactor < 1f) {
                    mCameraManager.zoomOut();
                }
                prevScaleFactor = scaleFactor;
            }
        }

        private class TapListener extends GestureDetector.SimpleOnGestureListener {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent event) {
                if(mCameraFouceHelper != null) {
                    mCameraFouceHelper.focus(event.getX(), event.getY());
                }
                return true;
            }
        }

    }

    private class MyCameraFouceHelperCallback implements CameraFouceHelper.Callback {

        @Override
        public void drawFocusFrame(Rect rect) {
            if(rect == null) {
                return;
            }

            int length = rect.right - rect.left;
            mCameraFocusRectView.show(rect.left+length/2, rect.top+length/2, length);
        }
    }
}
