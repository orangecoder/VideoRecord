package com.orangecoder.videorecord.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class AppImageUtil {

	/*public static String cacheGaussianBlurCrop(Activity activity, String name) {
		RenderScript rs = RenderScript.create(activity.getApplicationContext());
		Bitmap bmp = getCrop(activity);
		final Allocation input = Allocation.createFromBitmap(rs, bmp);
		final Allocation output = Allocation.createTyped(rs, input.getType());
		final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));

		script.setRadius(16f);
		script.setInput(input);
		script.forEach(output);
		output.copyTo(bmp);

		File file = new File(activity.getFilesDir(), name);
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Log.e("golfpad", e.getMessage());
		}
		bmp.compress(CompressFormat.JPEG, 50, fileOutput);

		script.destroy();
		return file.getAbsolutePath();
	}

	public static Bitmap gaussianBlurImage(Context context, Bitmap bitmap, float radius) {
		if (context == null || bitmap == null) {
			return bitmap;
		}

		RenderScript rs = RenderScript.create(context);
		final Allocation input = Allocation.createFromBitmap(rs, bitmap);
		final Allocation output = Allocation.createTyped(rs, input.getType());
		final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

		script.setRadius(radius);
		script.setInput(input);
		script.forEach(output);
		output.copyTo(bitmap);

		return bitmap;
	}*/

	public static String getCrop(Activity activity, String name) {
		Bitmap bmp = getCrop(activity);
		File file = new File(activity.getFilesDir(), name);
		FileOutputStream fileOutput = null;
		try {
			fileOutput = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			Log.e("golfpad", e.getMessage());
		}
		bmp.compress(CompressFormat.JPEG, 50, fileOutput);
		return file.getAbsolutePath();
	}

	public static Bitmap getCrop(Activity activity) {
		View view = activity.getWindow().getDecorView();
		view.buildDrawingCache();

		Rect rect = new Rect();
		view.getWindowVisibleDisplayFrame(rect);
		int stateBarHeight = rect.top;

		view.setDrawingCacheEnabled(true);

		Bitmap bmpCache = view.getDrawingCache();
		Bitmap bmp = Bitmap.createBitmap(bmpCache, 0, stateBarHeight,
				bmpCache.getWidth(), bmpCache.getHeight() - stateBarHeight);

		view.destroyDrawingCache();

		return bmp;
	}

	public static Bitmap setupFrame(Bitmap bitmap, int width, int color) {
		if (bitmap.getWidth() <= width * 2 || bitmap.getHeight() <= width * 2) {
			return bitmap;
		}

		Bitmap bp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);

		Canvas canvas = new Canvas(bp);
		canvas.drawBitmap(bitmap, 0, 0, new Paint());
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(width);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, canvas.getWidth() - width,
				canvas.getHeight() - width, paint);

		bitmap.recycle();
		return bp;
	}

}
