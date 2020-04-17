package lm.com.audioextract.utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;

import lm.com.audioextract.adapater.VideoPlayCallBack;
import lm.com.audioextract.application.MainApplication;
import lm.com.audioextract.model.ActionType;

public class ExactorMediaUtils {

    private static final String TAG = "Exactor";

    public static void exactorAudio(String mediapath, VideoPlayCallBack callBack){
        FileOutputStream audioOutputStream = null;
        MediaExtractor mediaExtractor = new MediaExtractor();
        File audioFile = new File(MainApplication.AudioFileDir, TimeFormat.getCurrentTime()+".aac");
        try {
            audioOutputStream = new FileOutputStream(audioFile);
            mediaExtractor.setDataSource(mediapath);
            int audioTrackIndex = -1;
            int trackCount = mediaExtractor.getTrackCount();

            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                //音频信道
                if (mineType.startsWith("audio")) {
                    audioTrackIndex = i;
                }
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                Log.d(TAG, "audio:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存音频信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                /************************* 用来为aac添加adts头**************************/
                byte[] aacaudiobuffer = new byte[readSampleCount + 7];
                addADTStoPacket(aacaudiobuffer, readSampleCount + 7);
                System.arraycopy(buffer, 0, aacaudiobuffer, 7, readSampleCount);
                audioOutputStream.write(aacaudiobuffer);
                /***************************************close**************************/
                //  audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            mediaExtractor.release();
            mediaExtractor = null;
            try {
                audioOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void exactorMedia(ActionType type, String mediapath, VideoPlayCallBack callBack) {
        FileOutputStream videoOutputStream = null;
        FileOutputStream audioOutputStream = null;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            //分离的视频文件
            File videoFile = new File(MainApplication.VideoFileDir, TimeFormat.getCurrentTime()+".mp4");
            //分离的音频文件
            File audioFile = new File(MainApplication.VideoFileDir, TimeFormat.getCurrentTime()+".aac");
            videoOutputStream = new FileOutputStream(videoFile);
            audioOutputStream = new FileOutputStream(audioFile);
            //输入文件,也可以是网络文件
            //oxford.mp4 视频 h264/baseline  音频 aac/lc 44.1k  2 channel 128kb/s
            mediaExtractor.setDataSource(mediapath);

            //信道总数
            int trackCount = mediaExtractor.getTrackCount();
            Log.d(TAG, "trackCount:" + trackCount);
            int audioTrackIndex = -1;
            int videoTrackIndex = -1;
            for (int i = 0; i < trackCount; i++) {
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                String mineType = trackFormat.getString(MediaFormat.KEY_MIME);
                //视频信道
                if (mineType.startsWith("video")) {
                    videoTrackIndex = i;
                }
                //音频信道
                if (mineType.startsWith("audio")) {
                    audioTrackIndex = i;
                }
            }

            ByteBuffer byteBuffer = ByteBuffer.allocate(500 * 1024);
            //切换到视频信道
            mediaExtractor.selectTrack(videoTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                Log.d(TAG, "video:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存视频信道信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                videoOutputStream.write(buffer);//buffer 写入到 videooutputstream中
                byteBuffer.clear();
                mediaExtractor.advance();
            }
            //切换到音频信道
            mediaExtractor.selectTrack(audioTrackIndex);
            while (true) {
                int readSampleCount = mediaExtractor.readSampleData(byteBuffer, 0);
                Log.d(TAG, "audio:readSampleCount:" + readSampleCount);
                if (readSampleCount < 0) {
                    break;
                }
                //保存音频信息
                byte[] buffer = new byte[readSampleCount];
                byteBuffer.get(buffer);
                /************************* 用来为aac添加adts头**************************/
                byte[] aacaudiobuffer = new byte[readSampleCount + 7];
                addADTStoPacket(aacaudiobuffer, readSampleCount + 7);
                System.arraycopy(buffer, 0, aacaudiobuffer, 7, readSampleCount);
                audioOutputStream.write(aacaudiobuffer);
                /***************************************close**************************/
                //  audioOutputStream.write(buffer);
                byteBuffer.clear();
                mediaExtractor.advance();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d(TAG, "mediaExtractor.release!\n");
            mediaExtractor.release();
            mediaExtractor = null;
            try {
                videoOutputStream.close();
                audioOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = getFreqIdx(44100);
        int chanCfg = 2; // CPE

        // fill in ADTS data
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    public static int getFreqIdx(int sampleRate) {
        int freqIdx;

        switch (sampleRate) {
            case 96000:
                freqIdx = 0;
                break;
            case 88200:
                freqIdx = 1;
                break;
            case 64000:
                freqIdx = 2;
                break;
            case 48000:
                freqIdx = 3;
                break;
            case 44100:
                freqIdx = 4;
                break;
            case 32000:
                freqIdx = 5;
                break;
            case 24000:
                freqIdx = 6;
                break;
            case 22050:
                freqIdx = 7;
                break;
            case 16000:
                freqIdx = 8;
                break;
            case 12000:
                freqIdx = 9;
                break;
            case 11025:
                freqIdx = 10;
                break;
            case 8000:
                freqIdx = 11;
                break;
            case 7350:
                freqIdx = 12;
                break;
            default:
                freqIdx = 8;
                break;
        }

        return freqIdx;
    }

    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
//            return size + "Byte";
            return "0";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }




}
