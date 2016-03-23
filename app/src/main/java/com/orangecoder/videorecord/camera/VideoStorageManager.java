package com.orangecoder.videorecord.camera;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import com.orangecoder.videorecord.utils.AppImageUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



public class VideoStorageManager {
	private static final String TAG = "VideoStorageManager";

	public static int getVideoDuration(File videoFile){
		MediaMetadataRetriever retriever = new MediaMetadataRetriever();
		retriever.setDataSource(videoFile.getAbsolutePath());
		String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
		if(hasVideo == null)
		{
			videoFile.delete();
			return 0;
		}

		String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
		return Integer.parseInt(duration)/1000;
	}

	public static Bitmap createVideoThumbnail(File videoFile, int position) {
        Bitmap bitmap = null;
        try {
        	MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            Log.e(TAG, "hasVideo:"+hasVideo);
            if(hasVideo == null)
            {
            	videoFile.delete();
            	return null;
            }
            
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int attime = Integer.parseInt(duration)/2;
            if(position > 0)
            {
            	attime = position;
            }
            bitmap = retriever.getFrameAtTime(attime*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);;
           
            retriever.release();
            retriever = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return bitmap;
    }
	
	public static Bitmap createVideoThumbnailDetail(File videoFile, int positon) {
        Bitmap bitmap = null;
        try {
        	MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(videoFile.getAbsolutePath());
            String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
            Log.e(TAG, "hasVideo:"+hasVideo);
            if(hasVideo == null)
            {
            	videoFile.delete();
            	return null;
            }
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int duration = Integer.parseInt(durationStr);
            if(positon <= 0)
            {
                int attime = (int) (duration*0.2);
                Bitmap bitmap1 = retriever.getFrameAtTime(attime*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap1 = comp(bitmap1, 640/3/2f, 400/2f);
                bitmap1 = AppImageUtil.setupFrame(bitmap1, 1, 0xffcccccc);
                attime = (int) (duration*0.4);
                Bitmap bitmap2 = retriever.getFrameAtTime(attime*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap2 = comp(bitmap2, 640/3/2f, 400/2f);
                bitmap2 = AppImageUtil.setupFrame(bitmap2, 1, 0xffcccccc);
                attime = (int) (duration*0.6);
                Bitmap bitmap3 = retriever.getFrameAtTime(attime*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap3 = comp(bitmap3, 640/3/2f, 400/2f);
                bitmap3 = AppImageUtil.setupFrame(bitmap3, 1, 0xffcccccc);
                bitmap = add2Bitmap(bitmap1, bitmap2);
                bitmap = add2Bitmap(bitmap, bitmap3);
            }else {
            	int firstposition = positon/2;
            	int secondposition = positon;
            	int thirdposition = positon + ((duration - positon)/2);

            	Log.e("duration", String.valueOf(duration));
            	Log.e("firstposition", String.valueOf(firstposition));
            	Log.e("secondposition", String.valueOf(secondposition));
            	Log.e("thirdposition", String.valueOf(thirdposition));

            	Bitmap bitmap1 = retriever.getFrameAtTime(firstposition*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap1 = comp(bitmap1, 640/3/2f, 400/2f);
                bitmap1 = AppImageUtil.setupFrame(bitmap1, 1, Color.WHITE);
                Bitmap bitmap2 = retriever.getFrameAtTime(secondposition*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap2 = comp(bitmap2, 640/3/2f, 400/2f);
                bitmap2 = AppImageUtil.setupFrame(bitmap2, 1, Color.WHITE);
                Bitmap bitmap3 = retriever.getFrameAtTime(thirdposition*1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap3 = comp(bitmap3, 640/3/2f, 400/2f);
                bitmap3 = AppImageUtil.setupFrame(bitmap3, 1, Color.WHITE);
                bitmap = add2Bitmap(bitmap1, bitmap2);
                bitmap = add2Bitmap(bitmap, bitmap3);
			}

            retriever.release();
            retriever = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return bitmap;
    }

	private static Bitmap add2Bitmap(Bitmap first, Bitmap second) {
        int width =first.getWidth() + second.getWidth();
        int height = Math.max(first.getHeight(), second.getHeight());
		Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		canvas.drawBitmap(first, 0, 0, null);
		canvas.drawBitmap(second, first.getWidth(), 0, null);
		return result;
	}
	
	private static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

	public static Bitmap comp(Bitmap image, float cw, float ch) {
	      
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();         
	    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
	    if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
	        baos.reset();//重置baos即清空baos  
	        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
	    }  
	    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();  
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;  
	    Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    newOpts.inJustDecodeBounds = false;  
	    int w = newOpts.outWidth;  
	    int h = newOpts.outHeight;  
	    //现在主流手机比较多是800*480分辨率，所以高和宽我们设置为  
	    float hh = ch;//这里设置高度为400f  
	    float ww = cw;//这里设置宽度为640f  
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;//be=1表示不缩放  
	    if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0)  
	        be = 1;  
	    newOpts.inSampleSize = be;//设置缩放比例  
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    isBm = new ByteArrayInputStream(baos.toByteArray());  
	    bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
	    return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
	}  
	
	public static void saveBitmap(String filePath, Bitmap mBitmap){
		  File f = new File(filePath);
		  FileOutputStream fOut = null;
		  try {
			  fOut = new FileOutputStream(f);
		  } catch (FileNotFoundException e) {
			  e.printStackTrace();
		  }
		  mBitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
		  try {
			  fOut.flush();
			  fOut.close();
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	 }
}
