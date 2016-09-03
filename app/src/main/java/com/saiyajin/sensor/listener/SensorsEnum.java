package com.saiyajin.sensor.listener;

public enum SensorsEnum {
    Accelerometer("加速度传感器"),
    Orient("方向传感器"),
    Magnetic("磁场传感器"),
    Temperature("温度传感器"),
    Light("光线传感器"),
    Gyroscope("陀螺仪传感器"),
    Pressure("压力传感器"),
    Proximity("距离传感器"),
    Gravity("重力传感器"),
    LinearA("线性加速度传感器"),
    Rotation("旋转矢量传感器"),
    FixedMagnetic("地理磁场传感器");

    private String mTitle;

    SensorsEnum(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

}