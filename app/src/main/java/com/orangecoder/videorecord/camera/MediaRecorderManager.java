package com.orangecoder.videorecord.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.orangecoder.videorecord.config.FilePathConfig;
import com.orangecoder.videorecord.model.LocalVideoData;
import com.orangecoder.videorecord.storage.db.LocalVideoDataTableManager;
import com.orangecoder.videorecord.utils.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;



public class MediaRecorderManager {
	private static final String TAG = "MediaRecorderManager";

	private Context mContext;
	private String videoName; // 视频名称
	private File videoFile; // 视频文件
	private MediaRecorder videoRecorder;
	
	private MediaRecorderManagerCallback mCallback;
	public interface MediaRecorderManagerCallback {
		void onStartRecorder();
		void onStopRecorder(LocalVideoData localVideoData);
	}
	
	public MediaRecorderManager(Context context, MediaRecorderManagerCallback callback) {
		mContext = context;
		mCallback = callback;
	}
	
	public void startRecord(final MyCameraManager cameraManager, final SurfaceHolder mHolder) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					if(!initRecorder(cameraManager, mHolder)) {
						return;
					}
					videoRecorder.start();
					mCallback.onStartRecorder();
				} catch (Exception e) {
					releaseMediaRecorder();
					Log.e(TAG, e.toString());
				}
			}
		}).start();
	}
	public void stopRecord() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (videoRecorder != null) {
					videoRecorder.stop();
					releaseMediaRecorder();
				}
				LocalVideoData localVideoData = saveRecordData();
				mCallback.onStopRecorder(localVideoData);
			}
		}).start();
	}

	private LocalVideoData saveRecordData() {
		// 给点时间保存录制的视频
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// 生成缩略图
		Bitmap bitmap = VideoStorageManager.createVideoThumbnail(videoFile, 0);
		if (bitmap == null) {
			// 创建缩略图失败，可能是视频有问题，删掉视频文件
			if (videoFile.exists()) {
				videoFile.delete();
			}
			return null;
		}

		File thumbnailDir = FileUtil.getCacheDirectory(
				mContext, FilePathConfig.PATH_LOCALVIDEO_THUMBNAIL);
		final File videoThumbnailFile = new File(thumbnailDir, videoName + ".jpg");
		VideoStorageManager.saveBitmap(videoThumbnailFile.getAbsolutePath(), bitmap);

		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		String date = sdf.format(new Date(Long.parseLong(videoName)));
		sdf = new SimpleDateFormat("HH:mm");
		String time = sdf.format(new Date(Long.parseLong(videoName)));

		// 保存到数据库
		LocalVideoDataTableManager db = new LocalVideoDataTableManager(mContext);
		final LocalVideoData video = new LocalVideoData();
		video.setId(videoName);
		video.setName(videoName);
		if (bitmap != null) {
			video.setVideoImgPath(videoThumbnailFile.getAbsolutePath());
		}
		video.setVideoLocalPath(videoFile.getAbsolutePath());
		video.setVideoCreateDate(date);
		video.setVideoCreateTime(time);
		video.setVideoLength(VideoStorageManager.getVideoDuration(videoFile));
		video.setIsLooked(0);
		db.insert(video);
		return video;
	}
	
	private boolean initRecorder(MyCameraManager cameraManager, SurfaceHolder mHolder) {
		if(cameraManager.getCameraInstance() == null) {
			return false;
		}
		if (videoRecorder == null) {
			videoRecorder = new MediaRecorder();
		}
		// 生成视频文件存放路径
		videoName = String.valueOf(System.currentTimeMillis());
		File videoDir = FileUtil.getCacheDirectory(mContext, FilePathConfig.PATH_LOCALVIDEO);
		videoFile = new File(videoDir, videoName + FilePathConfig.FILETYPE_VIDEO);

		// Step 1: Unlock and set camera to MediaRecorder
		// mCamera.stopPreview();
		cameraManager.getCameraInstance().unlock();
		videoRecorder.setCamera(cameraManager.getCameraInstance());

		// Step 2: Set sources
		videoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
		videoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

		// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
		setOptimalProfile(cameraManager);

		// Step 4: Set output file
		videoRecorder.setOutputFile(videoFile.getAbsolutePath());

		// Step 5: Set the preview output
		// videoRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
		videoRecorder.setPreviewDisplay(mHolder.getSurface());

		// Step 6: Prepare configured MediaRecorder
		if (cameraManager.getCurrentCamera() == Camera.CameraInfo.CAMERA_FACING_BACK) {
			videoRecorder.setOrientationHint(cameraManager.getCameraDisplayOrientation());
		} else {
			videoRecorder.setOrientationHint(270);
		}
		if(cameraManager.getResolutionSize() != null) {
			videoRecorder.setVideoSize(cameraManager.getResolutionSize().width,
					cameraManager.getResolutionSize().height);
		}
		
		try {
			videoRecorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	private void setOptimalProfile(MyCameraManager cameraManager) {
		if(cameraManager.getResolutionSize() != null)
		{
			int height = cameraManager.getResolutionSize().height;
			
			if(height == 720) {
				if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
					videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_720P));
					Log.e(TAG, "setProfile:QUALITY_720P");
					return;
				}
			}
			
			if(height == 480) {
				if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_480P)) {
					videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
					Log.e(TAG, "setProfile:QUALITY_480P");
					return;
				}
			}
		}
		
		if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
			videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			Log.e(TAG, "setProfile:QUALITY_HIGH");
			return;
		}
		if(CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW)) {
			videoRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
			Log.e(TAG, "setProfile:QUALITY_LOW");
			return;
		}
	}

	private void releaseMediaRecorder() {
		Log.e(TAG, "releaseMediaRecorder()");
		if (videoRecorder != null) {
			videoRecorder.reset();
			videoRecorder.release();
			videoRecorder = null;
		}
	}
}
