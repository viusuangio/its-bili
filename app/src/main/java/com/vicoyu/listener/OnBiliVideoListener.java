package com.vicoyu.listener;

import com.vicoyu.entity.BiliVideo;

import java.util.List;

/**
 * Created by Viusuangio on 2018/1/5.
 */

public interface OnBiliVideoListener {
    /**
     * 视频获取成功
     * @param videoList
     */
    void onGetBiliVideos(List<BiliVideo> videoList);

    /**
     * 视频获取失败
     */
    void onFailed();

    /**
     * 没有更多数据
     */
    void onNoMore();
}
