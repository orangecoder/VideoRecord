package com.orangecoder.videorecord.activity;

import android.annotation.TargetApi;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orangecoder.videorecord.R;
import com.orangecoder.videorecord.camera.CameraCountDownManager;
import com.orangecoder.videorecord.camera.CameraTimer;
import com.orangecoder.videorecord.camera.MediaRecorderManager;
import com.orangecoder.videorecord.camera.MyCameraManager;
import com.orangecoder.videorecord.camera.view.CameraPreviewLayout;
import com.orangecoder.videorecord.model.LocalVideoData;


public class RecordActivity extends AppCompatActivity {

    //闪关灯，辅助线，倒计时，前后摄像头切换，录制等图标
    private ImageView icFlash, icHelpline, icCountDown, icCamSwitch, icRecord;
    //头部工具栏，录制时间，辅助线，倒计时等布局控件
    private View vHeadTool, vRecordTimer, vHelpLine, vCountDown, vBottomBar;
    //视频略缩图
    private ImageView iv_thumbnail;
    //辅助线，倒计时等图型控件
    private ImageView ivHelpLineView, ivCountDownView;
    //录制时间显示控件
    private TextView tvRecordTime;
    //相机预览控件
    private CameraPreviewLayout cameraPreviewLayout;

    //闪关灯，辅助线，倒计时，前后摄像头切换等状态
    private int sFlash=0, sHelpline=0, sCountDown=0, sCamSwitch=0;
    //倒计时秒数
    private int totalCountdownSecond = 0;
    // 是否正在录制视频
    private boolean isRecording = false;
    //默认是竖屏
    private int screenRotaion = 0;

    private MyCameraManager cameraManager;
    private MediaRecorderManager mediaRecorderManager;
    private CameraTimer cameraTimer;
    private CameraCountDownManager cameraCountDownManager;

    private MyMediaRecorderManagerCallback mMediaRecorderManagerCallback;
    private MyCameraTimerCallback mCameraTimerCallback;
    private MyCameraCountDownCallback mCameraCountDownCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        initData();
        initView();
        initEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //开启camera
        if (!cameraManager.openCamera()) {
            finish();
        }
        updateView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //释放camera
        cameraManager.releaseCamera();
    }

    private void initData() {
        mMediaRecorderManagerCallback = new MyMediaRecorderManagerCallback();
        mCameraTimerCallback = new MyCameraTimerCallback();
        mCameraCountDownCallback = new MyCameraCountDownCallback();

        cameraManager = new MyCameraManager(this);
        mediaRecorderManager = new MediaRecorderManager(this, mMediaRecorderManagerCallback);
        cameraTimer = new CameraTimer();
        cameraCountDownManager = new CameraCountDownManager();
    }

    private void initView(){
        vHeadTool = findViewById(R.id.layout_camera_headtool);
        vRecordTimer = findViewById(R.id.layout_camera_recordtimer);
        vHelpLine = findViewById(R.id.layout_camera_helpline);
        vCountDown = findViewById(R.id.layout_camera_countdown);
        vBottomBar = findViewById(R.id.layout_camera_bottom);
        iv_thumbnail = (ImageView)findViewById(R.id.iv_camera_thumbnail);

        icFlash = (ImageView)findViewById(R.id.ic_camera_flash);
        icHelpline = (ImageView)findViewById(R.id.ic_camera_helpline);
        icCountDown = (ImageView)findViewById(R.id.ic_camera_countdonw);
        icCamSwitch = (ImageView)findViewById(R.id.ic_camera_switch);
        icRecord = (ImageView)findViewById(R.id.iv_camera_record);

        ivHelpLineView = (ImageView)findViewById(R.id.iv_camera_helplineview);
        ivCountDownView = (ImageView)findViewById(R.id.iv_camera_countdownview);

        tvRecordTime = (TextView)findViewById(R.id.tv_camera_recordtime);

        cameraPreviewLayout = (CameraPreviewLayout)findViewById(R.id.layout_camera_preview);
    }

    private void initEvent(){
        MyOnClickListener mOnClickListener = new MyOnClickListener();

        findViewById(R.id.v_camera_flash).setOnClickListener(mOnClickListener);
        findViewById(R.id.v_camera_helpline).setOnClickListener(mOnClickListener);
        findViewById(R.id.v_camera_countdown).setOnClickListener(mOnClickListener);
        findViewById(R.id.v_camera_camswitch).setOnClickListener(mOnClickListener);
        findViewById(R.id.layout_camera_back).setOnClickListener(mOnClickListener);
        findViewById(R.id.iv_camera_record).setOnClickListener(mOnClickListener);
    }

    private void updateView() {
        cameraPreviewLayout.loadView(cameraManager);

        isRecording = false;

        sFlash = 0;
        sHelpline = 0;
        sCountDown = 0;
        sCamSwitch = 0;

        vHeadTool.setVisibility(View.VISIBLE);
        vRecordTimer.setVisibility(View.GONE);
        vHelpLine.setVisibility(View.GONE);
        vCountDown.setVisibility(View.GONE);
        vBottomBar.setVisibility(View.VISIBLE);

        icFlash.setImageResource(R.mipmap.ic_camera_flash);
        icHelpline.setImageResource(R.mipmap.ic_camera_helpline);
        icCountDown.setImageResource(R.mipmap.ic_camera_countdown);
        icCamSwitch.setImageResource(R.mipmap.ic_camera_switch);
        icRecord.setImageResource(R.mipmap.ic_camera_record);
    }

    private void updateHelplineView() {
        switch (sHelpline) {
            case 0:
                icHelpline.setImageResource(R.mipmap.ic_camera_helpline);
                vHelpLine.setVisibility(View.GONE);
                break;
            case 1:
                icHelpline.setImageResource(R.mipmap.ic_camera_helpline_select1);
                ivHelpLineView.setImageResource(R.mipmap.ic_camera_helpline1);
                ivHelpLineView.setScaleType(ImageView.ScaleType.CENTER);
                vHelpLine.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void updateCountDownView() {
        switch (sCountDown) {
            case 0:
                icCountDown.setImageResource(R.mipmap.ic_camera_countdown);
                totalCountdownSecond = 0;
                break;
            case 1:
                icCountDown.setImageResource(R.mipmap.ic_camera_countdown_select1);
                totalCountdownSecond = 3;
                break;
            case 2:
                icCountDown.setImageResource(R.mipmap.ic_camera_countdown_select2);
                totalCountdownSecond = 5;
                break;
            case 3:
                icCountDown.setImageResource(R.mipmap.ic_camera_countdown_select3);
                totalCountdownSecond = 10;
                break;
        }
    }

    private void clickFlashBtn() {
        if(sCamSwitch == 1) {
            return;
        }
        if(sFlash == 1) {
            sFlash = 0;
        }else {
            sFlash++;
        }

        switch (sFlash) {
            case 0:
                icFlash.setImageResource(R.mipmap.ic_camera_flash);
                cameraManager.turnOffFlash();
                break;
            case 1:
                icFlash.setImageResource(R.mipmap.ic_camera_flash_select);
                cameraManager.turnOnFlash();
                break;
        }
    }

    private void clickHelplineBtn() {
        if(sHelpline == 1) {
            sHelpline= 0;
        }else {
            sHelpline++;
        }

        updateHelplineView();
    }

    private void clickCountDownBtn() {
        if(sCountDown == 3) {
            sCountDown = 0;
        }else {
            sCountDown++;
        }

        updateCountDownView();
    }

    private void clickCamswitchBtn() {
        if(sCamSwitch == 1) {
            sCamSwitch = 0;
        }else {
            sCamSwitch++;
        }

        switch (sCamSwitch) {
            case 0:
                icCamSwitch.setImageResource(R.mipmap.ic_camera_switch);
                break;
            case 1:
                icCamSwitch.setImageResource(R.mipmap.ic_camera_switch_select);
                break;
        }

        cameraManager.switchCamera(cameraPreviewLayout.getCameraPreview().getHolder());
    }

    private void clickRecordBtn() {
        if (isRecording) {
            stopRecord();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            startRecord();
            switch (screenRotaion) {
                case Surface.ROTATION_0:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                default:
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        }
    }

    private void startRecord() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        icRecord.setEnabled(false);

        if(totalCountdownSecond>0) {
            cameraCountDownManager.startTimer(totalCountdownSecond, mCameraCountDownCallback);
        }else {
            mediaRecorderManager.startRecord(cameraManager, cameraPreviewLayout.getCameraPreview().getHolder());
        }
    }

    private void stopRecord() {
        icRecord.setEnabled(false);
        cameraPreviewLayout.getCameraPreview().setEnabled(false);

        cameraTimer.stopTimer();
        mediaRecorderManager.stopRecord();
        cameraManager.lock();

        vRecordTimer.setVisibility(View.GONE);
        vBottomBar.setVisibility(View.INVISIBLE);
    }

    private class MyOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.layout_camera_back:
                    finish();
                    break;
                case R.id.v_camera_flash:
                    clickFlashBtn();
                    break;
                case R.id.v_camera_helpline:
                    clickHelplineBtn();
                    break;
                case R.id.v_camera_countdown:
                    clickCountDownBtn();
                    break;
                case R.id.v_camera_camswitch:
                    clickCamswitchBtn();
                    break;
                case R.id.iv_camera_record:
                    clickRecordBtn();
                    break;
            }
        }
    }

    private class MyMediaRecorderManagerCallback implements MediaRecorderManager.MediaRecorderManagerCallback {

        @Override
        public void onStartRecorder() {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    isRecording = true;
                    icRecord.setEnabled(false);
                    icRecord.setImageResource(R.mipmap.ic_camera_record_select1);
                    tvRecordTime.setText("00:00:00");

                    vRecordTimer.setVisibility(View.VISIBLE);
                    vHeadTool.setVisibility(View.INVISIBLE);

                    cameraTimer.startTimer(mCameraTimerCallback);
                }
            });
        }

        @Override
        public void onStopRecorder(final LocalVideoData localVideoData) {
            runOnUiThread(new Runnable() {

                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void run() {
                    isRecording = false;
                    sFlash = 0;
                    icRecord.setEnabled(true);
                    cameraPreviewLayout.getCameraPreview().setEnabled(true);

                    icFlash.setImageResource(R.mipmap.ic_camera_flash);
                    icRecord.setImageResource(R.mipmap.ic_camera_record);

                    vHeadTool.setVisibility(View.VISIBLE);
                    vBottomBar.setVisibility(View.VISIBLE);

                    if (localVideoData == null) {
                        Toast.makeText(RecordActivity.this, "拍摄时间太短了！",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        iv_thumbnail.setImageBitmap(BitmapFactory.decodeFile(
                                localVideoData.getVideoImgPath()));
                    }
                }
            });
        }

    }

    private class MyCameraTimerCallback implements CameraTimer.CameraTimerCallback {

        @Override
        public void updateTime(final int totalSmallSecond, final boolean isShowPoint) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    tvRecordTime.setText(CameraTimer.gettime(totalSmallSecond));

                    //录制大于1秒才给停止
                    if (!icRecord.isEnabled() && totalSmallSecond > 100) {
                        icRecord.setEnabled(true);
                    }

                    if (totalSmallSecond >= 3000 && isRecording) {
                        stopRecord();
                    }
                }
            });
        }
    }

    private class MyCameraCountDownCallback implements CameraCountDownManager.CameraCountDownCallback {

        @Override
        public void onUpdateTime(final int leftSecondImgRes) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (vCountDown.getVisibility() != View.VISIBLE) {
                        vCountDown.setVisibility(View.VISIBLE);
                    }
                    ivCountDownView.setImageResource(leftSecondImgRes);
//                    new PuffInAnimation(iv_countDownView).animate();
                }
            });
        }

        @Override
        public void onCountDownFinish() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    vCountDown.setVisibility(View.GONE);
                    mediaRecorderManager.startRecord(cameraManager, cameraPreviewLayout.getCameraPreview().getHolder());
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (!isRecording) {
                finish();
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (isRecording) {
                stopRecord();
            } else {
                startRecord();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                screenRotaion = Surface.ROTATION_0;
                break;
            case Surface.ROTATION_90:
                screenRotaion = Surface.ROTATION_90;
                break;
            case Surface.ROTATION_180:
                screenRotaion = Surface.ROTATION_180;
                break;
            case Surface.ROTATION_270:
                screenRotaion = Surface.ROTATION_270;
                break;
        }

        super.onConfigurationChanged(newConfig);
    }
}
