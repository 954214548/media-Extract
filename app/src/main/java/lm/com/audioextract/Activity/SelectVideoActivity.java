package lm.com.audioextract.Activity;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import lm.com.audioextract.R;
import lm.com.audioextract.adapater.ItemOnClickListener;
import lm.com.audioextract.adapater.ExtractVideoAdapater;
import lm.com.audioextract.model.ActionType;
import lm.com.audioextract.model.EventType;
import lm.com.audioextract.model.MessageEvent;
import lm.com.audioextract.model.VideoModel;
import lm.com.audioextract.utils.LogUtil;
import lm.com.audioextract.view.LoadingDialog;

public class SelectVideoActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "SelectVideoMex";
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ExtractVideoAdapater mVideoAdapater;
    private List<VideoModel> mList = new ArrayList<>();
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_video);
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();

    }

    public void initView() {

        findViewById(R.id.return_back).setOnClickListener(this);
        mVideoAdapater = new ExtractVideoAdapater(this,mList);
        mVideoAdapater.setActionType(ActionType.ORIGIN_VIDEO_TYPE);
        mVideoAdapater.setClickListener(onClickListener);
        mRecyclerView = findViewById(R.id.video_rv);
        mLayoutManager = new LinearLayoutManager(this);//添加
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));//添加分隔线
        mRecyclerView.setAdapter(mVideoAdapater);
    }

    public void initData() {

        loadingDialog = new LoadingDialog(this,"加载中...",R.drawable.list_loading12);
        loadingDialog.show();
        List<VideoModel> list = new ArrayList<VideoModel>();

        Cursor cursor = this.getContentResolver().query(
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
                LogUtil.i(TAG,"name:"+title+"duration"+duration+" size:"+size);

            }
            cursor.close();
        }
        mList.clear();
        mList.addAll(list);
        mVideoAdapater.setmList(mList);
        loadingDialog.dismiss();
    }

    private ItemOnClickListener onClickListener = new ItemOnClickListener() {
        @Override
        public void OnClick(View view, Object o) {

        }

        @Override
        public void OnClickModel(View view, VideoModel model) {
            if (model != null){
                EventBus.getDefault().post(new MessageEvent(EventType.VIDEOMODEL,model));
                finish();
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.return_back:
                finish();
                break;
        }
    }
}
