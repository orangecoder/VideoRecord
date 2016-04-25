package com.orangecoder.videorecord.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.view.IndicatorViewPager;
import com.orangecoder.videorecord.view.indicator.Indicator;
import com.orangecoder.videorecord.view.indicator.slidebar.ColorBar;
import com.orangecoder.videorecord.view.indicator.transition.OnTransitionTextListener;
import com.orangecoder.videorecord.view.viewpager.SViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by xpmbp on 16/3/2.
 */
public class MediaFragment extends LazyFragment {

    private LayoutInflater inflate;

    //删除视频数提示控件
    public TextView tv_deletetip;
    //正常标题栏，删除状态标题栏
    public View v_titlebar, v_deletebar;

    private SViewPager viewPager;

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.fragment_media);
        EventBus.getDefault().register(this);
        inflate = LayoutInflater.from(getApplicationContext());

        initView();
        initEvent();
    }

    @Override
    protected void onDestroyViewLazy() {
        super.onDestroyViewLazy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        v_titlebar = findViewById(R.id.layout_media_titlebar);
        v_deletebar = findViewById(R.id.layout_media_deletebar);
        tv_deletetip = (TextView) findViewById(R.id.tv_media_deleltetip);

        Indicator indicator = (Indicator) findViewById(R.id.fragment_media_indicator);
        indicator.setScrollBar(new ColorBar(getApplicationContext(), Color.WHITE, 2));
        float unSelectSize = 14;
        float selectSize = unSelectSize * 1.2f;
        Resources res = getResources();
        int selectColor = res.getColor(R.color.tab_top_text_select);
        int unSelectColor = res.getColor(R.color.tab_top_text_unSelect);
        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));

        viewPager = (SViewPager) findViewById(R.id.fragment_media_viewPager);
        viewPager.setCanScroll(true);
        viewPager.setOffscreenPageLimit(2);

        IndicatorViewPager indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getFragmentManager()));
    }

    private void initEvent() {
        MyOnClickListener listener = new MyOnClickListener();

        findViewById(R.id.btn_media_finish).setOnClickListener(listener);
        findViewById(R.id.btn_media_delete).setOnClickListener(listener);
    }

    private void clickFinishBtn() {
        EventBus.getDefault().post(new MediaFragmentEvent(MediaFragmentEvent.UPDATE_CLICKFINISHBTN));
        EventBus.getDefault().post(new BaseMediaFragment.BaseMediaFragmentEvent(BaseMediaFragment.BaseMediaFragmentEvent.UPDATEVIEW));
    }

    private void clickDeleteBtn() {
        EventBus.getDefault().post(new BaseMediaFragment.BaseMediaFragmentEvent(BaseMediaFragment.BaseMediaFragmentEvent.DELETEVIDEO));
    }

    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_media_finish:
                    clickFinishBtn();
                    break;
                case R.id.btn_media_delete:
                    clickDeleteBtn();
                    break;
            }
        }
    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.tab_top, container, false);
            }

            TextView textView = (TextView) convertView;
            switch (position) {
                case 0:
                    textView.setText("草稿箱");
                    break;
                case 1:
                    textView.setText("手机相册");
                    break;
            }

            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            return BaseMediaFragment.getInstance(position);
        }
    }

    public static class MediaFragmentEvent {
        public static final int UPDATE_CLICKVIEW = 0;
        public static final int UPDATE_LONGCLICKVIEW = 1;
        public static final int UPDATE_CLICKFINISHBTN = 2;

        public int type;
        public int deleteNum;

        public MediaFragmentEvent(int type) {
            this.type = type;
        }

        public MediaFragmentEvent(int type, int deleteNum) {
            this.type = type;
            this.deleteNum = deleteNum;
        }

        public int getType() {
            return type;
        }

        public int getDeleteNum() {
            return deleteNum;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MediaFragmentEvent event) {
        switch (event.getType()) {
            case MediaFragmentEvent.UPDATE_CLICKVIEW:
                int deleteNum = event.getDeleteNum();
                if (deleteNum > 0) {
                    tv_deletetip.setText("已选择(" + deleteNum + ")");
                } else {
                    tv_deletetip.setText("请选择要删除的视频");
                }
                break;
            case MediaFragmentEvent.UPDATE_LONGCLICKVIEW:
                v_titlebar.setVisibility(View.GONE);
                v_deletebar.setVisibility(View.VISIBLE);
                tv_deletetip.setText("请选择要删除的视频");
                viewPager.setCanScroll(false);
                break;
            case MediaFragmentEvent.UPDATE_CLICKFINISHBTN:
                v_titlebar.setVisibility(View.VISIBLE);
                v_deletebar.setVisibility(View.GONE);
                viewPager.setCanScroll(true);
                break;
        }
    }

}
