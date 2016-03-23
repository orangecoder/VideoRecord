package com.orangecoder.videorecord.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import com.orangecoder.videorecord.config.FilePathConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by xpmbp on 15/12/14.
 */
public class FileUtil {

    private static List<File> fileList = new ArrayList<File>();  //文件列表缓存

    //生成文件夹
    public static File getCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = null;
        //用sdcard
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
            if(!appCacheDir.isDirectory())
            {
                appCacheDir.mkdirs();
            }
        }else {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    //获取固定文件格式列表
    public static List<File> getAllFilesByFormat(Context ctx, String dir, String format){
        fileList.clear();

        File scanDir = getCacheDirectory(ctx, dir);
        if(scanDir.isDirectory()) {
            scanAllFileByFormat(scanDir, format);
        }

        return fileList;
    }

    //扫描固定格式文件
    private static void scanAllFileByFormat(File root, String format) {
        if(root==null || !root.isDirectory() || Basic_StringUtil.isNullOrEmpty(format))
        {
            return;
        }

        File[] files = root.listFiles();
        if (files == null) {
            return;
        } else {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanAllFileByFormat(file, format);
                } else if (file.getAbsolutePath().endsWith(format)) {
                    fileList.add(file);
                }
            }
        }
    }

    public static boolean isFileExist(String filePath) {
        if(TextUtils.isEmpty(filePath)) {
            return false;
        }

        try {
            File file = new File(filePath);
            if(!file.exists())
            {
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static String getDownloadVideoPath(Context context, String url) {
        return getCacheDirectory(context, FilePathConfig.PATH_DOWNLOADVIDEO).getAbsolutePath() +
                File.separator + Coder_Md5.md5(url);
    }
}
