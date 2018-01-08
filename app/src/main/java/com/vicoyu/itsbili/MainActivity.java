package com.vicoyu.itsbili;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.vicoyu.adapter.VideosAdapter;
import com.vicoyu.entity.BiliVideo;
import com.vicoyu.listener.OnBiliVideoListener;
import com.vicoyu.task.BiliVideoTask;
import com.vicoyu.view.DSwipeRefresh;

import java.util.List;

import static cc.duduhuo.applicationtoast.AppToast.showToast;

public class MainActivity extends AppCompatActivity implements OnBiliVideoListener {
    private EditText mEtKeyword;
    private ImageView mIvSearch;
    private DSwipeRefresh mSwipeRefresh;
    private RecyclerView mRvVideo;
    private VideosAdapter mAdapter;
    private String mFooterInfo = "";

    /** 搜索的关键词 */
    private String mKeyword;
    /** 当前页数 */
    private int mPage;
    /** 是刷新还是继续加载 */
    private boolean isLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findView();
        setListener();
    }

    private void setListener() {
        mAdapter.setFooterInfo("");
        mIvSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSearch();
                isLoad = false;
            }
        });
        /*下拉刷新*/
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BiliVideoTask task = new BiliVideoTask(MainActivity.this);
                mPage = 1;
                task.execute(mKeyword, String.valueOf(mPage));
                mSwipeRefresh.setRefreshing(true);
                isLoad = false;
            }
        });
        /*上拉加载更多*/
        mSwipeRefresh.setOnLoadingListener(new DSwipeRefresh.OnLoadingListener() {
            @Override
            public void onLoading() {
                mPage++;
                BiliVideoTask task = new BiliVideoTask(MainActivity.this);
                task.execute(mKeyword, String.valueOf(mPage));
                isLoad = true;
            }
        });
    }


    private void findView() {
        mEtKeyword = findViewById(R.id.et_keyword);
        mIvSearch = findViewById(R.id.iv_search);
        mSwipeRefresh = findViewById(R.id.swipe_refresh);
        mRvVideo = findViewById(R.id.rv_videos);

        mAdapter = new VideosAdapter(this);
        mSwipeRefresh.setRecyclerViewAndAdapter(mRvVideo, mAdapter);
        mAdapter.setFooterInfo("");
    }

    private void startSearch() {
        mKeyword = mEtKeyword.getText().toString().trim();
        if ("".equals(mKeyword)) {
            showToast("搜索关键词不能为空");
            return;
        }
        BiliVideoTask task = new BiliVideoTask(MainActivity.this);
        mPage = 1;
        task.execute(mKeyword, String.valueOf(mPage));
        mSwipeRefresh.setRefreshing(true);
        isLoad = false;
    }

    @Override
    public void onGetBiliVideos(List<BiliVideo> videoList) {
        mSwipeRefresh.setRefreshing(false);
        if (isLoad) {
            mAdapter.addData(videoList);
        } else {
            mAdapter.setData(videoList);
        }
        mAdapter.setFooterInfo("上拉加载更多...");
    }

    @Override
    public void onFailed() {
        mSwipeRefresh.setRefreshing(false);
        showToast("获取数据失败");
    }

    @Override
    public void onNoMore() {
        mSwipeRefresh.setRefreshing(false);
        showToast("没有更多数据了");
        mAdapter.setFooterInfo("没有更多数据了");
    }
}
