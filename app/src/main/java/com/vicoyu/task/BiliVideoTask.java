package com.vicoyu.task;

import android.os.AsyncTask;
import android.util.Log;

import com.vicoyu.entity.BiliVideo;
import com.vicoyu.listener.OnBiliVideoListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viusuangio on 2018/1/5.
 */

public class BiliVideoTask extends AsyncTask<String,Void,Integer> {
    private static final int SUCCESS = 0x0000;
    private static final int FAILED = 0x0001;
    private OnBiliVideoListener mListener;
    private List<BiliVideo> mBiliVideos = new ArrayList<>(1);


    public BiliVideoTask(OnBiliVideoListener listener) {
        this.mListener = listener;
    }
    /**
     * 第一个参数是关键词，第二个参数是搜索的页码
     * @param params
     * @return
     */
    @Override
    protected Integer doInBackground(String... params) {
        mBiliVideos.clear();
        try {
            Document doc = Jsoup.connect("http://search.bilibili.com/all?keyword=" + params[0]
                    + "&page=" + params[1] + "&order=totalrank").get();
            Elements elements = doc.select(".video").select(".list").select(".av");
            if (!elements.isEmpty()) {
                Element select = elements.get(0);
                Element mainHrefEle = select.getElementsByTag("a").get(0);
                // 得到视频链接
                String href = mainHrefEle.attr("href");
                href = "http:" + href.substring(0, href.lastIndexOf('?'));
                // 得到视频标题
                String title = mainHrefEle.attr("title");
                // 得到视频AV号
                String av = getAvnum(href);
                // 得到图片地址
                String coverUrl = select.getElementsByTag("img").get(0).attr("data-src");
                coverUrl = "http:" + coverUrl;
                // 得到播放时间
                String time = select.getElementsByTag("span").get(0).text();
                Elements mainInfo = select.getElementsByClass("so-icon");
                // 得到播放数
                String play = mainInfo.get(0).text();
                // 得到UP主姓名
                String up = mainInfo.get(3).getElementsByTag("a").text();
                BiliVideo biliVideo = new BiliVideo(coverUrl, av, title, up, play, time);
                mBiliVideos.add(biliVideo);
            }
            /*以关键字搜索的结果，有多条*/
            elements  = doc.select(".video").select(".matrix");
            for(Element ele : elements){
                Element hrefEle = ele.getElementsByTag("a").get(0);
                //得到视频链接
                String href = hrefEle.attr("href");
                href = "http:" + href.substring(0, href.lastIndexOf('?'));
                // 得到视频标题
                String title = hrefEle.attr("title");
                // 得到视频AV号
                String av = getAvnum(href);
                Element img = ele.getElementsByTag("img").get(0);
                // 得到图片地址
                String coverUrl = img.attr("data-src");
                coverUrl = "http:" + coverUrl;
                // 得到播放时间
                String time = ele.getElementsByTag("span").get(0).text();
                Elements info = ele.getElementsByClass("so-icon");
                // 得到播放数
                String watchInfo = info.get(0).text();
                Element upInfo = info.get(3);
                // 得到UP主姓名
                String up = upInfo.getElementsByTag("a").text();
                BiliVideo biliVideo = new BiliVideo(coverUrl, av, title, up, watchInfo, time);
                // 保存
                mBiliVideos.add(biliVideo);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return FAILED;
        }
        return SUCCESS;
    }

    /**
     * 根据视频URL获取AV号
     * @param href
     * @return
     */
    private String getAvnum(String href) {
        return href.replace("http://www.bilibili.com/video/av", "").replace("/", "");
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        if (status == FAILED) {
            if (mListener != null) {
                mListener.onFailed();
            }
        } else if (status == SUCCESS) {
            if (mBiliVideos.size() == 0) {
                if (mListener != null) {
                    mListener.onNoMore();
                }
            } else {
                if (mListener != null) {
                    mListener.onGetBiliVideos(mBiliVideos);
                }
            }
        }
    }
}
