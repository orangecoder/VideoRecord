package com.orangecoder.videorecord.storage.db;


import android.content.Context;

public class SystemVideoDataTableManager extends LocalVideoDBManager{

	public SystemVideoDataTableManager(Context context) {
		super(context, VideoDBHelper.TN_SYS_VIDEO);
	}

}
