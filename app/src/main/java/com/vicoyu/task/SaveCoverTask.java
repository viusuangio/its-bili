package com.vicoyu.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.target.Target;
import com.vicoyu.utils.FileUtils;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static cc.duduhuo.applicationtoast.AppToast.showToast;

/**
 * 视频封面下载异步线程类
 * Created by Viusuangio on 2018/1/7.
 */

public class SaveCoverTask extends AsyncTask<String, Void, Boolean> {
    private Activity mActivity;

    public SaveCoverTask(Activity activity) {
        this.mActivity = activity;
    }

    /**
     * @param params 第一个参数是封面URL
     * @return
     */
    @Override
    protected Boolean doInBackground(String... params) {
        FutureTarget<File> target = Glide.with(mActivity).load(params[0]).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        String coverPath = Environment.getExternalStorageDirectory() +
                File.separator + Environment.DIRECTORY_DOWNLOADS +
                File.separator + "BILI_" + System.currentTimeMillis() + ".jpg";
        Log.d("bili", coverPath);
        try {
            File file = target.get();
            /*拷贝下载的头像文件到SD卡下的应用工作目录*/
            FileUtils.copyFile(file.getAbsolutePath(), coverPath);
            file.delete();
            /*通知系统更新相册*/
            File mCopy = new File(coverPath);
            FileUtils.updatePhotoMedia(mCopy,mActivity);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        super.onPostExecute(success);
        if (success) {
            showToast("封面已保存至 " + Environment.DIRECTORY_DOWNLOADS + " 目录下 ：）");
        } else {
            showToast("封面保存失败 ：（");
        }
    }
}
