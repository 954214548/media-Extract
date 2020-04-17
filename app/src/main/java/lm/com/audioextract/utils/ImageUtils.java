package lm.com.audioextract.utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageUtils {

    public static String createVideoThumbFile(String thumbpath, String srcpath) {
        Bitmap bitmap = getVideoThumbnail(srcpath, 150, 200, MediaStore.Images.Thumbnails.MINI_KIND);

        FileOutputStream out;
        try {
            out = new FileOutputStream(thumbpath);
            if (bitmap != null) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                    return thumbpath;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
//        File dir = new File(ImClient.VEDIO_PATH);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
        try {
            bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
            System.out.println("w" + bitmap.getWidth());
            System.out.println("h" + bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    // 获取视频缩略图
    public static Bitmap getVideoThumbnail(String filePath) {
        Bitmap b=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b=retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }
}
