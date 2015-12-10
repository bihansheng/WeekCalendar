package com.hansheng.weekcalendar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hansheng.weekcalendar.view.MyCalendar;

public class MainActivity extends AppCompatActivity {
    Toolbar mToolbar;
    TextView mTvTitle;//标题
    MyCalendar mMcCalendar;//自定义日历控件
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMcCalendar = (MyCalendar) findViewById(R.id.mc_calendar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        //设置日历点击事件
        mMcCalendar.setOnItemClickLitener(new MyCalendar.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(MainActivity.this, mMcCalendar.getTheDayOfSelected(), Toast.LENGTH_SHORT).show();
            }
        });

        // 设置标题栏  “今天”按钮
        mToolbar.inflateMenu(R.menu.menu_main);
        MenuItem item = mToolbar.getMenu().findItem(R.id.action_inbox);
        item.setActionView(R.layout.menu_item_today);
        item.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMcCalendar.showToday()) { //跳转到今天
                    Toast.makeText(MainActivity.this, mMcCalendar.getTheDayOfSelected(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
