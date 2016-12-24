package com.brian.common.downloader;

import java.io.Serializable;

/**
 * 单个线程的下载任务信息
 * Created by huamm on 2016/12/6 0006.
 */

public class WorkInfo implements Serializable {

    int startPos;

    int endPos;

    int completeSize;
}
