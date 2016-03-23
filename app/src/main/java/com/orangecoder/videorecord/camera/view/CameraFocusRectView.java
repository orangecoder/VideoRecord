package com.orangecoder.videorecord.camera.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.View;

import com.orangecoder.videorecord.R;


public class CameraFocusRectView extends View {

	private final static int STATE_ORIGIN = 0;
	private final static int STATE_FOCUSING = 1;
	private final static int STATE_SECOND_FOCUSING = 2;

	private int mState = 0;

	private int localX =0, localY=0;

	private Bitmap mIcon = null;
	private int mIconWeight = 50;
	private CountDownTimer mTimer = null;

	public CameraFocusRectView(Context context) {
		super(context);
		init();
	}

	public CameraFocusRectView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CameraFocusRectView(Context context,
							   AttributeSet attrs,
							   int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		mIcon = BitmapFactory.decodeResource(getResources(),
				R.mipmap.icon_focus_rect);
		mIconWeight = (mIcon.getWidth() + mIcon.getHeight()) * 3 / 16;
	}

	public void show (int x, int y, int length) {
		cancelTimer();
		mIconWeight = length/2;
		localX = x;
		localY = y;
		mState = STATE_FOCUSING;
		invalidate();
	}

	public void hide () {
		mState = STATE_ORIGIN;
		invalidate();
	}

	@Override
	public void draw(Canvas canvas) {
		switch (mState) {
		case STATE_FOCUSING:
			putIcon(canvas, localX, localY);
			break;
		case STATE_SECOND_FOCUSING:
			putSecondIcon(canvas, localX, localY);
			break;
		default:
			break;
		}
		super.draw(canvas);
	}

	private void putIcon(Canvas canvas, int x, int y) {
		Paint paint = new Paint();
		canvas.drawBitmap(mIcon, null, new Rect(x - mIconWeight, y
				- mIconWeight, x + mIconWeight, y + mIconWeight), paint);
		startTimer();
	}

	private void putSecondIcon(Canvas canvas, int x, int y) {
		Paint paint = new Paint();
		paint.setAlpha(50);
		canvas.drawBitmap(mIcon, null, new Rect(x - mIconWeight, y
				- mIconWeight, x + mIconWeight, y + mIconWeight), paint);
	}

	private void setSecondIcon() {
		mState = STATE_SECOND_FOCUSING;
		invalidate();
	}

	private void clearIcon() {
		mState = STATE_ORIGIN;
		invalidate();
	}

	private void startTimer() {
		cancelTimer();
		if (mTimer == null) {
			mTimer = new CountDownTimer(2000, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					if ((int) (millisUntilFinished / 1000) == 1) {
						setSecondIcon();
					}
				}

				@Override
				public void onFinish() {
					clearIcon();
				}
			};
		}
		mTimer.start();
	}

	private void cancelTimer() {
		if (mTimer != null) {
			mTimer.cancel();
		}
	}

}
