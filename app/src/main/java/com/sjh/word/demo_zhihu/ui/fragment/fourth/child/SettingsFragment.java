package com.sjh.word.demo_zhihu.ui.fragment.fourth.child;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import me.yokeyword.fragmentation.SupportFragment;
import com.sjh.word.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

/**
 * Created by YoKeyword on 16/6/6.
 */
public class SettingsFragment extends SupportFragment {
    private Toolbar mToolbar;
    private Button button_write_local;
    private Button button_read_local;
    private Button button_write_cloud;
    private String []words =new String[35];
    //private List<String> words = new ArrayList<String>();

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
        initWords();
        mToolbar = (Toolbar) view.findViewById(R.id.toolbarSettings);

        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                _mActivity.onBackPressed();
            }
        });
        //写入本地按钮事件监听器
        button_write_local=view.findViewById(R.id.wirte_btn);
        button_write_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                //Log.i("run_try","");
                //writeExternal(getActivity(),"test_sjh.txt","66666");
                new WriteToExternalTask().execute(words);
            }
        });

        //读出本地按钮事件监听器
        button_read_local=view.findViewById(R.id.read_btn);
        button_read_local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                try {
                    //Log.i("run_try","");
                    //mTask.cancel(true);
                    Toast.makeText(getActivity(), readExternal(getActivity(),"test_sjh.txt"), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 写入云端按钮事件监听器
        // 加载按钮按按下时，则启动AsyncTask
        // 任务完成后更新TextView的文本
        button_write_cloud=view.findViewById(R.id.write_cloud_btn);
        button_write_cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "nihao", Toast.LENGTH_LONG).show();
                /*try {
                    //Log.i("run_try","");
                    writeCloud(getActivity(), "test_sjh.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                new WriteToCloudTask().execute(words);
            }
        });

        text = view.findViewById(R.id.text_wtc);
        progressBar = view.findViewById(R.id.progress_bar_wtc);

        /**
         * 步骤2：创建AsyncTask子类的实例对象（即 任务实例）
         * 注：AsyncTask子类的实例必须在UI线程中创建
         */
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

    /**
     * 从sd card文件中读取数据
     * @param filename 待读取的sd card
     * @return
     * @throws IOException
     */
    public static String readExternal(Context context, String filename) throws IOException {
        StringBuilder sb = new StringBuilder("");
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + filename;
            //打开文件输入流
            FileInputStream inputStream = new FileInputStream(filename);
            //测试
            File file = new File(filename);//filename为新的文件路径
            System.out.println(""+file.exists()+":"+file.length());

            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len));
                //将一次读取的1024byte个数据以String形式加入返回值
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            inputStream.close();
        }
        return sb.toString();
    }

    private void writeCloud(Context context, String filename) throws IOException {

        //Scanner scan = null;
        InputStream in = null;
        Socket socket = null;

        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            filename = context.getExternalCacheDir().getAbsolutePath() + File.separator + filename;
            //Toast.makeText(getActivity(),""+filename , Toast.LENGTH_LONG).show();
            //打开文件输入流
            //FileInputStream inputStream = new FileInputStream(filename);
            /*
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);
            //读取文件内容
            while(len > 0){
                sb.append(new String(buffer,0,len));
                //将一次读取的1024byte个数据以String形式加入返回值
                //继续将数据放到buffer中
                len = inputStream.read(buffer);
            }
            //关闭输入流
            */
        }

        try {
            /**
             * 1.扫描控制台接收文件路径名
             *   创建一个file引用，指向一个新的File对象，并给文件赋予地址
             */
            //System.out.println("请输入要传输文件的路径：");
            //scan = new Scanner(System.in);
            //String path = scan.nextLine();//文件路径path
            File file = new File(filename);//filename为新的文件路径

            //Toast.makeText(getActivity(),file.getName() , Toast.LENGTH_LONG).show();

            /**
             * 2.判断文件是文本文件而不是文件夹并且路径存在
             *  exists()：判断文件是否存在
             *  isFile()：判断是不是文件
             */
            //Looper.prepare();
            //Toast.makeText(getActivity(),""+file.exists() , Toast.LENGTH_LONG).show();
            //Looper.loop();
            System.out.println(""+file.exists());

            if(file.exists() && file.isFile()) {


                /**
                 * 3.创建文件输入流，发送文件
                 *   将文件输入的内容都放在file里面
                 */
                in = new FileInputStream(file);

                /**
                 * Socket 这个类实现客户端套接字(也称为“套接字”)。套接字是两台机器间通信的端点。
                 *
                 * 4.创建客户端套接字
                 */
                socket = new Socket();
                //InetSocketAddress Inets = new InetSocketAddress("127.0.0.1", 12345);

                /**
                 * 5.连接TCP服务器
                 *       确定服务端的IP和端口号
                 */
                socket.connect(new InetSocketAddress("192.168.0.105", 12345));
                //Toast.makeText(getActivity(),"1"+socket.isConnected() , Toast.LENGTH_LONG).show();
                //System.out.println(""+socket.isConnected());


                /**
                 * 6.获取到客户端的输出流
                 *   OutputStream     getOutputStream()
                 *                         返回此套接字的输出流。
                 */
                OutputStream out = socket.getOutputStream();

                /**
                 * 7.向服务器发送文件
                 *   自己定义了一个协议来解决粘包现象，获取文件名
                 *   7.1.我们先将文件中的内容读取出来，放到file里面
                 *   7.2.先读文件名  file.getName()
                 *   7.3.将文件名转换成字节  file.getName().getBytes()
                 *   7.4.获取文件名的字节的长度  file.getName().getBytes().length
                 *   7.5.再在文件名长度的后面加上  \r\n 作为标识符
                 */
                // 向服务器发送[文件名字节长度 \r\n]
                out.write((file.getName().getBytes().length + "\r\n").getBytes());
                // 向服务器发送[文件名字节]
                out.write(file.getName().getBytes());
                // 向服务器发送[文件字节长度\r\n]
                out.write((file.length() + "\r\n").getBytes());
                // 向服务器发送[文件字节内容]
                byte[] data = new byte[1024];
                int i = 0;
                while((i = in.read(data)) != -1) {
                    out.write(data, 0, i);
                }

            }else {
                //Toast.makeText(getActivity(), "文件不存在或者一个文件~~", Toast.LENGTH_LONG).show();
                //System.out.println("文件不存在或者一个文件~~");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }finally {
            /**
             * 关闭Scanner，文件输入流，套接字
             * 套接字装饰了输出流，所以不用关闭输出流
             */
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 强制将输入流置为空
                in = null;
            }
            try {
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 强制释放socket
                socket = null;
            }

        }
        System.out.println("文件传输完毕");
    }

    // 主布局中的UI组件
    Button button,cancel; // 加载、取消按钮
    private TextView text; // 更新的UI组件
    private ProgressBar progressBar; // 进度条

    /**
     * 步骤1：创建AsyncTask子类
     * 注：
     *   a. 继承AsyncTask类
     *   b. 为3个泛型参数指定类型；若不使用，可用java.lang.Void类型代替
     *      此处指定为：输入参数 = String类型、执行进度 = Integer类型、执行结果 = String类型
     *   c. 根据需求，在AsyncTask子类内实现核心方法
     */
    private class WriteToCloudTask extends AsyncTask<String, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            text.setText("加载中");
            // 执行前显示提示
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(String... params) {

            int count = 0;
            InputStream in = null;
            Socket socket = null;
            //String filename = "test_sjh.txt";
            String filename = "";


            //更新进度条
            count+=20;
            publishProgress(count);
            //使用循环写入
            int i=1;
            while(i<31){


                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    filename = getActivity().getExternalCacheDir().getAbsolutePath() + File.separator + params[i];
                }
                try {
                    /**
                     * 1.扫描控制台接收文件路径名
                     *   创建一个file引用，指向一个新的File对象，并给文件赋予地址
                     */
                    //System.out.println("请输入要传输文件的路径：");
                    //scan = new Scanner(System.in);
                    //String path = scan.nextLine();//文件路径path
                    File file = new File(filename);//filename为新的文件路径

                    //Toast.makeText(getActivity(),file.getName() , Toast.LENGTH_LONG).show();

                    /**
                     * 2.判断文件是文本文件而不是文件夹并且路径存在
                     *  exists()：判断文件是否存在
                     *  isFile()：判断是不是文件
                     */
                    if(file.exists() && file.isFile()) {


                        /**
                         * 3.创建文件输入流，发送文件
                         *   将文件输入的内容都放在file里面
                         */
                        in = new FileInputStream(file);

                        /**
                         * Socket 这个类实现客户端套接字(也称为“套接字”)。套接字是两台机器间通信的端点。
                         *
                         * 4.创建客户端套接字
                         */
                        socket = new Socket();
                        //InetSocketAddress Inets = new InetSocketAddress("127.0.0.1", 12345);


                        /**
                         * 5.连接TCP服务器
                         *       确定服务端的IP和端口号
                         */
                        try {
                            socket.connect(new InetSocketAddress("192.168.0.105", 12345));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //Toast.makeText(getActivity(),"1"+socket.isConnected() , Toast.LENGTH_LONG).show();
                        //System.out.println(""+socket.isConnected());


                        /**
                         * 6.获取到客户端的输出流
                         *   OutputStream     getOutputStream()
                         *                         返回此套接字的输出流。
                         */
                        OutputStream out = null;
                        try {
                            out = socket.getOutputStream();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        /**
                         * 7.向服务器发送文件
                         *   自己定义了一个协议来解决粘包现象，获取文件名
                         *   7.1.我们先将文件中的内容读取出来，放到file里面
                         *   7.2.先读文件名  file.getName()
                         *   7.3.将文件名转换成字节  file.getName().getBytes()
                         *   7.4.获取文件名的字节的长度  file.getName().getBytes().length
                         *   7.5.再在文件名长度的后面加上  \r\n 作为标识符
                         */
                        // 向服务器发送[文件名字节长度 \r\n]
                        out.write((file.getName().getBytes().length + "\r\n").getBytes());
                        // 向服务器发送[文件名字节]
                        out.write(file.getName().getBytes());
                        // 向服务器发送[文件字节长度\r\n]
                        out.write((file.length() + "\r\n").getBytes());
                        // 向服务器发送[文件字节内容]
                        byte[] data = new byte[1024];
                        int j = 0;
                        while((j = in.read(data)) != -1) {
                            out.write(data, 0, j);
                        }

                        //设置延时
                        Thread.sleep(100);
                        //更新进度条
                        count+=2;
                        publishProgress(count);

                    }else {
                        //Toast.makeText(getActivity(), "文件不存在或者一个文件~~", Toast.LENGTH_LONG).show();
                        //System.out.println("文件不存在或者一个文件~~");
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                i++;
            }

            //更新进度条
            count+=20;
            publishProgress(count);

            //关闭资源
            try {
                if(in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 强制将输入流置为空
                in = null;
            }
            try {
                if(socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                // 强制释放socket
                socket = null;
            }


            System.out.println("文件传输完毕");

            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {

            progressBar.setProgress(progresses[0]);
            text.setText("loading..." + progresses[0] + "%");

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            text.setText("写入云端完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {

            text.setText("已取消");
            progressBar.setProgress(0);

        }
    }

    private void initWords()
    {
        boolean flag = true;
        for(int i=1;i<31;i++)
        {
            //小于10则为01-09
            if(flag&&i<10){
                words[i]="unit_0"+i+".txt";
            }
            else{
                flag=false;
                words[i]="unit_"+i+".txt";
            }
        }
    }

    private class WriteToExternalTask extends AsyncTask<String, Integer, String> {

        // 方法1：onPreExecute（）
        // 作用：执行 线程任务前的操作
        @Override
        protected void onPreExecute() {
            text.setText("加载中");
            // 执行前显示提示
        }


        // 方法2：doInBackground（）
        // 作用：接收输入参数、执行任务中的耗时操作、返回 线程任务执行的结果
        // 此处通过计算从而模拟“加载进度”的情况
        @Override
        protected String doInBackground(String... params) {

            int count = 0;
            String filename;//文件名
            String content ;//文件内容
            //更新进度条
            count+=20;
            publishProgress(count);

            //循环写入
            int i=1;
            while(i<31)
            {
                try {
                    String storageState = Environment.getExternalStorageState();

                    //判断是否存在可用的的SD Card
                    if (storageState.equals(Environment.MEDIA_MOUNTED)) {

                        //路径： /storage/emulated/0/Android/data/com.yoryky.demo/cache/yoryky.txt
                        filename = getActivity().getExternalCacheDir().getAbsolutePath()  + File.separator + params[i];
                        FileOutputStream outputStream = new FileOutputStream(filename);
                        content="This is unit"+i;
                        outputStream.write(content.getBytes());
                        outputStream.close();
                        count+=2;
                        publishProgress(count);
                        //Toast.makeText(getActivity(), "写入完成"+filename, Toast.LENGTH_LONG).show();
                    }
                    else {
                        //Toast.makeText(getActivity(), "写入失败", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                }
                i++;
            }

            count+=20;
            publishProgress(count);
            System.out.println("文件写入完毕");
            return null;
        }

        // 方法3：onProgressUpdate（）
        // 作用：在主线程 显示线程任务执行的进度
        @Override
        protected void onProgressUpdate(Integer... progresses) {

            progressBar.setProgress(progresses[0]);
            text.setText("loading..." + progresses[0] + "%");

        }

        // 方法4：onPostExecute（）
        // 作用：接收线程任务执行结果、将执行结果显示到UI组件
        @Override
        protected void onPostExecute(String result) {
            // 执行完毕后，则更新UI
            text.setText("写入本地完毕");
        }

        // 方法5：onCancelled()
        // 作用：将异步任务设置为：取消状态
        @Override
        protected void onCancelled() {

            text.setText("已取消");
            progressBar.setProgress(0);

        }
    }
}
