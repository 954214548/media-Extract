package lm.com.audioextract.application;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

import com.tencent.mmkv.MMKV;

import lm.com.audioextract.utils.SharePreferenceManager;

public class MainApplication extends Application {

    private static final String preference_configs = "muxer";
    public static final String AudioFileDir = Environment.getExternalStorageDirectory()+"/Extractor/Audio/";
    public static final String VideoFileDir = Environment.getExternalStorageDirectory()+"/Extractor/video/";
    public static final String MaxerFileDir = Environment.getExternalStorageDirectory()+"/Extractor/media/";//2019-09-06-10-24-31.aac
    public static final String musicpath = AudioFileDir+"2019-09-06-10-24-31.aac";
    public static final String RecordFileDir = Environment.getExternalStorageDirectory()+"/Extractor/record/";
    @Override
    public void onCreate() {
        super.onCreate();
        MMKV.initialize(this);

        SharePreferenceManager.init(getApplicationContext(),preference_configs);
    }
}
