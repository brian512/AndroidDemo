LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := libffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ffmpegkit
LOCAL_SRC_FILES := com_common_ffmpegkit_FFmpegKit.c ffmpeg.c ffmpeg_opt.c cmdutils.c ffmpeg_filter.c
# 这里的地址改成自己的 FFmpeg 源码目录
LOCAL_C_INCLUDES := E:\Java\ffmpeg-3.2.4
LOCAL_LDLIBS := -llog -lz -ldl -landroid
LOCAL_SHARED_LIBRARIES := ffmpeg
include $(BUILD_SHARED_LIBRARY)