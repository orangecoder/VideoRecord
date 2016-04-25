package com.orangecoder.videorecord.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.activity.RecordActivity;
import com.orangecoder.videorecord.fragment.BaseMediaFragment;
import com.orangecoder.videorecord.fragment.BaseMediaFragment.BaseMediaFragmentEvent;
import com.orangecoder.videorecord.fragment.MediaFragment.MediaFragmentEvent;
import com.orangecoder.videorecord.model.LocalVideoData;
import com.orangecoder.videorecord.storage.BaseScanVideoFileTask;
import com.orangecoder.videorecord.storage.ScanMyCaptureVideoFileTask;
import com.orangecoder.videorecord.storage.ScanSystemVideoFileTask;
import com.orangecoder.videorecord.storage.db.LocalVideoDBManager;
import com.orangecoder.videorecord.storage.db.LocalVideoDataTableManager;
import com.orangecoder.videorecord.storage.db.SystemVideoDataTableManager;
import com.orangecoder.videorecord.utils.Basic_StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class MediaActivityAdapter extends BaseAdapter<LocalVideoData> {
	private final int PAGE_NUM = 12;

	private ImageLoader imageLoader;

	private List<LocalVideoData> videos;  // 要展示的视频数据
	private List<LocalVideoData> deleteVideos; // 要删除的视频数据
	private List<LocalVideoData> newVideos; // 新的视频数据
	private boolean isShowDelete = false;  //是否显示删除视频ui
	private int fragmentType;  //当前fragment类型
	private int pages;  //当前加载页数

	private LocalVideoDBManager mDbManager; // 我的视频数据库表管理器
	private HandlerThread handlerThread;
	private Handler mVideoScanHandler;
	public Boolean isMyCaptureVideoLoading = false, isSystemVideoLoading = false;

	public MediaActivityAdapter(Context context, int currentFragment) {
		super(context);
		fragmentType = currentFragment;
		imageLoader = ImageLoader.getInstance();

		videos = new ArrayList<LocalVideoData>();
		deleteVideos = new ArrayList<LocalVideoData>();
		newVideos = new ArrayList<LocalVideoData>();

		handlerThread = new HandlerThread("videoScan");
		handlerThread.start();
		mVideoScanHandler = new Handler(handlerThread.getLooper());
		switch (fragmentType) {
			case BaseMediaFragment.FRAGMENT_DRAFTBOX:
				mDbManager = new LocalVideoDataTableManager(context);
				break;
			case BaseMediaFragment.FRAGMENT_PHONEALBUM:
				mDbManager = new SystemVideoDataTableManager(context);
				break;
		}
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHold viewHold;
		if (convertView == null) {
			viewHold = new ViewHold();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.gvitem_video, null);
			viewHold.iv_videoThumbnail = (ImageView) convertView.findViewById(R.id.iv_mediaActivity_videoThumbnail);
			viewHold.iv_cameraShade = (ImageView) convertView.findViewById(R.id.iv_mediaActivity_cameraShade);
			viewHold.iv_camera = (ImageView) convertView.findViewById(R.id.iv_mediaActivity_camera);
			viewHold.tv_videolength = (TextView) convertView.findViewById(R.id.tv_mediaActivity_videoLength);
			viewHold.iv_point = (ImageView) convertView.findViewById(R.id.iv_mediaActivity_point);
			viewHold.v_select = convertView.findViewById(R.id.layout_mediaActivity_select);
			convertView.setTag(viewHold);
		} else {
			viewHold = (ViewHold) convertView.getTag();
		}

		final LocalVideoData videoData = getItem(position);

		if(fragmentType==BaseMediaFragment.FRAGMENT_DRAFTBOX && position==0) {
			if(!Basic_StringUtil.isNullOrEmpty(videoData.getVideoImgPath())) {
				imageLoader.displayImage(
						Uri.fromFile(new File(videoData.getVideoImgPath())).toString(),
						viewHold.iv_videoThumbnail);
			}
			viewHold.iv_cameraShade.setVisibility(View.VISIBLE);
			viewHold.iv_camera.setVisibility(View.VISIBLE);
			viewHold.tv_videolength.setText("");
			viewHold.iv_point.setVisibility(View.GONE);
		}else {
			imageLoader.displayImage(
					Uri.fromFile(new File(videoData.getVideoImgPath())).toString(),
					viewHold.iv_videoThumbnail);
			viewHold.iv_cameraShade.setVisibility(View.GONE);
			viewHold.iv_camera.setVisibility(View.GONE);
			viewHold.tv_videolength.setText(getVideoLength(videoData.getVideoLength()));

			if(videoData.getIsLooked() == 0) {
				if(!newVideos.contains(videoData)) {
					newVideos.add(videoData);
				}
				viewHold.iv_point.setVisibility(View.VISIBLE);
//			new BlinkAnimation(viewHold.iv_point).animate();
			}else {
				viewHold.iv_point.setVisibility(View.GONE);
			}
			if(deleteVideos.contains(videoData)) {
				viewHold.v_select.setVisibility(View.VISIBLE);
			}else {
				viewHold.v_select.setVisibility(View.GONE);
			}
		}

		convertView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(fragmentType==BaseMediaFragment.FRAGMENT_DRAFTBOX && position==0) {
					Intent intent = new Intent();
                    intent.setClass(getContext(), RecordActivity.class);
                    getContext().startActivity(intent);
					return;
				}

				if (isShowDelete) {
					ViewHold viewHold = (ViewHold) v.getTag();
					if (viewHold.v_select.getVisibility() == View.GONE) {
						viewHold.v_select.setVisibility(View.VISIBLE);
						deleteVideos.add(videoData);
						EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_CLICKVIEW, deleteVideos.size()));
					} else {
						viewHold.v_select.setVisibility(View.GONE);
						deleteVideos.remove(videoData);
						EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_CLICKVIEW, deleteVideos.size()));
					}
				} else {
					ViewHold viewHold = (ViewHold) v.getTag();
					if(newVideos.contains(videoData))
					{
						newVideos.remove(videoData);
					}
					mDbManager.updateLookedState(videoData.getId());
					viewHold.iv_point.setVisibility(View.GONE);
//					AppGlobal.getInstance(mContext).uploadVideo = videoData;
//					Intent intent = new Intent(mContext, SelectDialogActivity.class);
//					mContext.startActivity(intent);
				}
			}
		});
		convertView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				if (fragmentType == BaseMediaFragment.FRAGMENT_DRAFTBOX && !isShowDelete && position != 0) {
					isShowDelete = true;
					EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_LONGCLICKVIEW));

					ViewHold viewHold = (ViewHold) v.getTag();
					viewHold.v_select.setVisibility(View.VISIBLE);

					deleteVideos.add(videoData);
					EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_CLICKVIEW, deleteVideos.size()));
				}
				return true;
			}
		});

		return convertView;
	}

	class ViewHold {
		ImageView iv_videoThumbnail;
		ImageView iv_cameraShade;
		ImageView iv_camera;
		TextView tv_videolength;
		ImageView iv_point;
		View v_select;
	}

	private String getVideoLength(int length) {
		if(length < 60)
		{
			if(length < 10)
			{
				return "00:0"+length;
			}
			return "00:"+length;
		}

		int min = length/60;
		int sec = length-60*min;
		if(min < 10)
		{
			if(sec < 10)
			{
				return "0"+min+":0"+sec;
			}
			return "0"+min+":"+sec;
		}else {
			if(sec < 10)
			{
				return min+":0"+sec;
			}
			return min+":"+sec;
		}
	}

	public void onRefresh(final SwipeToLoadLayout swipeToLoadLayout) {
		loadFirstPageData();
		swipeToLoadLayout.setRefreshing(false);
	}

	public void onLoadMore(final SwipeToLoadLayout swipeToLoadLayout) {
		loadNextPageData();
		swipeToLoadLayout.setLoadingMore(false);
	}

	private void loadDataFromDB() {
		List<LocalVideoData> localVideoDatas = mDbManager.queryAll();

		//洗掉一些文件不存在的数据
		List<LocalVideoData> noExistVideos = new ArrayList<LocalVideoData>();
		for (LocalVideoData video : localVideoDatas) {
			if(!video.isDataExist()) {
				File videoThumbnail = new File(video.getVideoImgPath());
				if(videoThumbnail.exists()) {
					videoThumbnail.delete();
				}
				mDbManager.delete(video);
				noExistVideos.add(video);
			}
		}
		localVideoDatas.removeAll(noExistVideos);

		if(videos != null) {
			videos.clear();
		}
		if(fragmentType == BaseMediaFragment.FRAGMENT_DRAFTBOX) {
			if(localVideoDatas!=null && localVideoDatas.size()>0) {
				videos.add(localVideoDatas.get(0));
			}else {
				videos.add(new LocalVideoData());
			}
		}
		videos.addAll(localVideoDatas);
	}

	private void loadFirstPageData() {
		loadDataFromDB();

		int totalVideos = videos.size();
		if(totalVideos > PAGE_NUM) {
			update(videos.subList(0, PAGE_NUM));
			pages = 1;
		}else if(totalVideos > 0) {
			update(videos.subList(0, totalVideos));
			pages = 1;
		}
	}

	private void loadNextPageData() {
		int totalVideos = videos.size();
		if (totalVideos > PAGE_NUM*(pages+1)) {
			addAll(videos.subList(PAGE_NUM * pages, PAGE_NUM * (pages + 1)));
			pages++;
			notifyDataSetChanged();
		} else if (totalVideos > PAGE_NUM * pages) {
			addAll(videos.subList(PAGE_NUM*pages, totalVideos));
			pages++;
			notifyDataSetChanged();
		}
	}

	public void executeScanTask(Cursor cursor) {
		if(cursor == null) {
			mVideoScanHandler.post(new ScanMyCaptureVideoFileTask(mContext, new BaseScanVideoFileTask.Callback() {
				@Override
				public void finish() {
					isMyCaptureVideoLoading = false;
				}
			}));
		}else {
			mVideoScanHandler.post(new ScanSystemVideoFileTask(mContext, cursor, new BaseScanVideoFileTask.Callback() {
				@Override
				public void finish() {
					isSystemVideoLoading = false;
				}
			}));
		}
	}

	public void releaseResource() {
		if (handlerThread != null) {
			handlerThread.quit();
		}

		if(mVideoScanHandler != null) {
			mVideoScanHandler.removeCallbacksAndMessages(null);
		}

		if (newVideos!=null && newVideos.size()>0) {
			for (LocalVideoData localVideoData : newVideos) {
				mDbManager.updateLookedState(localVideoData.getId());
			}
		}
	}

	public void deleteVideos() {
		if (deleteVideos !=null && deleteVideos.size()>0) {
			for (LocalVideoData video : deleteVideos) {
				// 删除数据库记录
				mDbManager.delete(video);
				// 删除视频缩略图
				File thumbnailFile = new File(video.getVideoImgPath());
				if (thumbnailFile.exists()) {
					thumbnailFile.delete();
				}
				// 删除视频文件
				if(video.getIsImport() != 1) {
					File videoFile = new File(video.getVideoLocalPath());
					if (videoFile.exists()) {
						videoFile.delete();
					}
				}
			}
			EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_CLICKFINISHBTN));
			EventBus.getDefault().post(new BaseMediaFragmentEvent(BaseMediaFragmentEvent.UPDATEVIEW));
		}else {
			EventBus.getDefault().post(new BaseMediaFragmentEvent(BaseMediaFragmentEvent.NOSELECTVIDEO));
		}
	}

	public void updateDeleteView() {
		isShowDelete = false;
		newVideos.clear();
		deleteVideos.clear();
		videos.clear();
		loadFirstPageData();
	}

	public boolean isShowDelete() {
		return isShowDelete;
	}

}
