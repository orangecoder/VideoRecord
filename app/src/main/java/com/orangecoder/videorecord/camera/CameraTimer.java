package com.orangecoder.videorecord.camera;

import java.util.Timer;
import java.util.TimerTask;

public class CameraTimer {

	private int totalSmallSecond = 0; // 总共录制时间 (=100分之一秒)
	private Timer timer = new Timer(true);
	private TimerTask task;
	
	private CameraTimerCallback mCallback;
	public interface CameraTimerCallback{
		void updateTime(int totalSmallSecond, boolean isShowPoint);
	}
	
	public void startTimer(CameraTimerCallback callback)
	{
		mCallback = callback;
		totalSmallSecond = 0;
		task = new TimerTask() {
			boolean isShowPoint = false;
			int showPointTime = 0;

			public void run() {
				totalSmallSecond++;

				if (totalSmallSecond > 3000) {
					return;
				}
				
				if (showPointTime < totalSmallSecond / 100) {
					isShowPoint = true;
					showPointTime = totalSmallSecond / 100;
				} else {
					isShowPoint = false;
				}

				mCallback.updateTime(totalSmallSecond, isShowPoint);
			}
		};
		timer.schedule(task, 0, 10);
	}
	
	public void stopTimer(){
		if (task != null) {
			task.cancel();
			task = null;
			mCallback = null;
		}
	}
	
	public static String gettime(int smallSecond) {
		StringBuilder sb = new StringBuilder();
		if (smallSecond > 99) {
			int m = smallSecond / 100;
			if(m > 9)
			{
				sb.append(m).append(":");
			}else {
				sb.append("0").append(m).append(":");
			}
			int s = smallSecond % 100;
			if (s > 9) {
				sb.append(s);
			} else {
				sb.append("0").append(s);
			}
		} else {
			sb.append("00").append(":");
			if (smallSecond > 9) {
				sb.append(smallSecond);
			} else {
				sb.append("0").append(smallSecond);
			}
		}
		return sb.toString();
	}
}
