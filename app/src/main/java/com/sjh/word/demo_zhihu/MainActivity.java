package com.sjh.word.demo_zhihu;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import me.yokeyword.eventbusactivityscope.EventBusActivityScope;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.SupportFragment;

import com.sjh.word.R;
import com.sjh.word.demo_zhihu.base.BaseMainFragment;
import com.sjh.word.demo_zhihu.event.TabSelectedEvent;
import com.sjh.word.demo_zhihu.ui.fragment.first.ZhihuFirstFragment;
import com.sjh.word.demo_zhihu.ui.fragment.first.child.FirstHomeFragment;
import com.sjh.word.demo_zhihu.ui.fragment.fourth.ZhihuFourthFragment;
import com.sjh.word.demo_zhihu.ui.fragment.fourth.child.MeFragment;
import com.sjh.word.demo_zhihu.ui.fragment.second.ZhihuSecondFragment;
import com.sjh.word.demo_zhihu.ui.fragment.second.child.ViewPagerFragment;
import com.sjh.word.demo_zhihu.ui.fragment.third.ZhihuThirdFragment;
import com.sjh.word.demo_zhihu.ui.fragment.third.child.ShopFragment;
import com.sjh.word.demo_zhihu.ui.view.BottomBar;
import com.sjh.word.demo_zhihu.ui.view.BottomBarTab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 类知乎 复杂嵌套Demo tip: 多使用右上角的"查看栈视图"
 * Created by YoKeyword on 16/6/2.
 */
public class MainActivity extends SupportActivity implements BaseMainFragment.OnBackToFirstListener {
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOURTH = 3;

    //初始化4个底层Fragment用来装入不同的定制的Fragment
    private SupportFragment[] mFragments = new SupportFragment[4];

    //底层工具栏
    private BottomBar mBottomBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhihu_activity_main);//设置上下文
        //创建第一个Fragment
        SupportFragment firstFragment = findFragment(ZhihuFirstFragment.class);
        if (firstFragment == null) {
            mFragments[FIRST] = ZhihuFirstFragment.newInstance();
            mFragments[SECOND] = ZhihuSecondFragment.newInstance();
            mFragments[THIRD] = ZhihuThirdFragment.newInstance();
            mFragments[FOURTH] = ZhihuFourthFragment.newInstance();
            //load多个RootFragment到container
            loadMultipleRootFragment(R.id.fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOURTH]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findFragment(ZhihuSecondFragment.class);
            mFragments[THIRD] = findFragment(ZhihuThirdFragment.class);
            mFragments[FOURTH] = findFragment(ZhihuFourthFragment.class);
        }


        initView();
         /*
        File测试
         */
        //Log.i("File_Test",getCacheDir().getAbsolutePath());
        //Log.i("File_Test1",getFilesDir().getAbsolutePath());
        //以上为内存地址
        //以下为外存地址
        //Log.i("File_Test2",getExternalCacheDir().getAbsolutePath());//需要这个

        //写入
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                initFile();
            }
        }).start();
        */
    }

    private void initView() {
        mBottomBar = (BottomBar) findViewById(R.id.bottomBar);
        //在BottomBar中加入4个Tab的Item
        mBottomBar.addItem(new BottomBarTab(this, R.drawable.ic_home_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_discover_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_message_white_24dp))
                .addItem(new BottomBarTab(this, R.drawable.ic_account_circle_white_24dp));
        //为整个BottomBar添加SelectListener
        mBottomBar.setOnTabSelectedListener(new BottomBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position, int prePosition) {
                showHideFragment(mFragments[position], mFragments[prePosition]);
                //需要2分参数 当前位置和前一位置来show和hide
            }

            @Override
            public void onTabUnselected(int position) {
                //取消选中的方法
            }

            @Override
            public void onTabReselected(int position) {
                //重复选中一个Tab的方法
                //具体效果：回到初始处/刷新（已是初始且顶部）/回到顶部
                final SupportFragment currentFragment = mFragments[position];
                int count = currentFragment.getChildFragmentManager().getBackStackEntryCount();

                // 如果不在该类别Fragment的主页,则回到主页;
                if (count > 1) {
                    if (currentFragment instanceof ZhihuFirstFragment) {
                        currentFragment.popToChild(FirstHomeFragment.class, false);
                    } else if (currentFragment instanceof ZhihuSecondFragment) {
                        currentFragment.popToChild(ViewPagerFragment.class, false);
                    } else if (currentFragment instanceof ZhihuThirdFragment) {
                        currentFragment.popToChild(ShopFragment.class, false);
                    } else if (currentFragment instanceof ZhihuFourthFragment) {
                        currentFragment.popToChild(MeFragment.class, false);
                    }
                    return;
                }


                // 这里推荐使用EventBus来实现 -> 解耦
                if (count == 1) {
                    // 在FirstPagerFragment中接收, 因为是嵌套的孙子Fragment 所以用EventBus比较方便
                    // 主要为了交互: 重选tab
                    // 如果列表不在顶部则移动到顶部,如果已经在顶部,则刷新
                    EventBusActivityScope.getDefault(MainActivity.this).post(new TabSelectedEvent(position));
                }
            }
        });
    }

    @Override
    public void onBackPressedSupport() {
        //返回键的作用方法
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            pop();//pop当前fragment以回到上一个
        } else {
            //只有一个的时候关闭Activity
            ActivityCompat.finishAfterTransition(this);
        }
    }

    @Override
    public void onBackToFirstFragment() {
        mBottomBar.setCurrentItem(0);
    }

    /**
     * 这里暂没实现,忽略
     */
//    @Subscribe
//    public void onHiddenBottombarEvent(boolean hidden) {
//        if (hidden) {
//            mBottomBar.hide();
//        } else {
//            mBottomBar.show();
//        }
//    }

}
