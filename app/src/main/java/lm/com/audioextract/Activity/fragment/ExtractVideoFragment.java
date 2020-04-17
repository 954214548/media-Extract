package lm.com.audioextract.Activity.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.Activity.SelectVideoActivity;
import lm.com.audioextract.R;
import lm.com.audioextract.adapater.ItemOnClickListener;
import lm.com.audioextract.adapater.ExtractVideoAdapater;
import lm.com.audioextract.application.MainApplication;
import lm.com.audioextract.dialog.ProgressDialog;
import lm.com.audioextract.model.ActionType;
import lm.com.audioextract.model.EventType;
import lm.com.audioextract.model.MessageEvent;
import lm.com.audioextract.model.VideoModel;
import lm.com.audioextract.utils.ExactorMediaUtils;
import lm.com.audioextract.utils.LogUtil;
import lm.com.audioextract.utils.ThreadUtil;
import lm.com.audioextract.utils.TimeFormat;

import static lm.com.audioextract.utils.LogUtil.TAG;

public class ExtractVideoFragment extends BaseFragment implements View.OnClickListener {


    private static final int SHOW_PROGRCESS = 1;
    private static final int UPDATE_PROGRCESS = 2;
    private static final int CLOSE_PROGRCESS = 3;

    private TextView actionTextV;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ExtractVideoAdapater mVideoAdapater;
    private List<VideoModel> mList = new ArrayList<>();
    private ImageView addFileIv;
    private ProgressDialog mProgressDialog;
    private long prgValue = 0;
    private long totalValue;






    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_PROGRCESS:
                    mProgressDialog.show();
                    prgValue = 0;
                    break;
                case UPDATE_PROGRCESS:
                    prgValue =  (long)msg.obj;
                    int progress = (int) (prgValue * 1.0f / totalValue * 100);
                    LogUtil.d("prgValue","value:"+progress);
                    mProgressDialog.setProgressBarValue(progress);
                    break;
                case CLOSE_PROGRCESS:
                    prgValue = 0;
                    mProgressDialog.dismiss();
                    Toast.makeText(mContext,getString(R.string.string_extract_finish),Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mProgressDialog = new ProgressDialog(mContext);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_origin_video, container, false);
        actionTextV = view.findViewById(R.id.select_video_tv);
        addFileIv  = view.findViewById(R.id.add_file_iv);
        actionTextV.setOnClickListener(this);
        addFileIv.setOnClickListener(this);
        actionTextV.setVisibility(View.GONE);
        mRecyclerView = view.findViewById(R.id.media_extractor_rv);
        mLayoutManager = new LinearLayoutManager(mContext);//添加
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onEvent(MessageEvent event) {
        super.onEvent(event);
        if (event.getType() == EventType.VIDEOMODEL){
            mList.add(event.getModel());
            mVideoAdapater.setmList(mList);
            actionTextV.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mVideoAdapater = new ExtractVideoAdapater(mContext,mList);
        mVideoAdapater.setActionType(ActionType.EXTRACT_TYPE);
        mVideoAdapater.setClickListener(clickListener);
        mRecyclerView.setAdapter(mVideoAdapater);

        if (mList.size() > 0){
            actionTextV.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }else {
            mRecyclerView.setVisibility(View.GONE);
            actionTextV.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.select_video_tv:
            case R.id.add_file_iv:
                Intent intent = new Intent(mContext, SelectVideoActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }

    private ItemOnClickListener clickListener = new ItemOnClickListener() {
        @Override
        public void OnClick(View view, Object o) {
            VideoModel model = (VideoModel) o;
            LogUtil.d("VideoModel",model.getName()+" d:"+model.getUrl());
        }

        @Override
        public void OnClickModel(View view, VideoModel model) {
            switch (view.getId()){
                case R.id.extract_audio_rl:
                case R.id.extract_audio_ll:
                case R.id.extract_audio_ib:
                case R.id.extract_audio_tv:
                    extractAudio(model);
                    break;
                case R.id.extract_video_rl:
                    extractVideo(model);
                    break;
                case R.id.extract_audiopath_rl:
                    openAudioFile();
                    break;
            }
        }
    };

    private void ShowSelectDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("选择格式:");
        final String[] items = new String[]{"mp3", "acc"};
        builder.setSingleChoiceItems(items, 2, new DialogInterface.OnClickListener() {/*设置单选条件的点击事件*/
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void extractAudio(final VideoModel model){
        totalValue = model.getDuation();
        File mDir = new File(MainApplication.AudioFileDir);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        ThreadUtil.INST.excute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                FileOutputStream audioOutputStream = null;
                MediaExtractor mediaExtractor = new MediaExtractor();//声明多媒体提取器
                LogUtil.d(TAG, "audio:excute:");
                sendHandleMessage(0,SHOW_PROGRCESS);//通知显示提取进度条
                File audioFile = new File(MainApplication.AudioFileDir, TimeFormat.getCurrentTime()+".aac");//新建提取文件名
                try {
                    audioOutputStream = new FileOutputStream(audioFile);
                    mediaExtractor.setDataSource(model.getUrl());
                    int audioTrackIndex = -1;
                    int trackCount = mediaExtractor.getTrackCount();
                    LogUtil.d("mineType","   trackCount:"+trackCount);

                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                        LogUtil.d("mineType","mineType:"+mineType);
                        //音频信道
                        if (mineType.startsWith("audio")) {
                            audioTrackIndex = i;
                        }
                    }
                    //切换到音频信道
                    mediaExtractor.selectTrack(audioTrackIndex);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
                    while (true) {
                        int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);

                        if (readSampleCount < 0) {
                            sendHandleMessage(mediaExtractor.getSampleTime()/1000,CLOSE_PROGRCESS);
                            break;
                        }
                        sendHandleMessage(mediaExtractor.getSampleTime()/1000,UPDATE_PROGRCESS);

                        //保存音频信息
                        byte[] buffer = new byte[readSampleCount];
                        byteBuffer.get(buffer);
                        /************************* 用来为aac添加adts头**************************/
                        byte[] aacaudiobuffer = new byte[readSampleCount + 7];
                        addADTStoPacket(aacaudiobuffer, readSampleCount + 7);
                        System.arraycopy(buffer, 0, aacaudiobuffer, 7, readSampleCount);
                        audioOutputStream.write(aacaudiobuffer);
                        /***************************************close**************************/
                        byteBuffer.clear();
                        mediaExtractor.advance();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mediaExtractor.release();
                    mediaExtractor = null;
                    try {
                        audioOutputStream.flush();
                        audioOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void extractVideo(final VideoModel model){
        totalValue = model.getDuation();
        File mDir = new File(MainApplication.VideoFileDir);
        if (!mDir.exists()) {
            mDir.mkdirs();
        }
        ThreadUtil.INST.excute(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                FileOutputStream videoOutputStream = null;
                MediaExtractor mediaExtractor = new MediaExtractor();//声明多媒体提取器
                sendHandleMessage(0,SHOW_PROGRCESS);//通知显示提取进度条
                File videoFile = new File(MainApplication.VideoFileDir, TimeFormat.getCurrentTime()+".h264");//
                try {
                    videoOutputStream = new FileOutputStream(videoFile);
                    mediaExtractor.setDataSource(model.getUrl());
                    int videoTrackIndex = -1;
                    int trackCount = mediaExtractor.getTrackCount();

                    for (int i = 0; i < trackCount; i++) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                        //视频信道
                        if (mineType.startsWith("video")) {
                            videoTrackIndex = i;
                        }
                    }
                    if (videoTrackIndex == -1)
                        return;
                    mediaExtractor.selectTrack(videoTrackIndex);

                    ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
                    while (true) {
                        int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                        Log.d(TAG, "video:readSampleCount:" + readSampleCount);
                        if (readSampleCount < 0) {
                            sendHandleMessage(mediaExtractor.getSampleTime()/1000,CLOSE_PROGRCESS);
                            break;
                        }
                        sendHandleMessage(mediaExtractor.getSampleTime()/1000,UPDATE_PROGRCESS);
                        //保存视频信道信息
                        byte[] buffer = new byte[readSampleCount];
                        byteBuffer.get(buffer);
                        videoOutputStream.write(buffer);//buffer 写入到 videooutputstream中
                        byteBuffer.clear();
                        mediaExtractor.advance();
                    }
             } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    mediaExtractor.release();
                    mediaExtractor = null;
                    try {
                        videoOutputStream.flush();
                        videoOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendHandleMessage(long object,int what){
        Message msg=new Message();
        msg.obj= object;//message的内容
        msg.what=what;//指定message
        handler.sendMessage(msg);//handler发送message
    }

    private static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = ExactorMediaUtils.getFreqIdx(44100);
        int chanCfg = 2; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
    private void openAudioFile(){
        File file = new File(Environment.getExternalStorageDirectory()+"/Extractor/Audio/");//mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        //7.0以上跳转系统文件需用FileProvider，参考链接：https://blog.csdn.net/growing_tree/article/details/71190741
        Uri uri = FileProvider.getUriForFile(mContext,mContext.getPackageName(),file);
        intent.setData(uri);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,200);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
