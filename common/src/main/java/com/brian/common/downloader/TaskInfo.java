package com.brian.common.downloader;

import java.util.Comparator;
import java.util.List;

/**
 * 下载文件信息
 * Created by huamm on 2016/12/7 0007.
 */

public class TaskInfo implements Comparator {

    public static final int PRIORITY_NORMAL = 50;

    String url;

    String savePath;

    int completedSize;

    int totalSize;

    int priority;

    int state;

    String callTag;

    IDownloadCallback callback;

    List<WorkInfo> mTaskInfos;

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof  TaskInfo && o2 instanceof TaskInfo) {
            return ((TaskInfo)o1).priority - ((TaskInfo)o2).priority;
        }
        return 0;
    }
}
