package com.orangecoder.videorecord.storage;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.orangecoder.videorecord.model.LocalVideoData;
import com.orangecoder.videorecord.storage.db.SystemVideoDataTableManager;
import com.orangecoder.videorecord.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ScanSystemVideoFileTask extends BaseScanVideoFileTask{

	String[] thumbColumns = new String[]{
			MediaStore.Video.Thumbnails.DATA,
			MediaStore.Video.Thumbnails.VIDEO_ID
	};

	String[] mediaColumns = new String[]{
			MediaStore.Video.Media._ID,
			MediaStore.Video.Media.MIME_TYPE,
			MediaStore.Video.Media.TITLE,
			MediaStore.Video.Media.DATA,
			MediaStore.Video.Media.DURATION,
			MediaStore.Video.Media.DATE_ADDED,
	};

	public ScanSystemVideoFileTask(Context context) {
		super(context);
	}

	@Override
	protected void scan() {
		List<SystemVideo> systemVideos = getSystemVideos(mContext);

		if(systemVideos!=null && systemVideos.size()>0) {
			SystemVideoDataTableManager db = new SystemVideoDataTableManager(mContext);
			db.deleteAllTableData();

			for (SystemVideo video : systemVideos) {
				LocalVideoData localVideoData = new LocalVideoData();
				localVideoData.setId(video.addTime);
				localVideoData.setName(video.fileName);
				localVideoData.setVideoImgPath(video.thumbnailPath);
				localVideoData.setVideoLocalPath(video.filePath);

				insertVideoRecord(localVideoData, db);

			}
		}
	}

	class SystemVideo {
		String fileName;
		String filePath;
		String thumbnailPath;
		String duration;
		String addTime;
	}

	private List<SystemVideo> getSystemVideos(Context context) {
		//获取本地视频
        Cursor cursor = ((Activity) context).managedQuery(
        		MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 
        		mediaColumns,
				null,
				null,
				null);
          
        ArrayList<SystemVideo> videoList = new ArrayList<SystemVideo>();  
        String mimeType;
        String filepath;

        if(cursor.moveToFirst()){  
            do{  
            	mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
            	filepath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));  
            	if(mimeType.equals("video/mp4") && !filepath.contains("golfpad"))
            	{
            		SystemVideo info = new SystemVideo();

					//判断文件是否存在
            		info.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));  
            		if(!FileUtil.isFileExist(info.filePath)) {
						continue;
					}

	            	info.fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
	                info.duration = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
	                info.addTime = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED))+"000";  

					//小于2秒的视频不导入
					int videoLength = Integer.parseInt(info.duration) / 1000;
					if(videoLength < 2)
					{
						continue;
					}

					//获取视频缩略图
					int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
					File videoThumbnailFile = getThumbnail(id, info.filePath, info.fileName);
					if(videoThumbnailFile == null) {
						continue;
					}
					info.thumbnailPath = videoThumbnailFile.getAbsolutePath();

	                videoList.add(info);
            	}
            }while(cursor.moveToNext());  
        }  
	          
		return videoList;
	}

	private File getThumbnail(int id, String videoFilePath, String thumbnailFileName) {
		String selection = MediaStore.Video.Thumbnails.VIDEO_ID +"=?";
		String[] selectionArgs = new String[]{
				id+""
		};

		Cursor thumbCursor = ((Activity) mContext).managedQuery(
				MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
				thumbColumns,
				selection,
				selectionArgs,
				null);

		//优先从系统拿
		if(thumbCursor.moveToFirst()){
			String thumbnailPath = thumbCursor.getString(
					thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));
			if(FileUtil.isFileExist(thumbnailPath)) {
				return new File(thumbnailPath);
			}
		}

		//重新生成一张
		File videoThumbnailFile = getVideoThumbnail(new File(videoFilePath), thumbnailFileName);
		if(videoThumbnailFile == null) {
			return null;
		}
		return videoThumbnailFile;
	}

}
