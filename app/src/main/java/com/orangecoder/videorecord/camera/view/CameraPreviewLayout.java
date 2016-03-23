package com.orangecoder.videorecord.camera.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.orangecoder.videorecord.camera.MyCameraManager;


/**
 * Created by xpmbp on 15/12/29.
 */
public class CameraPreviewLayout extends FrameLayout {

    private CameraPreview cameraPreview;

    public CameraPreviewLayout(Context context) {
        super(context);
    }

    public CameraPreviewLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void loadView(MyCameraManager cameraManager) {
        removeAllViews();

        CameraFocusRectView cameraFocusRectView = new CameraFocusRectView(getContext());
        cameraPreview = new CameraPreview((Activity)getContext(), cameraManager, cameraFocusRectView);

        addView(cameraPreview);
        addView(cameraFocusRectView);
    }

    public CameraPreview getCameraPreview() {
        return cameraPreview;
    }
}
