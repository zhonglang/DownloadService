package com.lang.downloaddemo.com.lang.service;

import android.content.Context;
import android.content.Intent;

import com.lang.downloaddemo.com.lang.db.ThreadDAOImpl;
import com.lang.downloaddemo.com.lang.entities.FileInfo;
import com.lang.downloaddemo.com.lang.entities.ThreadInfo;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Administrator on 2016/6/19 0019.
 */
public class DownloadTask {
    private Context mContext=null;
    private FileInfo mFileInfo=null;
    private ThreadDAOImpl mDao=null;
    private int mFinished=0;
    public boolean isPause=false;

    public DownloadTask(Context mContext,FileInfo mFileInfo) {
        this.mContext = mContext;
        this.mFileInfo=mFileInfo;
        mDao = new ThreadDAOImpl(mContext);
    }
    public void download(){
        //读取数据库的线程信息
        List<ThreadInfo> threadInfos = mDao.getThreads(mFileInfo.getUrl());
        ThreadInfo threadInfo=null;
        if (threadInfos.size()==0) {
            //初始化线程对象信息
            threadInfo = new ThreadInfo(0, mFileInfo.getUrl(), 0, mFileInfo.getLength(), 0);

        }else {
            threadInfo = threadInfos.get(0);
        }
        new DownloadThread(threadInfo).start();
    }
    /**
     * 下载线程
     */
    class DownloadThread extends Thread{
        private ThreadInfo mThreadInfo = null;

        public DownloadThread(ThreadInfo mThreadInfo) {
            this.mThreadInfo = mThreadInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn=null;
            InputStream inputStream=null;
            RandomAccessFile raf=null;

            //向数据库插入线程信息
            if (!mDao.isExists(mThreadInfo.getUrl(),mThreadInfo.getId())){
                mDao.insertThread(mThreadInfo);
            }
            try {
                URL url =new URL(mThreadInfo.getUrl());
                conn= (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                //设置下载位置
                int start=mThreadInfo.getStart()+mThreadInfo.getFinished();
                conn.setRequestProperty("Range","bytes="+start+"-"+mThreadInfo.getEnd());
                //设置文件写入位置
                File file=new File(DownloadService.DOWNLOAD_PATH,mFileInfo.getFileName());
                 raf = new RandomAccessFile(file,"rwd");
                raf.seek(start);
                Intent intent = new Intent(DownloadService.ACTION_UPDATE);
                mFinished += mThreadInfo.getFinished();
                //开始下载
if (conn.getResponseCode()==HttpURLConnection.HTTP_PARTIAL) {
    inputStream = conn.getInputStream();
    byte[] buffer = new byte[1024 * 4];
    int len = -1;
    //读取数据
    long time = System.currentTimeMillis();
    while ((len = inputStream.read(buffer)) != -1) {
        //写入文件
        raf.write(buffer, 0, len);
        mFinished += len;
        //将进度以广播的形式传给Activity
        if (System.currentTimeMillis() - time > 500) {
            time = System.currentTimeMillis();
            intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());
            mContext.sendBroadcast(intent);
        }
        //下载暂停，保存下载进度
        if (isPause) {
            mDao.updateThread(mThreadInfo.getUrl(), mThreadInfo.getId(), mThreadInfo.getFinished());
            return;
        }


    }
    //删去线程信息
    mDao.deleteThread(mThreadInfo.getUrl(), mThreadInfo.getId());
}
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    conn.disconnect();
                    inputStream.close();
                    raf.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
