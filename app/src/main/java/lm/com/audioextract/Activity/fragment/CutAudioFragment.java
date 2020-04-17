package lm.com.audioextract.Activity.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.Activity.AudioEditActivity;
import lm.com.audioextract.Activity.CutAudioActivity;
import lm.com.audioextract.R;
import lm.com.audioextract.adapater.CutAudioAdapater;
import lm.com.audioextract.adapater.ItemOnClickListener;
import lm.com.audioextract.application.MainApplication;
import lm.com.audioextract.model.ActionType;
import lm.com.audioextract.model.AudioModel;
import lm.com.audioextract.model.VideoModel;
import lm.com.audioextract.utils.FileUtils;
import lm.com.audioextract.utils.LogUtil;

public class CutAudioFragment extends BaseFragment{

    private RecyclerView mRecyclerView;
    private CutAudioAdapater mCutAudioAdapater;
    private Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<AudioModel> mList = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cut_audio, container, false);
        mRecyclerView = view.findViewById(R.id.cut_audio_rv);
        mLayoutManager = new LinearLayoutManager(mContext);//添加
        mRecyclerView.setLayoutManager(mLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mCutAudioAdapater = new CutAudioAdapater(mList,mContext);
        mCutAudioAdapater.setOnClickListener(clickListener);
        mRecyclerView.setAdapter(mCutAudioAdapater);

        getAllAudio();
    }


    private ItemOnClickListener clickListener =  new ItemOnClickListener() {
        @Override
        public void OnClick(View view, Object object) {
            AudioModel model = (AudioModel) object;
            if (model != null){
                Intent intent = new Intent(mContext, AudioEditActivity.class);
                intent.putExtra("audiopath",model.getUrl());
                intent.putExtra("audioname",model.getName());
                startActivity(intent);
            }

        }

        @Override
        public void OnClickModel(View view, VideoModel model) {

        }
    };

    private void getAllAudio(){
        if (mList.size() > 0){
            mList.clear();
        }
        File path = new File(MainApplication.AudioFileDir);
        if (path.exists()){
            File[] files = path.listFiles();
            getFileContent(files);
        }
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
//                    LogUtil.d("duration",duration+"" + "filepath:"+fileUrl);
                    if (fileSize != 0) {
                        AudioModel model = new AudioModel();
                        model.setName(fileName);
                        model.setUrl(fileUrl);
                        model.setSize(fileSize);
//                        model.setDuation(duration);
                        mList.add(model);
                    }
                }
            }
            mCutAudioAdapater.notifyData();
        }
    }
}
