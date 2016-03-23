package com.orangecoder.videorecord.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.List;

public class MyCameraManager {
	
	private static final String TAG = "MyCameraManager";
	
	private Context mContext;
	private Camera mCamera;
	private int currentCamera = CameraInfo.CAMERA_FACING_BACK;
	private Camera.Size resolutionSize;
	private int cameraDisplayOrientation = 90;  //默认是竖屏

	public MyCameraManager(Context context) {
		mContext = context;
	}

	//开启camera
	public boolean openCamera() {
		mCamera = CameraHelper.getCameraInstance(mContext);
		if(mCamera != null)
		{
			currentCamera = CameraInfo.CAMERA_FACING_BACK;
			return true;
		}else {
			return false;
		}
	}

	//释放camera
	public void releaseCamera() {
		if (mCamera != null) {
			mCamera.lock();
			mCamera.release();
			mCamera = null;
		}
	}

	public void startPreview(SurfaceHolder holder) {
		try {
			mCamera.setPreviewDisplay(holder);
			mCamera.setDisplayOrientation(cameraDisplayOrientation);
			Parameters parameters = mCamera.getParameters();
			parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
			mCamera.setParameters(parameters);
			mCamera.startPreview();
		} catch (Exception e) {
		}
	}

	public void stopPreview() {
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		}
	}

	public void initCamera(SurfaceHolder holder) {
		initCameraHandler.removeCallbacksAndMessages(null);
		initCameraHandler.post(new InitCameraRunnable(holder));
	}
	Handler initCameraHandler = new Handler();
	class InitCameraRunnable implements Runnable {
		private SurfaceHolder mHolder;

		public InitCameraRunnable(SurfaceHolder holder) {
			mHolder = holder;
		}

		@Override
		public void run() {
			if (mCamera == null) {
				return;
			}
			mCamera.stopPreview();

			Parameters params = mCamera.getParameters();

			zoomRatios = params.getZoomRatios();
			zoomIndex = minZoomIndex = 0;
			maxZoomIndex = params.getMaxZoom();

			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			List<Camera.Size> vSize = params.getSupportedVideoSizes();
			Camera.Size size = CameraHelper.getOptimalPreviewSize(sizes, vSize);
			if(size != null)
			{
				params.setPreviewSize(size.width, size.height);
				mCamera.setParameters(params);

				resolutionSize = size;
			}else {
				resolutionSize = null;
			}

			try {
				mCamera.setPreviewDisplay(mHolder);
				cameraDisplayOrientation = CameraHelper.getCameraDisplayOrientation(
						(Activity)mContext, currentCamera);
				mCamera.setDisplayOrientation(cameraDisplayOrientation);

				mCamera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private List<Integer> zoomRatios;
	private int zoomIndex, minZoomIndex, maxZoomIndex;

	public void zoomIn() {
		if (++zoomIndex > maxZoomIndex) {
			zoomIndex = maxZoomIndex;
		}
		setZoom(zoomIndex);
	}

	public void zoomOut() {
		if (--zoomIndex < minZoomIndex) {
			zoomIndex = minZoomIndex;
		}
		setZoom(zoomIndex);
	}

	private void setZoom(int index) {
		Parameters parameters = mCamera.getParameters();
		parameters.setZoom(index);
		mCamera.setParameters(parameters);
	}

	public boolean hasAutoFocus() {
		List<String> supportedFocusModes = mCamera.getParameters().getSupportedFocusModes();
		return supportedFocusModes!=null && supportedFocusModes.contains(Parameters.FOCUS_MODE_AUTO);
	}

	public void clearCameraFocus() {
		try{
			if(hasAutoFocus()) {
				mCamera.cancelAutoFocus();
				Parameters parameters = mCamera.getParameters();
				parameters.setFocusAreas(null);
				parameters.setMeteringAreas(null);
				mCamera.setParameters(parameters);
			}
		}catch (Exception e){

		}
	}

	public void turnOnFlash() {
		CameraHelper.turnOnFlash(mContext, mCamera, currentCamera);
	}

	public void turnOffFlash() {
		CameraHelper.turnOffFlash(mContext, mCamera, currentCamera);
	}

	@SuppressLint("NewApi")
	public void customShutterSound() {
		try {
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
				CameraInfo cameraInfo = new CameraInfo();
				Camera.getCameraInfo(currentCamera, cameraInfo);
				if (cameraInfo.canDisableShutterSound) {
					mCamera.enableShutterSound(false);
				}

				final SoundPool soundPool = new SoundPool(1,
						AudioManager.STREAM_MUSIC, 0);
//				final int soundID = soundPool.load(mContext,
//						R.raw.shootsound, 1);
				final int soundID = 0; //先顶着

				final AudioManager mgr = (AudioManager) mContext
						.getSystemService(Context.AUDIO_SERVICE);
				float streamVolumeCurrent = mgr
						.getStreamVolume(AudioManager.STREAM_MUSIC);
				float streamVolumeMax = mgr
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				final float volume = streamVolumeCurrent / streamVolumeMax;

				soundPool.setOnLoadCompleteListener(
						new SoundPool.OnLoadCompleteListener() {
							@Override
							public void onLoadComplete(SoundPool arg0,
													   int arg1, int arg2) {
								soundPool.play(soundID, volume, volume, 0, 0, 1f);
							}
						});
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	@SuppressLint("NewApi")
	public void switchCamera(SurfaceHolder mHolder) {
		if(mCamera != null)
		{
			mCamera.stopPreview();
			mCamera.release();
			if (currentCamera == CameraInfo.CAMERA_FACING_BACK) {
				currentCamera = CameraInfo.CAMERA_FACING_FRONT;
			} else {
				currentCamera = CameraInfo.CAMERA_FACING_BACK;
			}

			mCamera = Camera.open(currentCamera);

			try {
				mCamera.setPreviewDisplay(mHolder);
				cameraDisplayOrientation = CameraHelper.getCameraDisplayOrientation(
						(Activity)mContext, currentCamera);
				mCamera.setDisplayOrientation(cameraDisplayOrientation);
				
				mCamera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void lock() {
		if (mCamera != null) {
			mCamera.lock();
		}
	}

	public Camera getCameraInstance() {
		return mCamera;
	}

	public Camera.Size getResolutionSize() {
		return resolutionSize;
	}

	public int getCurrentCamera() {
		return currentCamera;
	}
	
	public int getCameraDisplayOrientation() {
		return cameraDisplayOrientation;
	}

}
