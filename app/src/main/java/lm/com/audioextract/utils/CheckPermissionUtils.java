package lm.com.audioextract.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class CheckPermissionUtils {
    // 音频获取源
    public static int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    public static int sampleRateInHz = 44100;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    public static int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    public static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = 0;

    static List<String> permissionList = new ArrayList<>();

    // 动态申请权限
    public static String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_PHONE_STATE
            , Manifest.permission.RECORD_AUDIO
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.MODIFY_AUDIO_SETTINGS
            , Manifest.permission.ACCESS_NETWORK_STATE};
    public static String[] record_audio = new String[]{
        Manifest.permission.RECORD_AUDIO
    };
    /**
     * 判断是是否有录音权限
     */
    public static boolean isHasPermission(final Context context, boolean isShowDialog) {
        bufferSizeInBytes = 0;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
                channelConfig, audioFormat);
        AudioRecord audioRecord = new AudioRecord(audioSource, sampleRateInHz,
                channelConfig, audioFormat, bufferSizeInBytes);
        //开始录制音频
        try {
            // 防止某些手机崩溃，例如联想
            audioRecord.startRecording();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        /**
         * 根据开始录音判断是否有录音权限
         */
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            if (isShowDialog) {
                new AlertDialog.Builder(context)
                        .setTitle("提示")
                        .setMessage("经检测语音权限未开启,请先开启语音权限")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                context.startActivity(new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS));
                            }
                        })
                        .show();
            }
            return false;
        }
        audioRecord.stop();
        audioRecord.release();
        audioRecord = null;
        return true;
    }

    /**
     * 检查是否有权限
     *
     * @return
     */
    public static boolean isHasPermissions(Activity activity) {
        permissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if ((activity.checkSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED)) {
                    permissionList.add(permissions[i]);
                }
            }
        }
        if (permissionList.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断当前设备中有没有“有权查看使用权限的应用”这个选项
     *
     * @param context
     * @return
     */
    public static boolean isNoOption(Context context) {
        PackageManager packageManager = context.getApplicationContext()
                .getPackageManager();
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }
    public AlertDialog popupPermissionDialog(final Activity activity, final boolean isExit) {
        AlertDialog permissionDialog = null;
        if (permissionDialog == null) {
            permissionDialog = new AlertDialog.Builder(activity)
                    .setTitle("已禁用权限，请手动授予所有权限,否则软件不能正常使用")
                    .setMessage("按设置键进入权限设置，按取消键将退出本应用")
                    .setCancelable(false)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                            activity.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            if (isExit) {
//                                ActivityCollector.finishAll();
                            }
                        }
                    }).create();
            permissionDialog.setCancelable(false);
        }
        return permissionDialog;
    }

    /**
     * 判断调用该设备中“有权查看使用权限的应用”这个选项的APP有没有打开
     *
     * @param context
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean isNoSwitch(Context context) {
//        long ts = System.currentTimeMillis();
//        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext()
//                .getSystemService("usagestats");
//        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
//                UsageStatsManager.INTERVAL_BEST, 0, ts);
//        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
//            return false;
//        }
//        return true;

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager aom = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            return aom.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, info.uid, info.packageName) == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {

        }
        return false;
    }
}
