package com.brian.common.downloader;

/**
 * 下载回调
 * Created by huamm on 2016/12/7 0007.
 */

public interface IDownloadCallback {
    void onProgress(int state, int progress);

    void onFinished(String savePath);

    void onError(int errorCode, String message);
}
