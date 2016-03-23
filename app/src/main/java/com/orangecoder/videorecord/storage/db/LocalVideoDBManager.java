package com.orangecoder.videorecord.storage.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.orangecoder.videorecord.model.LocalVideoData;

import java.util.ArrayList;
import java.util.List;



public class LocalVideoDBManager {
	private static final String TAG = "LocalVideoDBManager";
	private VideoDBHelper dbHelper;
	private String mTableName;

	public LocalVideoDBManager(Context context, String tablename) {
		dbHelper = new VideoDBHelper(context);
		mTableName = tablename;
	}

	// 插入记录
	public int insert(LocalVideoData video) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("insert into " + mTableName
					+ " values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					new Object[] {
						video.getId(), 
						video.getName(), 
						video.getVideoImgPath(),
						video.getVideoDetailImgPath(), 
						video.getVideoLocalPath(),
						video.getVideoCreateDate(), 
						video.getVideoCreateTime(),
						video.getVideoLength(), 
						video.getIsUpload(), 
						video.getIsLooked(),
						"",
						"",
						"", 
						video.getIsImport()});
			db.setTransactionSuccessful();
		} catch (Exception e) {
			return 0;
		} finally {
			db.endTransaction();
		}
		db.close();
		return 1;
	}

	// 删除记录
	public int delete(LocalVideoData video) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("delete from " + mTableName
					+ " where "+LocalVideoData.KEY_ID+" = ?",
					new Object[] { video.getId() });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			return 0;
		} finally {
			db.endTransaction();
		}
		db.close();
		return 1;
	}

	public int deleteAllTableData()
	{
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("drop table if exists " + VideoDBHelper.TN_SYS_VIDEO);
			db.execSQL(VideoDBHelper.CREATETABLE_SYS_VIDEO);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			return 0;
		} finally {
			db.endTransaction();
		}
		db.close();
		return 1;
	}
	
	// 更新记录
	public int update(LocalVideoData video) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL(
					"update " + mTableName + " set "+ 
							LocalVideoData.KEY_NAME+"=?, " + 
							LocalVideoData.KEY_VIDEO_IMGPATH+"=?, "+ 
							LocalVideoData.KEY_VIDEO_DETAILIMGPATH+"=?, "+ 
							LocalVideoData.KEY_VIDEO_LOCALPATH+"=?, "+ 
							LocalVideoData.KEY_VIDEO_CREATEDATE+"=?, "+ 
							LocalVideoData.KEY_VIDEO_CREATETIME+"=?, "+ 
							LocalVideoData.KEY_VIDEO_LENGTH+"=?, "+ 
							LocalVideoData.KEY_POST_ID+"=?, "+ 
							LocalVideoData.KEY_PLAY_URL+"=?, "+ 
							LocalVideoData.KEY_SOURCE_URL+"=?, "+ 
							LocalVideoData.KEY_ISUPLOAD+"=?"+
							" where "+LocalVideoData.KEY_ID+"=?",
					new Object[] { 
							video.getName(), 
							video.getVideoImgPath(),
							video.getVideoDetailImgPath(),
							video.getVideoLocalPath(),
							video.getVideoCreateDate(),
							video.getVideoCreateTime(), 
							video.getVideoLength(),
							video.getPost_id(),
							video.getPlay_url(),
							video.getSource_url(),
							video.getIsUpload(), 
							video.getId() });
			db.setTransactionSuccessful();
		} catch (Exception e) {
			return 0;
		} finally {
			db.endTransaction();
		}
		db.close();
		return 1;
	}

	public int update(String sql, Object[] bindArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL(sql, bindArgs);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			return 0;
		} finally {
			db.endTransaction();
		}
		db.close();
		return 1;
	}

	public boolean updateLookedState(String id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LocalVideoData.KEY_ISLOOKED, 1);//key为字段名，value为值
		int num = db.update(
				mTableName, 
				values, 
				LocalVideoData.KEY_ID+"=?", 
				new String[]{id}); 
		db.close();
		if(num > 0)
		{
			return true;
		}else {
			return false;
		}
	}
	
	// 查询记录
	public ArrayList<LocalVideoData> query(String name) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor;
		ArrayList<LocalVideoData> list = new ArrayList<LocalVideoData>();
		// 若fileId为null或""则查询所有记录
		if (name == null || name.equals("")) {
			cursor = db.rawQuery(
					"select * from " + mTableName, 
					null);
		} else {
			cursor = db.rawQuery(
					"select * from " + mTableName + 
					" where " + LocalVideoData.KEY_NAME + "=?",
					 new String[] { name });
		}
		while (cursor.moveToNext()) {
			LocalVideoData video = new LocalVideoData();
			video.setId(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_ID)));
			video.setName(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_NAME)));
			video.setVideoImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_IMGPATH)));
			video.setVideoDetailImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_DETAILIMGPATH)));
			video.setVideoLocalPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LOCALPATH)));
			video.setVideoCreateDate(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATEDATE)));
			video.setVideoCreateTime(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATETIME)));
			video.setVideoLength(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LENGTH)));
			video.setIsUpload(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISUPLOAD)));
			video.setIsLooked(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISLOOKED)));
			video.setIsImport(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISIMPORT)));
			list.add(video);
		}
		cursor.close();
		db.close();
		return list;
	}
	// 通过post_id查询记录
	public LocalVideoData queryByPost_id(String post_id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor;
		// 若fileId为null或""则查询所有记录
		if (post_id == null || post_id.equals("")) {
			return null;
		} else {
			cursor = db.rawQuery(
					"select * from " + mTableName + " where post_id=?",
					 new String[] { post_id });
		}
		LocalVideoData video=null;
		while (cursor.moveToNext()) {
			video = new LocalVideoData();
			video.setId(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_ID)));
			video.setName(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_NAME)));
			video.setVideoImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_IMGPATH)));
			video.setVideoDetailImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_DETAILIMGPATH)));
			video.setVideoLocalPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LOCALPATH)));
			video.setVideoCreateDate(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATEDATE)));
			video.setVideoCreateTime(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATETIME)));
			video.setVideoLength(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LENGTH)));
			video.setIsUpload(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISUPLOAD)));
			video.setIsLooked(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISLOOKED)));
			video.setIsImport(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISIMPORT)));
			video.setPost_id(String.valueOf(cursor.getInt(cursor.getColumnIndex(LocalVideoData.KEY_POST_ID))));
			video.setPlay_url(cursor.getString(cursor.getColumnIndex(LocalVideoData.KEY_PLAY_URL)));
			video.setSource_url(cursor.getString(cursor.getColumnIndex(LocalVideoData.KEY_SOURCE_URL)));
		}
		cursor.close();
		db.close();
		return video;
	}

	// 查询创建时间列表
	public List<String> queryDate() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<String> list = new ArrayList<String>();
		Cursor cursor = db.rawQuery(
				"select distinct " + LocalVideoData.KEY_VIDEO_CREATEDATE +
				" from " + mTableName +
				" order by " + LocalVideoData.KEY_VIDEO_CREATEDATE+" desc",
				 null);
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATEDATE)));
		}
		cursor.close();
		db.close();
		return list;
	}

	// 按创建时间查询
	public ArrayList<LocalVideoData> queryByTime(String data) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ArrayList<LocalVideoData> list = new ArrayList<LocalVideoData>();
		Cursor cursor = db.rawQuery(
				"select * from " + mTableName + 
				" where " + LocalVideoData.KEY_VIDEO_CREATEDATE+"=?" + 
				" order by " + LocalVideoData.KEY_ID+" desc",
				new String[] { data });
		while (cursor.moveToNext()) {
			LocalVideoData video = new LocalVideoData();
			video.setId(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_ID)));
			video.setName(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_NAME)));
			video.setVideoImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_IMGPATH)));
			video.setVideoDetailImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_DETAILIMGPATH)));
			video.setVideoLocalPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LOCALPATH)));
			video.setVideoCreateDate(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATEDATE)));
			video.setVideoCreateTime(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATETIME)));
			video.setVideoLength(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LENGTH)));
			video.setIsUpload(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISUPLOAD)));
			video.setIsLooked(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISLOOKED)));
			video.setIsImport(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISIMPORT)));
			list.add(video);
		}
		cursor.close();
		db.close();
		return list;
	}

	public ArrayList<LocalVideoData> queryAll() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(mTableName, null, null, null, null, null,
						LocalVideoData.KEY_ID + " desc");
		ArrayList<LocalVideoData> list = new ArrayList<LocalVideoData>();
		while (cursor.moveToNext()) {
			LocalVideoData video = new LocalVideoData();
			video.setId(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_ID)));
			video.setName(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_NAME)));
			video.setVideoImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_IMGPATH)));
			video.setVideoDetailImgPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_DETAILIMGPATH)));
			video.setVideoLocalPath(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LOCALPATH)));
			video.setVideoCreateDate(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATEDATE)));
			video.setVideoCreateTime(cursor.getString(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_CREATETIME)));
			video.setVideoLength(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_VIDEO_LENGTH)));
			video.setIsUpload(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISUPLOAD)));
			video.setIsLooked(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISLOOKED)));
			video.setIsImport(cursor.getInt(cursor
					.getColumnIndex(LocalVideoData.KEY_ISIMPORT)));
			list.add(video);
		}
		cursor.close();
		db.close();
		return list;
	}

}
