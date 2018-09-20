#include <jni.h>
#include <string>
#include <malloc.h>
#include <memory.h>
#include <sys/stat.h>
#include <android/log.h>
#include <common.h>

#define TAG "leslam"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

jbyteArray as_byte_array(JNIEnv *env, unsigned char * buf, int len) {
    jbyteArray array = env->NewByteArray(len);
    env->SetByteArrayRegion(array, 0 , len, reinterpret_cast<jbyte*>(buf));
    return array;
}

unsigned char* as_unsigned_char_array(JNIEnv *env, jbyteArray array) {
    int len = env->GetArrayLength(array);
    unsigned char* buff = new unsigned char[len];
    env->GetByteArrayRegion(array, 0, len, reinterpret_cast<jbyte*>(buff));
    return buff;
}

extern "C"
JNIEXPORT jint JNICALL
Java_demo_cramel_com_anytest_Leslam_Init(
        JNIEnv *env,
        jobject /* this */) {

    LOGD(" Java_demo_cramel_com_anytest_Leslam_Init ");
    return slam_init();
}

extern "C"
JNIEXPORT jint JNICALL
Java_demo_cramel_com_anytest_Leslam_Start(
        JNIEnv *env,
        jobject /* this */) {
    LOGD(" Java_demo_cramel_com_anytest_Leslam_Start ");
    return start_slam();
}

extern "C"
JNIEXPORT jint JNICALL
Java_demo_cramel_com_anytest_Leslam_ModeSwitch(
        JNIEnv *env,
        jobject obj, jboolean mode) {
    LOGD(" Java_demo_cramel_com_anytest_Leslam_ModeSwitch ");
    return mode_switch(mode);
}


extern "C"
JNIEXPORT jint JNICALL
Java_demo_cramel_com_anytest_Leslam_GetSlamData(
        JNIEnv *env,
        jobject obj,jdoubleArray pos, jdoubleArray rot ) {

    jclass clss = env->GetObjectClass(obj);
    jboolean b;
    int result = 0;

    DataToService data;
    LOGD(" before get slam data");
    result = get_slam_data(&data, 0.0f);

    jdouble *pd2 = (jdouble*)env->GetPrimitiveArrayCritical( pos ,&b );
    for(int i=0;i<3;i++) {
        pd2[i] = data.dX[i];
    }
    env->ReleasePrimitiveArrayCritical( pos , pd2 , 0 );
    LOGD(" dx 2 : %lf", data.dX[2]);
    LOGD(" quat 2 : %lf", data.dQuaternion[2]);

    jdouble *pd3 = (jdouble*)env->GetPrimitiveArrayCritical( rot ,&b );
    for(int i=0;i<4;i++) {
        pd3[i] = data.dQuaternion[i];
    }
    env->ReleasePrimitiveArrayCritical( rot , pd3 , 0 );

    jfieldID f1 = env->GetFieldID(clss, "dTime","D");
    LOGD(" timestamp : %lf", data.dTime);
    env->SetDoubleField(obj, f1, data.dTime);

    return result;
}

