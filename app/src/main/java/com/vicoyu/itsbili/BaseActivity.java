package com.vicoyu.itsbili;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * 基类
 * Created by Viusuangio on 2017/12/8.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    /**
     * 事件分配(点击事件)
     * @param v View
     */
    @Override
    public void onClick(View v) {}

    /**
     * 初始化View对象
     */
    public abstract void initView();

    /**
     * 初始化View对象事件绑定
     */
    public abstract void initEvent();

    /**
     * 简化Toast
     * @param message 消息参数
     */
    public void mToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    /**
     * 简化Toast
     * @param message 消息参数
     * @param time 传递S或L S为3秒 L为5秒
     */
    public void mToast(String message,int time){
        Toast.makeText(getApplicationContext(),message,time).show();
    }

    /**
     * 简化获取View操作
     * @param resId 资源id
     * @param <T> View泛型
     * @return View对象
     */
    public <T extends View> T $(int resId){
        return (T) super.findViewById(resId);
    }

    /**
     * 快速构建Intent对象并前往Activity
     * @param clz 需要前往的Activity
     */
    public void mGoto(Class clz){
        startActivity(
                new Intent(this,clz)
        );
    }


}
