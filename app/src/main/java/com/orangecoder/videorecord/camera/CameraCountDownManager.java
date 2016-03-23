package com.orangecoder.videorecord.camera;

import com.orangecoder.videorecord.R;

import java.util.Timer;
import java.util.TimerTask;


public class CameraCountDownManager {

	private int mTotalSecond = 0; // 总倒计时时间
	private Timer timer = new Timer(true);
	private TimerTask task;
	private Integer[] countdownNumResource = new Integer[]{
			R.mipmap.ic_camera_num1, R.mipmap.ic_camera_num2,
			R.mipmap.ic_camera_num3, R.mipmap.ic_camera_num4,
			R.mipmap.ic_camera_num5, R.mipmap.ic_camera_num6,
			R.mipmap.ic_camera_num7, R.mipmap.ic_camera_num8,
			R.mipmap.ic_camera_num9, R.mipmap.ic_camera_num10,
	};
	
	private CameraCountDownCallback mCallback;
	public interface CameraCountDownCallback {
		void onUpdateTime(int leftSecondImgRes);
		void onCountDownFinish();
	}
	
	public void startTimer(int totalTime, CameraCountDownCallback callback) {
		mCallback = callback;
		mTotalSecond = totalTime;
		task = new TimerTask() {

			public void run() {
				if(mTotalSecond <= 0)
				{
					mCallback.onCountDownFinish();
					stopTimer();
					return;
				}else {
					mTotalSecond--;
					mCallback.onUpdateTime(countdownNumResource[mTotalSecond]);
				}
			}
		};
		timer.schedule(task, 0, 1000);
	}
	
	public void stopTimer() {
		if (task != null) {
			task.cancel();
			task = null;
			mCallback = null;
		}
	}
}
