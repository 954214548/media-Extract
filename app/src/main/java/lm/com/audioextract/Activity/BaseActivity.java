package lm.com.audioextract.Activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import lm.com.audioextract.model.MessageEvent;
import lm.com.audioextract.utils.CheckPermissionUtils;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(CheckPermissionUtils.permissions, 1);
        }
        EventBus.getDefault().register(this);
    }
    @Subscribe
    public void onEvent(MessageEvent event){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MessageEvent event) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
