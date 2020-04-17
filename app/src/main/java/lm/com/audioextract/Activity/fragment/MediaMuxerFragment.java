package lm.com.audioextract.Activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lm.com.audioextract.Activity.MediaMuxerActivity;
import lm.com.audioextract.R;
import lm.com.audioextract.adapater.ItemOnClickListener;
import lm.com.audioextract.adapater.MediaMuxerAdapater;
import lm.com.audioextract.application.MainApplication;

import lm.com.audioextract.model.VideoModel;
import lm.com.audioextract.utils.CheckPermissionUtils;
import lm.com.audioextract.utils.ExactorMediaUtils;
import lm.com.audioextract.utils.FileUtils;
import lm.com.audioextract.utils.LogUtil;



public class MediaMuxerFragment extends BaseFragment {


    private RecyclerView mRecyclerView;
    private Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<VideoModel> mList = new ArrayList<>();
    private MediaMuxerAdapater mMediaMuxerAdapater;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(CheckPermissionUtils.permissions, 2);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_muxer, container, false);

        mRecyclerView = view.findViewById(R.id.media_muxer_rv);
        mLayoutManager = new LinearLayoutManager(mContext);//添加
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mMediaMuxerAdapater = new MediaMuxerAdapater(mList,mContext);
        mMediaMuxerAdapater.setOnClickListener(clickListener);
        mRecyclerView.setAdapter(mMediaMuxerAdapater);

//        getAllMedai();
        initdata();

    }

    private void initdata(){
        List<VideoModel> list = new ArrayList<VideoModel>();

        Cursor cursor = mContext.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String title = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                String album = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                String artist = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                String displayName = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                String mimeType = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String path = cursor
                        .getString(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                long duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                long size = cursor
                        .getLong(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                if (duration != 0) {
                    VideoModel video = new VideoModel();
                    video.setName(title);
                    video.setSize(size);
                    video.setUrl(path);
                    video.setDuation(duration);
                    list.add(video);
                }
               // LogUtil.i(TAG,"name:"+title+"duration"+duration+" size:"+size);

            }
            cursor.close();
        }
        mList.clear();
        mList.addAll(list);
        mMediaMuxerAdapater.setmList(mList);
        mMediaMuxerAdapater.notifyDataSetChanged();
    }
    private void getAllMedai() {
        File path = new File(MainApplication.VideoFileDir);
        if (path.exists()){
            File[] files = path.listFiles();
            getFileContent(files);
        }
    }
    public String[] getSupportedMedia() {
        return new String[] {"mp4", "3gp","h264"};
    }

    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }
    private void getFileContent(File[] files){
        if (files != null){
            for (File file:files){
                if (file.isDirectory()){
                    getFileContent(file.listFiles());
                }else {
                    String fileName = file.getName();
                    String fileUrl  = file.getPath();
                    long   fileSize = FileUtils.getFileSize(file);
                    LogUtil.d("fileName",fileName+" " + "filepath:"+fileUrl);

                    if (!Arrays.asList(getSupportedMedia()).contains(getExtensionName(fileName))) {
                        continue;
                    }
                    if (fileSize != 0) {
                        VideoModel model = new VideoModel();
                        model.setName(fileName);
                        model.setUrl(fileUrl);
                        model.setSize(fileSize);
//                        model.setDuation(duration);
                        mList.add(model);
                    }
                }
            }
            mMediaMuxerAdapater.notifyData();
        }
    }
    private ItemOnClickListener clickListener = new ItemOnClickListener() {
        @Override
        public void OnClick(View view, Object object) {
            VideoModel model = (VideoModel) object;

            Intent intent = new Intent(mContext, MediaMuxerActivity.class);
            intent.putExtra("videopath",model.getUrl());
            intent.putExtra("videoname",model.getName());
            intent.putExtra("duration",model.getDuation());
            startActivity(intent);
//            if (path.exists()){
//                ExactorMediaUtils.combineTwoVideos(MainApplication.musicpath,0,model.getUrl(),path);
//            }
        }

        @Override
        public void OnClickModel(View view, VideoModel model) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogUtil.d("requestCode","requestCode:"+requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.d("requestCode","onRequestPermissionsResult:"+requestCode);
    }
}
