package com.brian.testandroid.util;

public class PathConfig {

    // PS：注意程序中所有出现的路径，结尾都不带 "/"

    /**
     * 发帖时，图片、视频的输出目录
     */
    public static String getCameraOutputDir() {
        String dir = PathUtil.getCacheDir() + "/camera";
        FileUtil.ensureFolderExists(dir);
        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        // 如果实在是创建失败则直接返回缓存根目录
        return PathUtil.getCacheDir();
    }

    /**
     * 视频封面保存到本地
     */
    public static String getVideoPosterOutputDir() {
        String dir = PathUtil.getCacheDir() + "/poster";
        FileUtil.ensureFolderExists(dir);
        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        // 如果实在是创建失败则直接返回缓存根目录
        return PathUtil.getCacheDir();
    }

    /**
     * 发帖时，拍照输出路径（相册选择照片发送，也存这个路径）
     */
    public static String getCameraOutputPicPath() {
        return getCameraOutputDir() + "/pic.jpg";
    }

    /**
     * 发帖时，视频输出路径
     */
    public static String getCameraOutputVideoPath() {
        return getCameraOutputDir() + "/video.mp4";
    }



    /**
     * 发帖时，图片、视频的进行上传前，作为临时上传文件的保存目录
     *
     * @return
     */
    public static String getMediaUploadDir() {
        String dir = PathUtil.getCacheDir() + "/upload";
        FileUtil.ensureFolderExists(dir);
        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        // 如果实在是创建失败则直接返回缓存根目录
        return PathUtil.getCacheDir();
    }

    /**
     * 发帖时，图片、视频的进行上传前，如果需要保存图片、视频的话，则单独保存的目录
     * PS：此时文件会存在两份，一份在缓存中（可清理），一份在保存目录下
     *
     * @return
     */
    public static String getMediaSaveDir() {
        // 如果不行，再到data目录下创建缓存目录 PS：问题在于程序被卸载后，照片也会被删除
        String dir = PathUtil.getCacheDir() + "/save";
        FileUtil.ensureFolderExists(dir);
        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        // 如果实在是创建失败则直接返回缓存根目录
        return PathUtil.getCacheDir();
    }

    /**
     * 聊天录音的路径
     *
     * @return
     */
    public static String getAudioDir() {
        String dir = PathUtil.getCacheDir() + "/audio";
        FileUtil.ensureFolderExists(dir);

        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        return PathUtil.getCacheDir();
    }

    /**
     * 视频录制完成后音乐保存的目录
     * @return
     */
    public static String getMusicOutputDir(){
        String dir = PathUtil.getCacheDir() + "/music";
        FileUtil.ensureFolderExists(dir);

        if (FileUtil.isDirExist(dir)) {
            return dir;
        }

        return PathUtil.getCacheDir();
    }
}
