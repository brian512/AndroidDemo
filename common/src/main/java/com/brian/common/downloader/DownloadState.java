package com.brian.common.downloader;

/**
 * 下载状态
 * Created by huamm on 2016/12/7 0007.
 */

public class DownloadState {
    public static final int STATE_IDEL = 0;
    public static final int STATE_WAITING = 1;
    public static final int STATE_DOWNLOADING = 2;
    public static final int STATE_PAUSING = 3;
    public static final int STATE_CANCELED = 4;
    public static final int STATE_FINISHED = 5;
}
