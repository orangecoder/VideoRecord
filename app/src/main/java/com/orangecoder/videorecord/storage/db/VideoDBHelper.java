package com.orangecoder.videorecord.storage.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.orangecoder.videorecord.model.LocalVideoData;


public class VideoDBHelper extends SQLiteOpenHelper{

    public static final String DBNAME = "video.db";	// 数据库名称  
    public static final int VERSION = 5;  // 数据库版本  
    
    public static final String TN_LOCALVIDEO = "localvideo";  //我拍摄的视频
    public static final String TN_SYS_VIDEO = "sysvideo";  //手机相册的视频
    public static final String TN_LOOKED = "looked";  //我看过的视频
    
    // 建表语句，大小写不敏感  
    private static final String CREATETABLE_MYCAPURE = "create table " + TN_LOCALVIDEO + "("+ 
            LocalVideoData.KEY_ID +" string, " +
            LocalVideoData.KEY_NAME +" string, " +
            LocalVideoData.KEY_VIDEO_IMGPATH + " string, " +
            LocalVideoData.KEY_VIDEO_DETAILIMGPATH + " string, " +
            LocalVideoData.KEY_VIDEO_LOCALPATH + " string, " +
            LocalVideoData.KEY_VIDEO_CREATEDATE + " string, " +
            LocalVideoData.KEY_VIDEO_CREATETIME + " string, " +
            LocalVideoData.KEY_VIDEO_LENGTH + " int, " +
            LocalVideoData.KEY_ISUPLOAD + " int, " +
            LocalVideoData.KEY_ISLOOKED + " int, " +
			LocalVideoData.KEY_POST_ID + " string, " +    //增加的上传成功后返回的post_id
			LocalVideoData.KEY_PLAY_URL + " string, " +    //增加的上传成功后返回的post_id
			LocalVideoData.KEY_SOURCE_URL+ " string, " +    //增加的上传成功后返回的post_id
            LocalVideoData.KEY_ISIMPORT + " int)";
    public static final String CREATETABLE_SYS_VIDEO = "create table " + TN_SYS_VIDEO + "("+ 
            LocalVideoData.KEY_ID +" string, " +
            LocalVideoData.KEY_NAME +" string, " +
            LocalVideoData.KEY_VIDEO_IMGPATH + " string, " +
            LocalVideoData.KEY_VIDEO_DETAILIMGPATH + " string, " +
            LocalVideoData.KEY_VIDEO_LOCALPATH + " string, " +
            LocalVideoData.KEY_VIDEO_CREATEDATE + " string, " +
            LocalVideoData.KEY_VIDEO_CREATETIME + " string, " +
            LocalVideoData.KEY_VIDEO_LENGTH + " int, " +
            LocalVideoData.KEY_ISUPLOAD + " int, " +
            LocalVideoData.KEY_ISLOOKED + " int, " +
			LocalVideoData.KEY_POST_ID + " string, " +    //增加的上传成功后返回的post_id
			LocalVideoData.KEY_PLAY_URL + " string, " +    //增加的上传成功后返回的post_id
			LocalVideoData.KEY_SOURCE_URL+ " string, " +    //增加的上传成功后返回的post_id
            LocalVideoData.KEY_ISIMPORT + " int)";
    private static final String CREATETABLE_LOOKED = "create table " + TN_LOOKED +
            "(id string, " +
            "lookedDate string, " +
            "videoThumbnail string, " +
            "videoId int)";
  
    public VideoDBHelper(Context context) {  
        super(context, DBNAME, null, VERSION);  
    }  
  
    // 创建表  
    @Override  
    public void onCreate(SQLiteDatabase db) {  
        db.execSQL(CREATETABLE_MYCAPURE);  
        db.execSQL(CREATETABLE_SYS_VIDEO);
        db.execSQL(CREATETABLE_LOOKED);  
    }  
  
    // 更新表  
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  
        this.deleteDB(db);  
        this.onCreate(db);  
    }  
  
    // 删除表  
    private void deleteDB(SQLiteDatabase db) {  
        db.execSQL("drop table if exists " + TN_LOCALVIDEO);  
        db.execSQL("drop table if exists " + TN_SYS_VIDEO);
        db.execSQL("drop table if exists " + TN_LOOKED);
    }  
}
