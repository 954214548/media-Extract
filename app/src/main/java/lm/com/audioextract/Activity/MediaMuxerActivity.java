package lm.com.audioextract.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import lm.com.audioextract.R;
import lm.com.audioextract.adapater.VideoPlayCallBack;
import lm.com.audioextract.application.MainApplication;
import lm.com.audioextract.dialog.ProgressDialog;
import lm.com.audioextract.utils.CheckPermissionUtils;
import lm.com.audioextract.utils.ExactorMediaUtils;
import lm.com.audioextract.utils.ImageUtils;
import lm.com.audioextract.utils.LogUtil;
import lm.com.audioextract.utils.SharePreferenceManager;
import lm.com.audioextract.utils.TimeFormat;
import lm.com.audioextract.view.CustomVideoView;

public class MediaMuxerActivity extends BaseActivity implements View.OnClickListener, VideoPlayCallBack {


    private static final String TAG = "mediamuxer";
    private static final int SHOW_PROGRCESS = 1;
    private static final int UPDATE_PROGRCESS = 2;
    private static final int CLOSE_PROGRCESS = 3;
    private CustomVideoView mVideoView;
    private String videopath;
    private String videoname;
    private String recordname;
    private String muxerVideopath;
    private String muxerVideoname;
    private ImageButton muxerAudio;
    private TextView audioNameTv;
    private ImageButton recordBtn;
    private MediaRecorder mMediaRecorder;
    private ProgressDialog mProgressDialog;
    private long prgValue = 0;
    private long totalValue;
    private boolean isRecord = false;
    private Context mContext;
    private ProgressBar mProgressBar;
    private ImageButton muxerPlayIb;
    private LinearLayout muxerPlayll;
    private TextView muxerFilePath;
    private AlertDialog.Builder mAlertDialog;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_PROGRCESS:
//                    mProgressDialog.show();
                    showProgrcess();
                    prgValue = 0;
                    break;
                case UPDATE_PROGRCESS:
                    prgValue =  (long)msg.obj;
                    int progress = (int) (prgValue * 1.0f / totalValue * 100);
                    LogUtil.d("prgValue","value:"+progress);
//                    mProgressDialog.setProgressBarValue(progress);
                    mProgressBar.setProgress(progress);
                    break;
                case CLOSE_PROGRCESS:
                    prgValue = 0;
                    closeProgress();
                    Toast.makeText(MediaMuxerActivity.this,getString(R.string.string_muxer_finshed),Toast.LENGTH_SHORT).show();
//                    mProgressDialog.dismiss();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_muxer);
        mContext = this;

        initView();
        initData();
        showAlertDialog();
    }

    private void showAlertDialog(){

        final String[] tip = new String[]{getString(R.string.string_muxer_handle_tip),getString(R.string.no_tip_again)};
        mAlertDialog = new AlertDialog.Builder(MediaMuxerActivity.this);
        mAlertDialog.setTitle(getString(R.string.tip));
        mAlertDialog.setItems(tip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (which == 1){
                    SharePreferenceManager.setShowMuxertip(true);
                }

            }
        });
        if (!SharePreferenceManager.getShowMuxertip()) {
            mAlertDialog.show();
        }
    }


    private void initView() {

        mVideoView  = findViewById(R.id.muxer_video_cv);
        muxerAudio    = findViewById(R.id.muxer_video_ib);
        audioNameTv = findViewById(R.id.audio_file_name);
        recordBtn   = findViewById(R.id.muxer_audio_record_ib);
        mProgressBar = findViewById(R.id.muxer_progress);
        muxerPlayIb  = findViewById(R.id.video_play_ib);
        muxerPlayll  = findViewById(R.id.video_play_ll);
        muxerFilePath = findViewById(R.id.muxer_file_name);
        mProgressBar.setMax(100);
        //closeProgress();
        muxerPlayIb.setOnClickListener(this);
        muxerPlayll.setOnClickListener(this);
        mVideoView.setCallBack(this);
        recordBtn.setOnClickListener(this);
        findViewById(R.id.muxer_audio_record_ll).setOnClickListener(this);
        findViewById(R.id.muxer_audio_record_ib).setOnClickListener(this);
        findViewById(R.id.muxer_video_ll).setOnClickListener(this);
        findViewById(R.id.return_back).setOnClickListener(this);
        muxerAudio.setOnClickListener(this);
        audioNameTv.setOnClickListener(this);

    }

    private void setRecodeFilePath(String path){
        audioNameTv.setText(String.format("%s:%S",getString(R.string.string_record_path),path));
    }
    private void setMuxerVideopath(String path){
        muxerFilePath.setText(String.format("%s:%S",getString(R.string.string_muxer_file),path));
    }

    private void initData() {
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle(getString(R.string.string_muxer));
        videopath = getIntent().getStringExtra("videopath");
        videoname = getIntent().getStringExtra("videoname");
        totalValue = getIntent().getLongExtra("duration",10);
        mVideoView.setUp(videopath,videoname);
        mVideoView.thumbImageView.setImageBitmap(ImageUtils.getVideoThumbnail(videopath));


    }

    private void showProgrcess(){
       // mProgressBar.setVisibility(View.VISIBLE);
        LogUtil.d("videoname","showProgrcess");
    }

    private void closeProgress(){
        //mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_back:
                finish();
                break;
            case R.id.muxer_video_ib:
            case R.id.muxer_video_ll:
                if (recordname != null) {
                    muxerAudioFile();
                }else {
                    Toast.makeText(this,getString(R.string.string_muxer_record),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.muxer_audio_record_ll:
            case R.id.muxer_audio_record_ib:
                exchangeAudioRecord();
                break;
            case R.id.video_play_ib:
            case R.id.video_play_ll:
                playMuxeredVideo();
                break;

        }
    }

    private void playMuxeredVideo() {
        if (muxerVideopath != null) {
            mVideoView.setUp(muxerVideopath, muxerVideoname);
            mVideoView.thumbImageView.setImageBitmap(ImageUtils.getVideoThumbnail(videopath));
        }else {
            Toast.makeText(MediaMuxerActivity.this,getString(R.string.string_muxer_play_tip),Toast.LENGTH_SHORT).show();
        }
    }

    private void exchangeAudioRecord() {
        if (isRecord){
            stopRecord();
        }else {
            mVideoView.startVideo();
            muxerAudioRecord();
        }
    }

    public void muxerAudioRecord() {

        if (!CheckPermissionUtils.isHasPermission(this, true)) {
            return;
        }

        File dir = new File(MainApplication.RecordFileDir);  // 文件夹
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = TimeFormat.getCurrentTime() + ".aac";

        File file = new File(dir, fileName);
        recordname = file.getAbsolutePath();
        setRecodeFilePath(recordname);
        mMediaRecorder = new MediaRecorder();
        startRecordBtn();
        try {
            mMediaRecorder.setOutputFile(file.getAbsolutePath());  // 设置输出文件
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);  // 设置音频源为麦克风
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            // 准备结束

        } catch (Exception e) {
        }
    }
    private void startRecordBtn(){
        recordBtn.setBackgroundResource(R.drawable.audio_stop);
        isRecord = true;
    }
    private void endRecordBtn(){
        if (recordname != null){
            audioNameTv.setText(recordname);
        }
        isRecord= false;
        recordBtn.setBackgroundResource(R.drawable.audio_record);
    }
    public void stopRecord() {

//        mMediaRecorder.setOnErrorListener(null);
//        mMediaRecorder.setPreviewDisplay(null);
        try {
            mMediaRecorder.stop();
            endRecordBtn();
        } catch (Exception e) {

        }
//        mMediaRecorder.reset();
//        mMediaRecorder.release();
//        mMediaRecorder = null;
    }
    private void muxerAudioFile() {
        muxerVideoname = TimeFormat.getCurrentTime()+".mp4";
        muxerVideopath = MainApplication.MaxerFileDir+muxerVideoname;
        setMuxerVideopath(muxerVideopath);
        File muxerFile = new File(muxerVideopath);
        muxerAudioAndVideo(recordname,0,videopath,muxerFile);
    }
    public void muxerAudioAndVideo(String audioVideoPath,
                                 long audioStartTime,
                                 String frameVideoPath,
                                 File combinedVideoOutFile) {

        MediaExtractor audioVideoExtractor = new MediaExtractor();
        int mainAudioExtractorTrackIndex = -1; //提供音频的视频的音频轨（有点拗口）
        int mainAudioMuxerTrackIndex = -1; //合成后的视频的音频轨
        int mainAudioMaxInputSize = 0; //能获取的音频的最大值

        MediaExtractor videoExtractor = new MediaExtractor();
        int videoExtractorIndex = -1; //视频轨
        int videoIndex = -1; //合成后的视频的视频轨
        int frameMaxInputSize = 0; //能获取的视频的最大值
        int frameRate = 0; //视频的帧率
        long frameDuration = 0;

        MediaMuxer muxer = null; //用于合成音频与视频

        try {
            muxer = new MediaMuxer(combinedVideoOutFile.getPath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

            audioVideoExtractor.setDataSource(audioVideoPath); //设置视频源
            //音轨信息
            int audioTrackCount = audioVideoExtractor.getTrackCount(); //获取数据源的轨道数
            //在此循环轨道数，目的是找到我们想要的音频轨
            for (int i = 0; i < audioTrackCount; i++) {
                MediaFormat format = audioVideoExtractor.getTrackFormat(i); //得到指定索引的记录格式
                String mimeType = format.getString(MediaFormat.KEY_MIME); //主要描述mime类型的媒体格式
                if (mimeType.startsWith("audio")) { //找到音轨
                    mainAudioExtractorTrackIndex = i;
                    mainAudioMuxerTrackIndex = muxer.addTrack(format); //将音轨添加到MediaMuxer，并返回新的轨道
                    mainAudioMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE); //得到能获取的有关音频的最大值
//                    mainAudioDuration = format.getLong(MediaFormat.KEY_DURATION);
                }
            }

            //图像信息
            videoExtractor.setDataSource(frameVideoPath); //设置视频源
            int trackCount = videoExtractor.getTrackCount(); //获取数据源的轨道数
            //在此循环轨道数，目的是找到我们想要的视频轨
            for (int index = 0; index < trackCount; index++) {
                MediaFormat format = videoExtractor.getTrackFormat(index); //得到指定索引的媒体格式
                String mimeType = format.getString(MediaFormat.KEY_MIME); //主要描述mime类型的媒体格式
                if (mimeType.startsWith("video")) { //找到视频轨
                    videoExtractorIndex = index;
                    videoIndex = muxer.addTrack(format); //将视频轨添加到MediaMuxer，并返回新的轨道
                    frameMaxInputSize = format.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE); //得到能获取的有关视频的最大值
                    frameRate = format.getInteger(MediaFormat.KEY_FRAME_RATE); //获取视频的帧率
                    frameDuration = format.getLong(MediaFormat.KEY_DURATION); //获取视频时长
                }
            }

            muxer.start(); //开始合成

            audioVideoExtractor.selectTrack(mainAudioExtractorTrackIndex); //将提供音频的视频选择到音轨上
            MediaCodec.BufferInfo audioBufferInfo = new MediaCodec.BufferInfo();
            ByteBuffer audioByteBuffer = ByteBuffer.allocate(mainAudioMaxInputSize);
            while (true) {
                int readSampleSize = audioVideoExtractor.readSampleData(audioByteBuffer, 0); //检索当前编码的样本并将其存储在字节缓冲区中
                if (readSampleSize < 0) { //如果没有可获取的样本则退出循环
                    audioVideoExtractor.unselectTrack(mainAudioExtractorTrackIndex);
                    break;
                }

                long sampleTime = audioVideoExtractor.getSampleTime(); //获取当前展示样本的时间（单位毫秒）

                if (sampleTime < audioStartTime) { //如果样本时间小于我们想要的开始时间就快进
                    audioVideoExtractor.advance(); //推进到下一个样本，类似快进
                    continue;
                }

                if (sampleTime > audioStartTime + frameDuration) { //如果样本时间大于开始时间+视频时长，就退出循环
                    break;
                }
                //设置样本编码信息
                audioBufferInfo.size = readSampleSize;
                audioBufferInfo.offset = 0;
                audioBufferInfo.flags = audioVideoExtractor.getSampleFlags();
                audioBufferInfo.presentationTimeUs = sampleTime - audioStartTime;

                muxer.writeSampleData(mainAudioMuxerTrackIndex, audioByteBuffer, audioBufferInfo); //将样本写入
                audioVideoExtractor.advance(); //推进到下一个样本，类似快进
            }
            sendHandleMessage(0,SHOW_PROGRCESS);
            videoExtractor.selectTrack(videoExtractorIndex); //将提供视频图像的视频选择到视频轨上
            MediaCodec.BufferInfo videoInfo = new MediaCodec.BufferInfo();
            ByteBuffer videoByteBuffer = ByteBuffer.allocate(frameMaxInputSize);
            while (true) {
                int readSampleSize = videoExtractor.readSampleData(videoByteBuffer, 0); //检索当前编码的样本并将其存储在字节缓冲区中
                if (readSampleSize < 0) { //如果没有可获取的样本则退出循环
                    videoExtractor.unselectTrack(videoExtractorIndex);
                    sendHandleMessage(videoExtractor.getSampleTime()/1000,CLOSE_PROGRCESS);
                    break;
                }
                sendHandleMessage(videoExtractor.getSampleTime()/1000,UPDATE_PROGRCESS);
                //设置样本编码信息
                videoInfo.size = readSampleSize;
                videoInfo.offset = 0;
                videoInfo.flags = videoExtractor.getSampleFlags();
                videoInfo.presentationTimeUs += 1000 * 1000 / frameRate;

                muxer.writeSampleData(videoIndex, videoByteBuffer, videoInfo); //将样本写入
                videoExtractor.advance(); //推进到下一个样本，类似快进
            }
        } catch (IOException e) {
            LogUtil.e(TAG, "muxer audio and video: "+e);
        } finally {
            //释放资源
            audioVideoExtractor.release();
            videoExtractor.release();
            if (muxer != null) {
                muxer.release();
            }
        }
    }
    private void sendHandleMessage(long object,int what){
        Message msg=new Message();
        msg.obj= object;//message的内容
        msg.what=what;//指定message
        handler.sendMessage(msg);//handler发送message
    }
    @Override
    public void onStateAutoComplete() {
        if (isRecord = true) {
            stopRecord();
        }
    }
}
