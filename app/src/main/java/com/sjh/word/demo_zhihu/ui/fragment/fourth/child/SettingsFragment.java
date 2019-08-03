package com.sjh.word.demo_zhihu.ui.fragment.fourth.child;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import me.yokeyword.fragmentation.SupportFragment;
import com.sjh.word.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by YoKeyword on 16/6/6.
 */
public class SettingsFragment extends SupportFragment {
    private Toolbar mToolbar;
    private Button button_update;

    public static SettingsFragment newInstance() {

        Bundle args = new Bundle();

        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zhihu_fragment_fourth_settings, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbarSettings);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                _mActivity.onBackPressed();
            }
        });

        button_update=view.findViewById(R.id.wirte_btn);
        button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                try {
                    //Log.i("run_try","");
                    writeExternal(getActivity(),"test_sjh.txt","66666");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public boolean onBackPressedSupport() {
        pop();
        return true;
    }

    /**
     * 将内容写入sd卡中
     * @param filename 要写入的文件名
     * @param content  待写入的内容
     * @throws IOException
     */
    private void writeExternal(Context context, String filename, String content) throws IOException {

        Log.i("运行","");
        //获取外部存储卡的可用状态
        String storageState = Environment.getExternalStorageState();

        //判断是否存在可用的的SD Card
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {

            //路径： /storage/emulated/0/Android/data/com.yoryky.demo/cache/yoryky.txt
            filename = context.getExternalCacheDir().getAbsolutePath()  + File.separator + filename;
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(content.getBytes());
            outputStream.close();
            Toast.makeText(getActivity(), "写入完成"+filename, Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(getActivity(), "写入失败", Toast.LENGTH_LONG).show();
        }
    }
}
