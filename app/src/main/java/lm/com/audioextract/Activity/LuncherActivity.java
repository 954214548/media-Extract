package lm.com.audioextract.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import lm.com.audioextract.R;
import lm.com.audioextract.utils.CheckPermissionUtils;

public class LuncherActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(CheckPermissionUtils.permissions, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CheckPermissionUtils.isHasPermissions(this)){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }
}
