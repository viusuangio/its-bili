package com.vicoyu.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 文件相关操作工具类
 */
public class FileUtils {
    /**
     * 将文件拷贝到指定路径
     * @param source 源文件
     * @param target 目标路径
     */
    public static void copyFile(String source, String target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
    }

    /**
     * 关闭流
     * @param closeable
     */
    private static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 更新系统图库
     * @param file 需要提醒图库录入的图片文件
     * @param context 上下文
     */
    public static void updatePhotoMedia(File file , Context context){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        context.sendBroadcast(intent);
    }

}
