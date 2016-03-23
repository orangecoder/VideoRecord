package com.orangecoder.videorecord.storage.db;


import android.content.Context;

public class LocalVideoDataTableManager extends LocalVideoDBManager {

	public LocalVideoDataTableManager(Context context) {
		super(context, VideoDBHelper.TN_LOCALVIDEO);
	}
}
