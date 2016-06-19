package com.lang.downloaddemo.com.lang.db;

import com.lang.downloaddemo.com.lang.entities.ThreadInfo;

import java.util.List;

/**
 * Created by Administrator on 2016/6/19 0019.
 */
public interface ThreadDAO {
    /**
     * 插入线程信息
     * @param threadInfo
     */
    public void insertThread(ThreadInfo threadInfo);

    /**
     *
     * @param url
     * @param thread_id
     */
    public void deleteThread(String url,int thread_id);
    public List<ThreadInfo> getThreads(String url);
    //查询文件的线程信息
    public void updateThread(String url,int thread_id,int finished);

    public boolean isExists(String url,int thread_id);
}
