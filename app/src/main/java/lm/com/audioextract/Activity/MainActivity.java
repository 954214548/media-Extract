package lm.com.audioextract.Activity;

import android.database.Observable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import lm.com.audioextract.R;
import lm.com.audioextract.Activity.fragment.CutAudioFragment;
import lm.com.audioextract.Activity.fragment.MediaMuxerFragment;
import lm.com.audioextract.Activity.fragment.ExtractVideoFragment;
import lm.com.audioextract.Activity.fragment.SettingFragment;
import lm.com.audioextract.utils.BottomBar;
import lm.com.audioextract.utils.CheckPermissionUtils;
import lm.com.audioextract.utils.LogUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    private static final String TAG = "Extractor";
    private BottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RxJavaTest();
        setContentView(R.layout.activity_main);
        bottomBar = findViewById(R.id.bottom_bar);
        bottomBar.setContainer(R.id.fl_container)
                .setTitleBeforeAndAfterColor("#999999", "#1aaaf5")
                .setIconWidth(25)
                .setIconHeight(25)
                .setTitleIconMargin(10)
                .setTitleSize(16)
                .addItem(ExtractVideoFragment.class,
                        getString(R.string.string_extract),
                        R.drawable.icon_extract,
                        R.drawable.icon_extract_sel)
                .addItem(CutAudioFragment.class,
                        getString(R.string.string_cut_audio),
                        R.drawable.icon_cut_audio,
                        R.drawable.icon_cut_audio_sel)
                .addItem(MediaMuxerFragment.class,
                        getString(R.string.string_muxer_video),
                        R.drawable.icon_muxer_video,
                        R.drawable.icon_muxer_video_sel)
//                .addItem(SettingFragment.class,
//                        getString(R.string.string_setting),
//                        R.drawable.icon_setting,
//                        R.drawable.icon_setting_sel)
                .build();

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckPermissionUtils.isHasPermissions(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void RxJavaTest(){
        Observable<String> oble = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("kaishi");
                emitter.onComplete();
                emitter.onNext("end");

            }
        });


        Observer<String> observer = new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                LogUtil.d("Observer","onSubscribe");

            }

            @Override
            public void onNext(String s) {
                LogUtil.d("Observer","onNext:"+s);
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d("Observer","onError");
            }

            @Override
            public void onComplete() {
                LogUtil.d("Observer","onComplete");
            }
        };
        oble.subscribe(observer);
    }
}
