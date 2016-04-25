package com.orangecoder.videorecord.storage;

import android.content.Context;

import com.orangecoder.videorecord.config.FilePathConfig;
import com.orangecoder.videorecord.model.LocalVideoData;
import com.orangecoder.videorecord.storage.db.LocalVideoDataTableManager;
import com.orangecoder.videorecord.utils.FileUtil;

import java.io.File;
import java.util.List;


/**
 * Created by xpmbp on 15/12/23.
 */
public class ScanMyCaptureVideoFileTask extends BaseScanVideoFileTask {

    public ScanMyCaptureVideoFileTask(Context context, Callback callback) {
        super(context, callback);
    }

    @Override
    protected void scan() {
        LocalVideoDataTableManager db = new LocalVideoDataTableManager(mContext);
        List<File> localvideo = FileUtil.getAllFilesByFormat(mContext,
                FilePathConfig.PATH_LOCALVIDEO, FilePathConfig.FILETYPE_VIDEO);

        System.out.println("localvideo num:"+localvideo.size());

        if(localvideo!=null && localvideo.size()>0)
        {
            for (File videoFile : localvideo) {

                //获取文件名
                String filename = getFileName(videoFile);
                if(filename == null) {
                    continue;
                }

                List<LocalVideoData> videos = db.query(filename);
                //数据库没有记录，补上
                if(videos==null || videos.size()==0)
                {
                    //判断视频缩略图是否存在
                    File videoThumbnailFile = getVideoThumbnail(videoFile, filename);
                    if(videoThumbnailFile==null || !videoThumbnailFile.exists()) {
                        continue;
                    }

                    LocalVideoData localVideoData = new LocalVideoData();
                    localVideoData.setId(filename);
                    localVideoData.setName(filename);
                    localVideoData.setVideoImgPath(videoThumbnailFile.getAbsolutePath());
                    localVideoData.setVideoLocalPath(videoFile.getAbsolutePath());

                    insertVideoRecord(localVideoData, db);
                }
            }
        }
    }

    private String getFileName(File videoFile) {
        String filename = videoFile.getName();
        if(!filename.contains(FilePathConfig.FILETYPE_VIDEO))
        {
            return null;
        }
        filename = filename.substring(0, filename.indexOf(FilePathConfig.FILETYPE_VIDEO));

        //如果文件名是时间戳，说明是golfpad拍摄的视频
        try {
            Long.parseLong(filename);
            if(filename.length() != 13)
            {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }

        return filename;
    }

}
