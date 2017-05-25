#include <jni.h>
#include "JNILog.h"
#include "com_common_ffmpegkit_FFmpegKit.h"
#include "ffmpeg.h"

/*
 * Class:     com_common_ffmpegkit_FFmpegKit
 * Method:    run
 * Signature: ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_common_ffmpegkit_FFmpegKit_run
  (JNIEnv *env, jclass obj, jobjectArray commands)
{
    if (commands == NULL)
    {
        return -1;
    }

    int argc = (*env)->GetArrayLength(env, commands);
    char **argv = (char **) malloc(sizeof(char *) * argc);
    jstring *strr = (jstring *) malloc(sizeof(jstring) * argc);

    int i = 0;
    for(i=0; i<argc; i++)
    {
        strr[i] = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char*) (*env)->GetStringUTFChars(env, strr[i], 0);
    }

    LOGI("try ffmpeg main: ");
    int ret = ffmpeg_run(argc, argv);

    for(i=0;i<argc;i++)
    {
        (*env)->ReleaseStringUTFChars(env, strr[i], argv[i]);
    }
    free(argv);
    free(strr);
    return ret;
}
