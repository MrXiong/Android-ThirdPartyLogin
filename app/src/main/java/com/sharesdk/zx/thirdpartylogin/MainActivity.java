package com.sharesdk.zx.thirdpartylogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTvThird;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvThird = (TextView) findViewById(R.id.tv_third);
        //长按此控件弹出
        registerForContextMenu(mTvThird);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.qq_login:
                gotoActivity(QQActivity.class);
                break;
            case R.id.wechat_login:
                gotoActivity(WeChatActivity.class);
                break;
            case R.id.weibo_login:
                gotoActivity(WeiboActivity.class);
                break;
        }
        return super.onContextItemSelected(item);

    }
    private void gotoActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }


}
