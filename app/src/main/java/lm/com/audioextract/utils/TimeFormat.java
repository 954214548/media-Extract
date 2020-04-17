package lm.com.audioextract.utils;

import java.text.SimpleDateFormat;

import io.reactivex.Observable;

public class TimeFormat {


    /**
     * 格式化当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
    //毫秒转秒
    public static String long2String(long time) {
        //毫秒转秒
        int sec = (int) time / 1000;
        int min = sec / 60;    //分钟
        sec = sec % 60;        //秒
        if (min < 10) {    //分钟补0
            if (sec < 10) {    //秒补0
                return "0" + min + ":0" + sec;
            } else {
                return "0" + min + ":" + sec;
            }
        } else {
            if (sec < 10) {    //秒补0
                return min + ":0" + sec;
            } else {
                return min + ":" + sec;
            }
        }
    }


}
