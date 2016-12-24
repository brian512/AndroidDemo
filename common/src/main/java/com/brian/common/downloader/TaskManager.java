package com.brian.common.downloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 单个下载任务管理类
 * Created by huamm on 2016/12/7 0007.
 */

public class TaskManager {

    private TaskInfo mTaskInfo;

    TaskManager(TaskInfo taskInfo) {
        mTaskInfo = taskInfo;
    }

    void start() {
        mTaskInfo.state = DownloadState.STATE_DOWNLOADING;
    }

    void pause() {
        mTaskInfo.state = DownloadState.STATE_PAUSING;
    }

    void resume() {
        mTaskInfo.state = DownloadState.STATE_DOWNLOADING;
    }

    void cancel() {
        mTaskInfo.state = DownloadState.STATE_CANCELED;
    }

    public TaskInfo getTaskInfo() {
        return mTaskInfo;
    }

    private void initTaskInfo() {
        mTaskInfo.savePath = "";
        mTaskInfo.totalSize = getFileLength(mTaskInfo.url);
    }

    private int getFileLength(String url) {
        try {
            // 创建连接
            URL ur = new URL(url.trim()); // 先截断url的空格，避免上层传进来的url有空格
            HttpURLConnection conn = (HttpURLConnection) ur.openConnection();
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(15 * 1000);
            conn.setReadTimeout(15 * 1000);
            conn.connect();

            // 获取responseCopde
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                return conn.getContentLength();
            }
            return 0;
        } catch(IOException e) {
            return 0;
        }
    }
}
