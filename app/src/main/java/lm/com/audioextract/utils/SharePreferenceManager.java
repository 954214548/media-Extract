package lm.com.audioextract.utils;

import android.content.Context;
import android.util.Log;

import com.tencent.mmkv.MMKV;

public class SharePreferenceManager {

    static MMKV sp;

    public static void init(Context mContext, String name){
        sp = MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE);
    }


    private static final String USER_TOKEN = "user_token";

    public static void setUserToken(String psw) {
        if (null != sp) {
            Log.v("token","token setUserToken"+psw);
            sp.edit().putString(USER_TOKEN, psw).apply();
        }
    }

    public static String getUserToken() {
        if (null != sp) {
            return sp.getString(USER_TOKEN, null);
        }
        return null;
    }

    private static final String SHOW_MUXERTIP = "handletip";

    public static void setShowMuxertip(boolean count) {
        if (null != sp) {
            sp.edit().putBoolean(SHOW_MUXERTIP, count).apply();
        }
    }

    public static boolean getShowMuxertip () {
        if (null != sp) {
            return sp.getBoolean(SHOW_MUXERTIP, false);
        }
        return false;
    }

    private static final String USER_ID = "user_id";

    public static void setUserId(String uid) {
        if (null != sp) {
            sp.edit().putString(USER_ID, uid).apply();
        }
    }

    public static String getUserId() {
        if (null != sp) {
            return sp.getString(USER_ID, null);
        }
        return null;
    }


    private static final String CHANNEL = "channel";

    public static void setChannel(String channel) {
        if (null != sp) {
            sp.edit().putString(CHANNEL, channel).apply();
        }
    }

    public static String getChannel() {
        if (null != sp) {
            return sp.getString(CHANNEL, null);
        }
        return null;
    }

    private static final String KEY_CACHED_AVATAR_PATH = "avatar";

    public static void setCachedAvatarString(String path) {
        if (null != sp) {
            sp.edit().putString(KEY_CACHED_AVATAR_PATH, path).apply();
        }
    }

    public static String getCachedAvatarString() {
        if (null != sp) {
            return sp.getString(KEY_CACHED_AVATAR_PATH, null);
        }
        return null;
    }

}
