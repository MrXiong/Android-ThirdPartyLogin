package com.sharesdk.zx.thirdpartylogin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.friends.Wechat;

public class WeChatActivity extends AppCompatActivity {
    private static final String TAG = WeChatActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_we_chat);
        ShareSDK.initSDK(this);
        initWechat();
    }

    private void initWechat() {
        Platform wechat= ShareSDK.getPlatform(this, Wechat.NAME);
        wechat.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.v(TAG,"@@@MAP"+hashMap);
                Log.v(TAG,"@@@screen==="+hashMap.get("screen_name"));
                Log.v(TAG,"@@@头像"+hashMap.get("avatar_hd"));
                Log.v(TAG,"@@@name"+hashMap.get("name"));
                Log.v(TAG,"@@@家庭住址"+hashMap.get("location"));
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {

            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        //true不使用sso授权（不使用单点登录）
        wechat.SSOSetting(true);
        //只要功能，不要数据
        //wechat.authorize();
        //获取用户资料
        wechat.showUser(null);
    }
}
