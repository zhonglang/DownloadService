package com.lang.downloaddemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lang.downloaddemo.com.lang.entities.FileInfo;
import com.lang.downloaddemo.com.lang.service.DownloadService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvFileName;
    private ProgressBar mPbProgress;
    private Button mBtnStart;
    private Button mBtnStop;
    private String url = "http://www.imooc.com/mobile/mukewang.apk";
    private   FileInfo fileInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvFileName = (TextView) findViewById(R.id.tvFileName);
        mPbProgress = (ProgressBar) findViewById(R.id.pbProgress);
        mBtnStart = (Button) findViewById(R.id.btndownload);
        mBtnStop = (Button) findViewById(R.id.btnstop);
        mPbProgress.setMax(100);
       fileInfo = new FileInfo(0, url, "mukewang.apk", 0, 0);
        mTvFileName.setText(fileInfo.getFileName());
        mBtnStart.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);
//注册广播接收器

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.ACTION_UPDATE);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndownload:
                Intent downlodaIntent = new Intent(MainActivity.this, DownloadService.class);
                downlodaIntent.setAction(DownloadService.ACTION_START);
                downlodaIntent.putExtra("fileInfo", fileInfo);
                startService(downlodaIntent);
                break;
            case R.id.btnstop:
                Intent stopIntent = new Intent(MainActivity.this, DownloadService.class);
                stopIntent.setAction(DownloadService.ACTION_STOP);
                stopIntent.putExtra("fileInfo", fileInfo);
                stopService(stopIntent);
                break;
        }
    }
    BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (DownloadService.ACTION_UPDATE.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished", 0);
                mPbProgress.setProgress(finished);
            }
        }
    };
}
