package com.saiyajin.sensor.listener;

import android.app.Activity;
import android.hardware.Sensor;

public class SensorMonitorFactory {
    public static SensorMonitor createSensorMonitor(SensorsEnum type, Activity activity) {
        String[] description;
        switch (type) {
            case Accelerometer:
                description = new String[3];
                description[0] = "x方向上加速度为(m/s2)：";
                description[1] = "y方向上加速度为(m/s2)：";
                description[2] = "z方向上加速度为(m/s2)：";
                return new SensorMonitor(Sensor.TYPE_ACCELEROMETER, description, activity);
            case Gravity:
                description = new String[3];
                description[0] = "x方向上重力加速度为(m/s2)：";
                description[1] = "y方向上重力加速度为(m/s2)：";
                description[2] = "z方向上重力加速度为(m/s2)：";
                return new SensorMonitor(Sensor.TYPE_GRAVITY, description, activity);
            case Gyroscope:
                description = new String[3];
                description[0] = "x方向的角速度为(rad/s)：";
                description[1] = "y方向的角速度为(rad/s)：";
                description[2] = "z方向的角速度为(rad/s)：";
                return new SensorMonitor(Sensor.TYPE_GYROSCOPE, description, activity);
            case Light:
                description = new String[1];
                description[0] = "光的强度为(lux)：";
                return new SensorMonitor(Sensor.TYPE_LIGHT, description, activity);
            case LinearA:
                description = new String[3];
                description[0] = "x方向上线性加速度为(m/s2)：";
                description[1] = "y方向上线性加速度为(m/s2)：";
                description[2] = "z方向上线性加速度为(m/s2)：";
                return new SensorMonitor(Sensor.TYPE_LINEAR_ACCELERATION, description, activity);
            case Magnetic:
                description = new String[3];
                description[0] = "x方向的磁场分量为(uT)：";
                description[1] = "y方向的磁场分量为(uT)：";
                description[2] = "z方向的磁场分量为(uT)：";
                return new MagneticMonitor(SensorMonitor.FIXEDMAGNETIC, description, activity);
            case Orient:
                description = new String[3];
                description[0] = "手机的方位角azimuth为：";
                description[1] = "手机的倾斜角pitch为：";
                description[2] = "手机的旋转角roll为：";
                return new OrientMonitor(SensorMonitor.NEWORIENTATION, description, activity);
            case Pressure:
                description = new String[1];
                description[0] = "当前的压强为(hPa)：";
                return new SensorMonitor(Sensor.TYPE_PRESSURE, description, activity);
            case Proximity:
                description = new String[1];
                description[0] = "对象与手机的距离:";
                return new SensorMonitor(Sensor.TYPE_PROXIMITY, description, activity);
            case Rotation:
                description = new String[3];
                description[0] = "x方向的旋转矢量为：";
                description[1] = "y方向的旋转矢量为：";
                description[2] = "z方向的旋转矢量为：";
                return new SensorMonitor(Sensor.TYPE_ROTATION_VECTOR, description, activity);
            case Temperature:
                description = new String[1];
                description[0] = "当前的温度为(°C)：";
                return new SensorMonitor(Sensor.TYPE_AMBIENT_TEMPERATURE, description, activity);
            default:
                return null;
        }
    }
}
