package com.orangecoder.videorecord.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import com.orangecoder.videorecord.camera.VideoStorageManager;
import com.orangecoder.videorecord.config.FilePathConfig;
import com.orangecoder.videorecord.model.LocalVideoData;
import com.orangecoder.videorecord.storage.db.LocalVideoDBManager;
import com.orangecoder.videorecord.storage.db.SystemVideoDataTableManager;
import com.orangecoder.videorecord.utils.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by xpmbp on 15/12/23.
 */
public abstract class BaseScanVideoFileTask implements Runnable {

    protected Context mContext;

    private Callback mCallback;
    public interface Callback {
        void finish();
    }

    public BaseScanVideoFileTask(Context context, Callback callback) {
        mContext = context;
        mCallback = callback;
    }

    @Override
    public void run() {
        try {
            scan();

            if(mCallback != null) {
                mCallback.finish();
            }
        } catch (Exception e) {

        }
    }

    protected abstract void scan();

    protected void insertVideoRecord(LocalVideoData localVideoData, LocalVideoDBManager db) {
        //生成时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy年MM月dd日");
        String date=sdf.format(new Date(Long.parseLong(localVideoData.getId())));
        sdf=new SimpleDateFormat("HH:mm");
        String time=sdf.format(new Date(Long.parseLong(localVideoData.getId())));

        //计算视频时长
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(localVideoData.getVideoLocalPath());
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        //完善视频数据
        localVideoData.setVideoCreateDate(date);
        localVideoData.setVideoCreateTime(time);
        localVideoData.setVideoLength(Integer.parseInt(duration) / 1000);
        localVideoData.setIsLooked(1);
        if(db instanceof SystemVideoDataTableManager) {
            localVideoData.setIsImport(1);
        } else {
            localVideoData.setIsImport(0);
        }

        db.insert(localVideoData);
    }

    protected boolean checkVideoLength(String videoPath) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        if(Integer.parseInt(duration)/1000 < 2) {
            File videoFile = new File(videoPath);
            if(videoFile.exists()) {
                videoFile.delete();
            }
            return false;
        }

        return true;
    }

    protected File getVideoThumbnail(File videoFile, String thumbnailFilename) {
        File thumbnailDir = FileUtil.getCacheDirectory(
                mContext, FilePathConfig.PATH_LOCALVIDEO_THUMBNAIL);
        File videoThumbnailFile = new File(thumbnailDir,
                thumbnailFilename+FilePathConfig.FILETYPE_THUMBNAIL);

        if(!videoThumbnailFile.exists()) {
            Bitmap bitmap = VideoStorageManager.createVideoThumbnail(videoFile, 0);
            if(bitmap == null)
            {
                //创建缩略图失败，可能是视频有问题，删掉视频文件
                if(videoFile.exists()) {
                    videoFile.delete();
                }
                return null;
            }
            VideoStorageManager.saveBitmap(videoThumbnailFile.getAbsolutePath(), bitmap);
        }

        return videoThumbnailFile;
    }

}
