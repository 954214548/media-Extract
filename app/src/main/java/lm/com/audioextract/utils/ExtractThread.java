package lm.com.audioextract.utils;

import android.content.Context;

import lm.com.audioextract.adapater.VideoPlayCallBack;

public class ExtractThread extends Thread {

    private VideoPlayCallBack callBack;
    private Context mContext;

    public ExtractThread(VideoPlayCallBack callBack, Context mContext) {
        this.callBack = callBack;
        this.mContext = mContext;
    }

    @Override
    public synchronized void start() {
        super.start();
    }

    @Override
    public void run() {
        super.run();
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }
}
