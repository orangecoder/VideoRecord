package com.orangecoder.videorecord.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewCompat;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.adapter.MediaActivityAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class BaseMediaFragment extends LazyFragment implements OnRefreshListener, OnLoadMoreListener,
		LoaderManager.LoaderCallbacks<Cursor> {
	private static final String TAG = BaseMediaFragment.class.getName();
	public static final String KEY_FRAGMENT = "fragment";
	public static final int FRAGMENT_DRAFTBOX = 0;
	public static final int FRAGMENT_PHONEALBUM = 1;

	public int currentFragment;

	private SwipeToLoadLayout swipeToLoadLayout;
	public GridView gridView;
	public MediaActivityAdapter adapter;

	public static BaseMediaFragment getInstance(int type){
		BaseMediaFragment fragment = new BaseMediaFragment();
		Bundle bundle = new Bundle();
		switch (type) {
			case FRAGMENT_DRAFTBOX:
				bundle.putInt(KEY_FRAGMENT, FRAGMENT_DRAFTBOX);
				break;
			case FRAGMENT_PHONEALBUM:
				bundle.putInt(KEY_FRAGMENT, FRAGMENT_PHONEALBUM);
				break;
		}
		bundle.putBoolean(LazyFragment.INTENT_BOOLEAN_LAZYLOAD, true);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_basemedia);
		EventBus.getDefault().register(this);
		currentFragment = getArguments().getInt(KEY_FRAGMENT);

		swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
		swipeToLoadLayout.setOnRefreshListener(this);
		swipeToLoadLayout.setOnLoadMoreListener(this);

		gridView = (GridView) findViewById(R.id.swipe_target);
		adapter = new MediaActivityAdapter(getActivity(), currentFragment);
		gridView.setAdapter(adapter);
		gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					if (!ViewCompat.canScrollVertically(view, 1)) {
						swipeToLoadLayout.setLoadingMore(true);
					}
				}

				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					ImageLoader.getInstance().pause();
				} else {
					ImageLoader.getInstance().resume();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

			}
		});

		swipeToLoadLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeToLoadLayout.setRefreshing(true);
			}
		});

	}

	@Override
	protected void onPauseLazy() {
		super.onPauseLazy();
		if (swipeToLoadLayout.isRefreshing()) {
			swipeToLoadLayout.setRefreshing(false);
		}
		if (swipeToLoadLayout.isLoadingMore()) {
			swipeToLoadLayout.setLoadingMore(false);
		}
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
		EventBus.getDefault().unregister(this);
		adapter.releaseResource();
	}

	@Override
	public void onRefresh() {
		switch (currentFragment) {
			case FRAGMENT_DRAFTBOX:
				if(!adapter.isMyCaptureVideoLoading) {
					adapter.isMyCaptureVideoLoading = true;
					adapter.onRefresh(swipeToLoadLayout);
					adapter.executeScanTask(null);
				}else {
					swipeToLoadLayout.setRefreshing(false);
				}
				break;
			case FRAGMENT_PHONEALBUM:
				if(!adapter.isSystemVideoLoading) {
					adapter.isSystemVideoLoading = true;
					adapter.onRefresh(swipeToLoadLayout);
					getLoaderManager().initLoader(1, null, this);
				}else {
					swipeToLoadLayout.setRefreshing(false);
				}
				break;
		}
	}

	@Override
	public void onLoadMore() {
		adapter.onLoadMore(swipeToLoadLayout);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getContext(), MediaStore.Video.Media.getContentUri("external"), null, null, null,
				"UPPER(" + MediaStore.Video.Media.DATA + ")");
	}

	@Override
	public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
		adapter.executeScanTask(data);
	}

	@Override
	public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
	}


	public static class BaseMediaFragmentEvent {

		public static final int DELETEVIDEO = 0;
		public static final int UPDATEVIEW = 1;
		public static final int NOSELECTVIDEO = 2;

		public int type;
		public BaseMediaFragmentEvent(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(BaseMediaFragmentEvent event) {
		switch (event.getType()) {
			case BaseMediaFragmentEvent.DELETEVIDEO:
				if (adapter.isShowDelete()) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							adapter.deleteVideos();
						}
					}).start();
				}
				break;
			case BaseMediaFragmentEvent.UPDATEVIEW:
				adapter.updateDeleteView();
				break;
			case BaseMediaFragmentEvent.NOSELECTVIDEO:
				Toast.makeText(getApplicationContext(), "请选择要删除的视频!", Toast.LENGTH_SHORT).show();
				break;
		}
	}

}
