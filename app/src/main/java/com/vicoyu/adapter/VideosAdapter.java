package com.vicoyu.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vicoyu.entity.BiliVideo;
import com.vicoyu.itsbili.R;
import com.vicoyu.itsbili.WebActivity;
import com.vicoyu.task.SaveCoverTask;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频列表的Adapter
 * Created by Viusuangio on 2018/1/4.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0x0000;
    private static final int TYPE_FOOTER = 0x0001;
    private Activity mActivity;
    private List<BiliVideo> mVideos = new ArrayList<>(1);
    private String mFooterInfo = "";

    public VideosAdapter(Activity activity) {
        this.mActivity = activity;
    }
    /**
     * 设置数据
     * @param videos 视频数据集合
     */
    public void setData(List<BiliVideo> videos) {
        mVideos.clear();
        if (videos != null && !videos.isEmpty()) {
            mVideos.addAll(videos);
        }
        notifyDataSetChanged();
    }

    /**
     * 添加数据
     * @param videos 视频数据集合
     */
    public void addData(List<BiliVideo> videos) {
        int start = mVideos.size();
        if (videos != null && !videos.isEmpty()) {
            mVideos.addAll(videos);
            notifyItemRangeInserted(start, videos.size());
        }
    }

    /**
     * 设置列表尾部信息
     *
     * @param footerInfo
     */
    public void setFooterInfo(String footerInfo) {
        this.mFooterInfo = footerInfo;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_footer, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType == TYPE_ITEM){
            final BiliVideo biliVideo = mVideos.get(position);
            /*加载视频信息*/
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.mTitle.setText(biliVideo.getTitle());
            itemViewHolder.mUp.setText(biliVideo.getUp());
            itemViewHolder.mViews.setText(biliVideo.getPlay());
            itemViewHolder.mDate.setText(biliVideo.getTime());
            //加载视频封面
            Glide.with(mActivity).load(biliVideo.getCoverUrl()).asBitmap().centerCrop().into(itemViewHolder.mCover);
            /*为Cover绑定点击事件*/
            itemViewHolder.mCover.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                        .setTitle("封面下载")
                        .setMessage("确定要下载该视频封面图片吗?(建议在Wi-Fi环境中下载，以便节省您的流量)")
                        .setNegativeButton("取消下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                        .setPositiveButton("确定下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //开启异步线程下载视频封面
                            SaveCoverTask task = new SaveCoverTask(mActivity);
                            task.execute(biliVideo.getCoverUrl());
                        }
                    });
                    builder.create().show();
                    return true;
                }
            });
            itemViewHolder.mList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //判断当前网络环境为移动流量还是WIFI
                    if(isWifi(mActivity)){
                        // 跳转到内置浏览器
                        String url = "http://m.bilibili.com/video/av"+ biliVideo.getAv()+".html";
                        gotoInsidePlay(url,mActivity, WebActivity.class);
                    }else{
                        // 流量提示
                        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                                .setTitle("网络环境提醒")
                                .setMessage("检测到您当前的网络环境并非在Wi-Fi环境下，任何下载或浏览操作都将会消耗手机流量，请问您是否继续？")
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {}
                                })
                                .setPositiveButton("继续", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String url = "http://m.bilibili.com/video/av"+ biliVideo.getAv()+".html";
                                        gotoInsidePlay(url,mActivity, WebActivity.class);
                                    }
                                });
                        builder.create().show();
                    }
                }
            });
        }else if(viewType == TYPE_FOOTER){
            ((FooterViewHolder) holder).mFooter.setText(mFooterInfo);
        }
    }

    @Override
    public int getItemCount() {
        return mVideos.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        private ImageView mCover;
        private TextView mTitle;
        private TextView mUp,mViews,mDate;
        private LinearLayout mList;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.iv_cover);
            mTitle = itemView.findViewById(R.id.tv_title);
            mUp = itemView.findViewById(R.id.tv_upName);
            mViews = itemView.findViewById(R.id.tv_view);
            mDate = itemView.findViewById(R.id.tv_date);
            mList = itemView.findViewById(R.id.ll_video);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder{
        private TextView mFooter;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mFooter = itemView.findViewById(R.id.tv_footer);
        }
    }

    /**
     * 跳转至内置播放器方法
     * @param path 指定播放路径
     * @param context 上下文
     * @param clz 目标Activity
     */
    private static void gotoInsidePlay(String path, Context context,Class clz){
        Intent intent = new Intent(context,clz);
        intent.putExtra("path",path);
        context.startActivity(intent);
    }

    private static boolean isWifi(Context context){
        ConnectivityManager mConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mInfo = mConn.getActiveNetworkInfo();
        if(mInfo!=null&&mInfo.getType()==ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }
}
