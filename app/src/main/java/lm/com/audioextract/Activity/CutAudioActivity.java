package lm.com.audioextract.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import lm.com.audioextract.R;


import lm.com.audioextract.utils.SoundFile.SamplePlayer;
import lm.com.audioextract.utils.SoundFile.SoundFile;
import lm.com.audioextract.view.waveview.MarkerView;
import lm.com.audioextract.view.waveview.WaveformView;

public class CutAudioActivity extends AppCompatActivity implements MarkerView.MarkerListener , WaveformView.WaveformListener {

    private WaveformView mWaveformView;
    private MarkerView mStartMarker;
    private MarkerView mEndMarker;
    private Thread mLoadSoundFileThread;
    private SamplePlayer mPlayer;
    private SoundFile mSoundFile;
    private String filePath;
    private ProgressDialog mProgressDialog;
    private boolean mLoadingKeepGoing;
    private long mLoadingLastUpdateTime;
    private File mFile;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_audio);

        mWaveformView = findViewById(R.id.audioWaveform);
        mStartMarker  = findViewById(R.id.startmarker);
        mEndMarker    = findViewById(R.id.endmarker);

        mWaveformView.setListener(this);
        mStartMarker.setListener(this);
        mEndMarker.setListener(this);

        filePath = getIntent().getStringExtra("audiopath");
        loadSoundFile();

    }
    private SoundFile.ProgressListener listener =
            new SoundFile.ProgressListener() {
                public boolean reportProgress(double fractionComplete) {
                    long now = getCurrentTime();
                    if (now - mLoadingLastUpdateTime > 100) {
                        mProgressDialog.setProgress(
                                (int) (mProgressDialog.getMax() * fractionComplete));
                        mLoadingLastUpdateTime = now;
                    }
                    return mLoadingKeepGoing;
                }
            };
    private long getCurrentTime() {
        return System.nanoTime() / 1000000;
    }
    private void loadSoundFile(){
        if (filePath == null)
            return;
        mLoadingLastUpdateTime = getCurrentTime();
        mLoadSoundFileThread = new Thread() {
            public void run() {
                try {
                    mSoundFile = SoundFile.create(filePath, listener);

                    if (mSoundFile == null) {
                        mProgressDialog.dismiss();
                        String name = mFile.getName().toLowerCase();
                        String[] components = name.split("\\.");
                        String err;
                        if (components.length < 2) {
                            err = getResources().getString(
                                    R.string.no_extension_error);
                        } else {
                            err = getResources().getString(
                                    R.string.bad_extension_error) + " " +
                                    components[components.length - 1];
                        }
                        final String finalErr = err;
                        Runnable runnable = new Runnable() {
                            public void run() {
                                showFinalAlert(new Exception(), finalErr);
                            }
                        };
                        mHandler.post(runnable);
                        return;
                    }
                    mPlayer = new SamplePlayer(mSoundFile);
                } catch (final Exception e) {
                    mProgressDialog.dismiss();
                    e.printStackTrace();
//                    mInfoContent = e.toString();
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            mInfo.setText(mInfoContent);
//                        }
//                    });

                    Runnable runnable = new Runnable() {
                        public void run() {
                            showFinalAlert(e, getResources().getText(R.string.read_error));
                        }
                    };
                    mHandler.post(runnable);
                    return;
                }
                mProgressDialog.dismiss();
//                if (mLoadingKeepGoing) {
//                    Runnable runnable = new Runnable() {
//                        public void run() {
//                            finishOpeningSoundFile();
//                        }
//                    };
//                    mHandler.post(runnable);
//                } else if (mFinishActivity){
//                    CutAudioActivity.this.finish();
//                }
            }
        };
        mLoadSoundFileThread.start();
    }

    private void showFinalAlert(Exception e, CharSequence message) {
        CharSequence title;
        if (e != null) {
            title = getResources().getText(R.string.alert_title_failure);
            setResult(RESULT_CANCELED, new Intent());
        } else {
            title = getResources().getText(R.string.alert_title_success);
        }

        new AlertDialog.Builder(CutAudioActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(
                        R.string.alert_ok_button,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                finish();
                            }
                        })
                .setCancelable(false)
                .show();
    }

    private void showFinalAlert(Exception e, int messageResourceId) {
        showFinalAlert(e, getResources().getText(messageResourceId));
    }
    @Override
    public void markerTouchStart(MarkerView marker, float pos) {

    }

    @Override
    public void markerTouchMove(MarkerView marker, float pos) {

    }

    @Override
    public void markerTouchEnd(MarkerView marker) {

    }

    @Override
    public void markerFocus(MarkerView marker) {

    }

    @Override
    public void markerLeft(MarkerView marker, int velocity) {

    }

    @Override
    public void markerRight(MarkerView marker, int velocity) {

    }

    @Override
    public void markerEnter(MarkerView marker) {

    }

    @Override
    public void markerKeyUp() {

    }

    @Override
    public void markerDraw() {

    }

    @Override
    public void waveformTouchStart(float x) {

    }

    @Override
    public void waveformTouchMove(float x) {

    }

    @Override
    public void waveformTouchEnd() {

    }

    @Override
    public void waveformFling(float x) {

    }

    @Override
    public void waveformDraw() {

    }

    @Override
    public void waveformZoomIn() {

    }

    @Override
    public void waveformZoomOut() {

    }
}
