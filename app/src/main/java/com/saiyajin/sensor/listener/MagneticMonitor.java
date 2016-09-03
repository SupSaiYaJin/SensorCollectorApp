package com.saiyajin.sensor.listener;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by SaiYa on 15/12/3.
 */
public class MagneticMonitor extends SensorMonitor{

    private float[] avalues;
    private float[] mvalues;

    MagneticMonitor(int SensorInt, String[] description, Activity activity) {
        super(SensorInt, description, activity);
        avalues = new float[3];
        mvalues = new float[3];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                avalues[0] = event.values[0];
                avalues[1] = event.values[1];
                avalues[2] = event.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                mvalues[0] = event.values[0];
                mvalues[1] = event.values[1];
                mvalues[2] = event.values[2];
                break;
            default:
                break;
        }
        float[] r = new float[9];
        if (SensorManager.getRotationMatrix(r, null, avalues, mvalues)) {
            values[0] = r[0] * mvalues[0] + r[1] * mvalues[1] + r[2] * mvalues[2];
            values[1] = r[3] * mvalues[0] + r[4] * mvalues[1] + r[5] * mvalues[2];
            values[2] = r[6] * mvalues[0] + r[7] * mvalues[1] + r[8] * mvalues[2];
            super.show();
        }
    }

}
