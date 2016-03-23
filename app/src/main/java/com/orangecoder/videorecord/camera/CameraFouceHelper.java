package com.orangecoder.videorecord.camera;

import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;

import java.util.Arrays;

/**
 * Created by xpmbp on 15/12/31.
 */
public class CameraFouceHelper {
    private static final float FOCUS_AREA_SIZE = 120f;
    private static final float FOCUS_AREA_FULL_SIZE = 2000f;

    private Rect tapArea;

    private float focusKoefW;
    private float focusKoefH;

    private float screenWidth;
    private float screenHeight;

    private boolean focusing;

    private MyCameraManager mCameraManager;
    private Callback mCallback;

    public CameraFouceHelper(MyCameraManager cameraManager, float width, float height, Callback callback) {
        mCameraManager = cameraManager;
        screenWidth = width;
        screenHeight = height;
        focusKoefW = width / FOCUS_AREA_FULL_SIZE;
        focusKoefH = height / FOCUS_AREA_FULL_SIZE;
        mCallback = callback;
    }

    public void focus(float x, float y) {
        tapArea = calculateTapArea(x, y, 1f);
        Camera.Parameters parameters = mCameraManager.getCameraInstance().getParameters();

        int maxFocusAreas = parameters.getMaxNumFocusAreas();
        if (maxFocusAreas > 0) {
            Camera.Area area = new Camera.Area(convert(tapArea), 300);
            parameters.setFocusAreas(Arrays.asList(area));
        }

        maxFocusAreas = parameters.getMaxNumMeteringAreas();
        if (maxFocusAreas > 0) {
            Rect rectMetering = calculateTapArea(x, y, 1.5f);
            Camera.Area area = new Camera.Area(convert(rectMetering), 300);
            parameters.setMeteringAreas(Arrays.asList(area));
        }

        mCameraManager.getCameraInstance().setParameters(parameters);

        if(mCallback != null) {
            mCallback.drawFocusFrame(tapArea);
        }
        startFocusing();
    }

    public void clearCameraFocus() {
        focusing = false;
        mCameraManager.clearCameraFocus();
    }

    private Rect convert(Rect rect) {
        Rect result = new Rect();

        result.top = normalize(rect.top/focusKoefH - 1000);
        result.left = normalize(rect.left/focusKoefW - 1000);
        result.right = normalize(rect.right/focusKoefW - 1000);
        result.bottom = normalize(rect.bottom/focusKoefH - 1000);

        return result;
    }

    private int normalize(float value) {
        if (value > 1000) {
            return 1000;
        }
        if (value < -1000) {
            return -1000;
        }
        return Math.round(value);
    }

    private Rect calculateTapArea(float x, float y, float coefficient) {
        int areaSize = Float.valueOf(FOCUS_AREA_SIZE * coefficient).intValue();

        int left = clamp((int)x-areaSize/2, 0, (int) (screenWidth-areaSize));
        int top = clamp((int)y-areaSize/2, 0, (int) (screenHeight-areaSize));

        RectF rect = new RectF(left, top, left+areaSize, top+areaSize);

        return new Rect(Math.round(rect.left),
                Math.round(rect.top),
                Math.round(rect.right),
                Math.round(rect.bottom));
    }

    private int clamp(int x, int min, int max) {
        if (x > max) {
            return max;
        }
        if (x < min) {
            return min;
        }
        return x;
    }

    private void startFocusing() {
        if (!focusing) {
            focusing = true;
            if (tapArea==null && mCallback!=null) {
                mCallback.drawFocusFrame(createAutoFocusRect());
            }
            mCameraManager.getCameraInstance().autoFocus(new MyCameraAutoFocusCallback());
        }
    }

    private Rect createAutoFocusRect() {
        int left = (int) (screenWidth/2 - FOCUS_AREA_SIZE);
        int right = (int) (screenWidth/2 + FOCUS_AREA_SIZE);
        int top = (int) (screenHeight/2 - FOCUS_AREA_SIZE);
        int bottom = (int) (screenHeight/2 + FOCUS_AREA_SIZE);
        return new Rect(left, top, right, bottom);
    }

    private class MyCameraAutoFocusCallback implements Camera.AutoFocusCallback {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            focusing = false;
        }
    }

    public interface Callback {
        void drawFocusFrame(Rect rect);
    }
}
