package com.sharesdk.zx.thirdpartylogin;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQAuth;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 * 测试qq第三方登录功能  
 *
 */
public class QQActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mUserInfo;
    private ImageView mUserLogo;
    private Button mNewLoginButton;
    private TextView backInfo;

    private UserInfo mInfo;
    private Tencent mTencent;
    public QQAuth mQQAuth;
    // 申请的id
    public String mAppid = "222222";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qq);
        initView();
    }

    public void initView() {
        mUserInfo = (TextView) findViewById(R.id.user_nickname);
        mUserLogo = (ImageView) findViewById(R.id.user_logo);
        mNewLoginButton = (Button) findViewById(R.id.new_login_btn);
        mNewLoginButton.setOnClickListener(this);
        backInfo = (TextView) findViewById(R.id.user_callback);
        // Tencent类是SDK的主要实现类，通过此访问腾讯开放的OpenAPI。
        mQQAuth = QQAuth.createInstance(mAppid, this.getApplicationContext());
        // 实例化
        mTencent = Tencent.createInstance(mAppid, this);
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                mUserInfo.setVisibility(android.view.View.VISIBLE);
                mUserInfo.setText(msg.getData().getString("nickname"));
            } else if (msg.what == 1) {
                Bitmap bitmap = (Bitmap) msg.obj;
                mUserLogo.setImageBitmap(bitmap);
                mUserLogo.setVisibility(android.view.View.VISIBLE);
            }
        }
    };

    private void updateUserInfo() {
        if (mQQAuth != null && mQQAuth.isSessionValid()) {
            IUiListener listener = new IUiListener() {
                @Override
                public void onError(UiError e) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void onComplete(final Object response) {
                    JSONObject json = (JSONObject) response;
                    // 昵称
                    Message msg = new Message();
                    String nickname = null;
                    try {
                        nickname = ((JSONObject) response)
                                .getString("nickname");
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    msg.getData().putString("nickname", nickname);
                    msg.what = 0;
                    mHandler.sendMessage(msg);
                    // 头像
                    String path;
                    try {
                        path = json.getString("figureurl_qq_2");
                        MyImgThread imgThread = new MyImgThread(path);
                        Thread thread = new Thread(imgThread);
                        thread.start();
                    } catch (JSONException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    // TODO Auto-generated method stub

                }
            };
            // MainActivity.mTencent.requestAsync(Constants.GRAPH_SIMPLE_USER_INFO,
            // null,
            // Constants.HTTP_GET, requestListener, null);
            mInfo = new UserInfo(this, mQQAuth.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
            // mUserInfo.setText("");
            // mUserInfo.setVisibility(android.view.View.GONE);
            // mUserLogo.setVisibility(android.view.View.GONE);
        }
    }

    /**
     * 开启线程 获取头像
     */
    class MyImgThread implements Runnable {
        private String imgPath;
        private Bitmap bitmap;

        public MyImgThread(String imgpath) {
            this.imgPath = imgpath;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            bitmap = getImgBitmap(imgPath);
            Message msg = new Message();
            msg.obj = bitmap;
            msg.what = 1;
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 根据头像的url 获取bitmap
     */
    public Bitmap getImgBitmap(String imageUri) {
        // 显示网络上的图片
        Bitmap bitmap = null;
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            URL myFileUrl = new URL(imageUri);
            conn = (HttpURLConnection) myFileUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();

            is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                conn.disconnect();
                is.close();
                is.reset();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public void onClickLogin() {
        // 登录
        if (!mQQAuth.isSessionValid()) {
            // 实例化回调接口
            IUiListener listener = new BaseUiListener() {
                @Override
                protected void doComplete(JSONObject values) {
                    updateUserInfo();
                    // updateLoginButton();
                    if (mQQAuth != null) {
                        mNewLoginButton.setTextColor(Color.BLUE);
                        mNewLoginButton.setText("登录");
                    }
                }
            };
            // "all": 所有权限，listener: 回调的实例
            // mQQAuth.login(this, "all", listener);

            // 这版本登录是使用的这种方式，后面的几个参数是啥意思 我也没查到
            mTencent.loginWithOEM(this, "all", listener, "10000144",
                    "10000144", "xxxx");
        } else {
            // 注销登录
            mQQAuth.logout(this);
            updateUserInfo();

            // updateLoginButton();
            mNewLoginButton.setTextColor(Color.RED);
            mNewLoginButton.setText("退出帐号");
        }
    }

    /**
     * 调用SDK封装好的借口，需要传入回调的实例 会返回服务器的消息
     */
    private class BaseUiListener implements IUiListener {
        /**
         * 成功
         */
        @Override
        public void onComplete(Object response) {
            backInfo.setText(response.toString());
            doComplete((JSONObject) response);
        }

        /**
         * 处理返回的消息 比如把json转换为对象什么的
         *
         * @param values
         */
        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            Toast.makeText(QQActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(QQActivity.this, "cancel", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // 当点击登录按钮
        if (v == mNewLoginButton) {
            onClickLogin();
        }
    }
}
