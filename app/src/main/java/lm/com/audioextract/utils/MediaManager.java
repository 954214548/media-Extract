package lm.com.audioextract.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;

import java.io.File;
import java.io.FileInputStream;

import lm.com.audioextract.adapater.AudioPlayError;

/*
 *@Author min
 *@description:播放器管理
 */
public class MediaManager {

    private static MediaPlayer mMediaPlayer;
    private static boolean isPause;


    /**
     * 播放音乐
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(Context context, Boolean isEarPlay, final String filePath, final OnCompletionListener onCompletionListener) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            if (isEarPlay) {
                am.setMode(AudioManager.MODE_IN_COMMUNICATION);
                am.setSpeakerphoneOn(false);
                am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
            } else {
                am.setMode(AudioManager.MODE_NORMAL);
                am.setSpeakerphoneOn(true);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }

            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            FileInputStream fis = new FileInputStream(new File(filePath));
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {

        }
    }

    /**
     * 播放音乐Tts
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(Context context, final String filePath, final OnCompletionListener onCompletionListener) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            am.setMode(AudioManager.STREAM_VOICE_CALL);
            am.setSpeakerphoneOn(true);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            FileInputStream fis = new FileInputStream(new File(filePath));
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            LogUtil.d("palyerror",e.getMessage());
        }
    }
    /**
     * 播放音乐Tts
     *
     * @param filePath
     * @param onCompletionListener
     */
    public static void playSound(Context context, final String filePath, final OnCompletionListener onCompletionListener, AudioPlayError errorListener) {

        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new OnErrorListener() {

                public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                    mMediaPlayer.reset();
                    return false;
                }
            });
        } else {
            mMediaPlayer.reset();
        }
        try {
            am.setMode(AudioManager.STREAM_VOICE_CALL);
            am.setSpeakerphoneOn(true);
            am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, am.getStreamMaxVolume(AudioManager.MODE_IN_COMMUNICATION), AudioManager.FX_KEY_CLICK);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);

            mMediaPlayer.setOnCompletionListener(onCompletionListener);
            FileInputStream fis = new FileInputStream(new File(filePath));
            mMediaPlayer.setDataSource(fis.getFD());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            LogUtil.d("palyerror",e.getMessage());
            errorListener.PlayError(e.getMessage());
        }
    }
    /*
     *@Author min
     *@date 2019/9/4 16:03
     * @description:播放状态
     */
    public static boolean isPlaying(){
        if (mMediaPlayer != null) { //正在播放的时候

            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    /*
     *@Author min
     *@date 2019/9/4 16:03
     * @description:播放状态
     */
    public static void stop(){
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.stop();
        }
    }
    /**
     * 暂停播放
     */
    public static void pause() {
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume() {
        if (mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
