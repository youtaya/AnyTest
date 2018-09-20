#ifndef SLAM_COMMON_H
#define SLAM_COMMON_H

typedef struct
{
        double dTime;
        double dX[3];
       	double dQuaternion[4];
} DataToService;


extern "C" bool start_slam();
extern "C" bool stop_slam();
extern "C" bool get_slam_data(DataToService*, float prediction_time = 0.0f);
extern "C" bool mode_switch(bool mode);
extern "C" int slam_init();
extern "C" int slam_running_status();

#endif
