package com.lang.downloaddemo.com.lang.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.lang.downloaddemo.com.lang.entities.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/6/18 0018.
 */
public class DownloadService extends Service {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_UPDATE = "ACTION_UPDATE";
    public static final String TAG = "DownloadService";
    public static final int MSG_INIT=0;
    private DownloadTask mTask=null;
    public  static final String DOWNLOAD_PATH= Environment.getExternalStorageDirectory().getAbsolutePath()+"/download/";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_START.equals(intent.getAction())) {

            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.d(TAG, "start:" + fileInfo.toString());
            new InitThread(fileInfo).start();
        } else if (ACTION_STOP.equals(intent.getAction())) {

            FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
            Log.d(TAG, "stop:" + fileInfo.toString());
            if (mTask != null) {
                mTask.isPause = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_INIT:
                    FileInfo fileInfo= (FileInfo) msg.obj;
                    Log.d(TAG,fileInfo.toString() );
                    mTask = new DownloadTask(DownloadService.this, fileInfo);
                    mTask.download();
                    break;
            }

        }
    };
    /**
     * 初始化的子线程
     */
    class InitThread extends Thread{
        private FileInfo mFileInfo=null;
        public  HttpURLConnection conn;
        public  RandomAccessFile raf;
        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {

          //连接网络文件
            try {
                URL url = new URL(mFileInfo.getUrl());
                 conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length;
                if(conn.getResponseCode()==HttpURLConnection.HTTP_OK){
                   length= conn.getContentLength();
                    if(length<=0){
                        return;
                        //获得文件长度
                    }
                    File dir = new File(DOWNLOAD_PATH);
                    if(!dir.exists()){
                        dir.mkdir();
                    }
                    File file = new File(dir, mFileInfo.getFileName());
                   raf = new RandomAccessFile(file,"rwd");
                    raf.setLength(length);
                    mFileInfo.setLength(length);
                    handler.obtainMessage(MSG_INIT,mFileInfo).sendToTarget();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
          //在本地创建文件
          //创建文件长度
        }
    }
}
