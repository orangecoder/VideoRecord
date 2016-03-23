package com.orangecoder.videorecord.model;


import com.orangecoder.videorecord.utils.FileUtil;

public class LocalVideoData {
	public static final String KEY_ID = "id";
	public static final String KEY_NAME = "name";
	public static final String KEY_VIDEO_IMGPATH = "videoImgPath";
	public static final String KEY_VIDEO_DETAILIMGPATH = "videoDetailImgPath";
	public static final String KEY_VIDEO_LOCALPATH = "videoLocalPath";
	public static final String KEY_VIDEO_CREATEDATE = "videoCreateDate";
	public static final String KEY_VIDEO_CREATETIME = "videoCreateTime";
	public static final String KEY_VIDEO_LENGTH = "videoLength";
	public static final String KEY_ISUPLOAD = "isUpload";
	public static final String KEY_ISLOOKED = "isLooked";
	public static final String KEY_ISIMPORT = "isImport";
	public static final String KEY_POST_ID= "post_id";
	public static final String KEY_PLAY_URL= "play_url";
	public static final String KEY_SOURCE_URL= "source_url";
	
	private String id;
	private String post_id; //上传成功返回的post_id
	private String play_url; //上传成功返回的post_id
	private String source_url; //上传成功返回的post_id
	private String name;  //视频名称
	private String videoImgUrl;  //上传成功后返回的图片的地址
	private String shareurl;  //上传成功后返回的分享地址
	private String videoImgPath;  //视频缩略图地址
	private String videoDetailImgPath;  //三张缩略图地址
	private String videoLocalPath;  //视频本地存储路径
	private String videoCreateDate;  //视频拍摄日期
	private String videoCreateTime;  //视频拍摄时间
	private int videoLength;  //视频长度
	private int isUpload;  //是否上传服务器  0没上传,1已上传
	private int isLooked;  //是否被查看过
	private int isImport;  //是否从app外导入的
	private boolean goToComplie;//跳转到上传页面是否为编辑
	
	public boolean isGoToComplie() {
		return goToComplie;
	}
	public void setGoToComplie(boolean goToComplie) {
		this.goToComplie = goToComplie;
	}
	public String getShareurl() {
		return shareurl;
	}
	public void setShareurl(String shareurl) {
		this.shareurl = shareurl;
	}
	public String getVideoImgUrl() {
		return videoImgUrl;
	}
	public void setVideoImgUrl(String videoImgUrl) {
		this.videoImgUrl = videoImgUrl;
	}
	public String getPlay_url() {
		return play_url;
	}
	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}
	public String getSource_url() {
		return source_url;
	}
	public void setSource_url(String source_url) {
		this.source_url = source_url;
	}
	public String getPost_id() {
		return post_id;
	}
	public void setPost_id(String post_id) {
		this.post_id = post_id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getVideoImgPath() {
		return videoImgPath;
	}
	public void setVideoImgPath(String videoImgPath) {
		this.videoImgPath = videoImgPath;
	}
	public String getVideoDetailImgPath() {
		return videoDetailImgPath;
	}
	public void setVideoDetailImgPath(String videoDetailImgPath) {
		this.videoDetailImgPath = videoDetailImgPath;
	}
	public String getVideoLocalPath() {
		return videoLocalPath;
	}
	public void setVideoLocalPath(String videoLocalPath) {
		this.videoLocalPath = videoLocalPath;
	}
	public String getVideoCreateDate() {
		return videoCreateDate;
	}
	public void setVideoCreateDate(String videoCreateDate) {
		this.videoCreateDate = videoCreateDate;
	}
	public String getVideoCreateTime() {
		return videoCreateTime;
	}
	public void setVideoCreateTime(String videoCreateTime) {
		this.videoCreateTime = videoCreateTime;
	}
	public int getVideoLength() {
		return videoLength;
	}
	public void setVideoLength(int videoLength) {
		this.videoLength = videoLength;
	}
	public int getIsUpload() {
		return isUpload;
	}
	public void setIsUpload(int isUpload) {
		this.isUpload = isUpload;
	}
	public int getIsLooked() {
		return isLooked;
	}
	public void setIsLooked(int isLooked) {
		this.isLooked = isLooked;
	}
	public int getIsImport() {
		return isImport;
	}
	public void setIsImport(int isImport) {
		this.isImport = isImport;
	}

	public boolean isDataExist()
	{
		//判断视频文件是否存在
		if(!FileUtil.isFileExist(videoLocalPath)) {
			return false;
		}

		//判断视频缩略图是否存在
		if(!FileUtil.isFileExist(videoImgPath)) {
			return false;
		}

		return true;
	}
}
