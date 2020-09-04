package com.sty.ne.okhttp;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnSyncGet;
    private Button btnAsyncGet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        addListeners();
    }

    private void initView() {
        btnSyncGet = findViewById(R.id.btn_sync_get);
        btnAsyncGet = findViewById(R.id.btn_async_get);
    }

    private void addListeners() {
        btnSyncGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpSyncGet();
            }
        });
        btnAsyncGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                okhttpAsyncGet();
            }
        });
    }

    /**
     * OKHTTP同步请求的方法
     */
    private void okhttpSyncGet() {
        //构建者模式
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //GET请求
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .get()
                .build();
        //Call == RealCall
        final Call call = okHttpClient.newCall(request);

        //取消请求
        //call.cancel();

        //同步方法 --> 耗时操作，需要自己开启子线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = call.execute();
                    String str = response.body().string();
                    //response.body().byteStream();
                    //response.body().charStream();
                    Log.d(TAG, "同步get请求成功： " + str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * OKHTTP异步请求的方法
     */
    private void okhttpAsyncGet() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //GET请求
        Request request = new Request.Builder()
                .url("https://www.baidu.com")
                .get()
                .build();
        Call call = okHttpClient.newCall(request);
        //取消请求
        //call.cancel();

        //异步方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(TAG, "请求失败： " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                //response.body().byteStream();
                //response.body().charStream();
                Log.d(TAG, "异步get请求成功：" + str);
            }
        });
    }
}