package com.orangecoder.videorecord.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.Surface;
import android.widget.Toast;

import com.orangecoder.videorecord.utils.DensityUtil;

import java.util.List;



public class CameraHelper {
	private static final String TAG = "CameraHelper";

	public static Camera getCameraInstance(Context context) {
		Camera c = null;
		try {
			if(checkCameraHardware(context))
			{
				c = Camera.open(); 
			}
		} catch (Exception e) {
			Toast.makeText(context, "Camera is not available", Toast.LENGTH_SHORT).show();
		}
		return c; 
	}

	public static boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			Toast.makeText(context, "No camera on this device", Toast.LENGTH_SHORT).show();
			return false;
		}
	}
	
	public static int getCameraDisplayOrientation(
			Activity activity, int cameraId) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		return result;
	}

	public static void turnOnFlash(Context context, Camera camera, int currentCameraId) {
		if (context.getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_CAMERA_FLASH)) {
			Parameters params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
		}
	}

	public static void turnOffFlash(Context context, Camera camera, int currentCameraId) {
		if (context.getPackageManager().hasSystemFeature(
						PackageManager.FEATURE_CAMERA_FLASH)) {
			Parameters params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
		}
	}
	
	public static Size getOptimalPreviewSize(Activity currentActivity,
            List<Size> sizes, double targetRatio) {
        // Use a very small tolerance because we want an exact match.
        final double ASPECT_TOLERANCE = 0.01;
        if (sizes == null) {
            return null;
        }

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        // Because of bugs of overlay and layout, we sometimes will try to
        // layout the viewfinder in the portrait orientation and thus get the
        // wrong size of preview surface. When we change the preview size, the
        // new overlay will be created before the old one closed, which causes
        // an exception. For now, just get the screen size.
        int targetHeight = (int) Math.min(DensityUtil.getWidthInPx(currentActivity),
        		DensityUtil.getHeightInPx(currentActivity));
        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }
        // Cannot find the one match the aspect ratio. This should not happen.
        // Ignore the requirement.
        if (optimalSize == null) {
            Log.w(TAG, "No preview size match the aspect ratio");
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

	public static Size getMaxPreviewSize(List<Size> sizes, List<Size> vSize) {
        if (sizes == null) {
            return null;
        }

        Size optimalSize = null;
        int maxHeight = 0;
        int maxWidth = 0;
        
        for (Size size : sizes) {
        	if(size.height > maxHeight)
        	{
        		if(vSize==null)
        		{
        			optimalSize = size;
        			maxHeight = size.height;
        			maxWidth = size.width;
        			continue;
        		}
        		
        		if(vSize.contains(size))
        		{
        			optimalSize = size;
        			maxHeight = size.height;
        			maxWidth = size.width;
        		}
        	}else if(size.width > maxWidth)
        	{
        		if(vSize==null)
        		{
        			optimalSize = size;
            		maxWidth = size.width;
            		continue;
        		}
        		
        		if(vSize.contains(size))
        		{
        			optimalSize = size;
            		maxWidth = size.width;
        		}
        	}
        }
        
        Log.e(TAG, "width:"+optimalSize.width+"height:"+optimalSize.height);
        return optimalSize;
    }

	public static Size getOptimalPreviewSize(List<Size> sizes, List<Size> vSize) {
        if (sizes == null) {
            return null;
        }

        Size optimalSize = null;
        int maxHeight = 0;
        int maxWidth = 0;
        
        //优先返回720p和480p分辨率
        for (Size size : sizes) {
			if(size.height==720 && size.width>maxWidth)
			{
				optimalSize = size;
				maxWidth = size.width;
			}
		}
        if(optimalSize == null)
        {
        	maxWidth = 0;
        	for (Size size : sizes) {
            	if(size.height==480 && size.width>maxWidth)
    			{
        			optimalSize = size;
    				maxWidth = size.width;
    			}
    		}
        }
        
        //获取camera支持分辨率和camera视频分辨率的最大分辨率
        if(optimalSize == null)
        {
        	maxWidth = 0;
        	for (Size size : sizes) {
            	if(size.height > maxHeight)
            	{
            		if(vSize==null)
            		{
            			optimalSize = size;
            			maxHeight = size.height;
            			maxWidth = size.width;
            			continue;
            		}
            		
            		if(vSize.contains(size))
            		{
            			optimalSize = size;
            			maxHeight = size.height;
            			maxWidth = size.width;
            		}
            	}else if(size.width > maxWidth)
            	{
            		if(vSize==null)
            		{
            			optimalSize = size;
                		maxWidth = size.width;
                		continue;
            		}
            		
            		if(vSize.contains(size))
            		{
            			optimalSize = size;
                		maxWidth = size.width;
            		}
            	}
            }
        }
        
        Log.e(TAG, "width:"+optimalSize.width+"height:"+optimalSize.height);
        return optimalSize;
    }
}
