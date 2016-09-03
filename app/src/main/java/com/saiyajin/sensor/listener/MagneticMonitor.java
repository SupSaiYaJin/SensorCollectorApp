package com.saiyajin.sensor.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

/**
 * Created by SaiYa on 15/12/3.
 */
public class MagneticMonitor extends SensorMonitor {

    private float[] accValues;
    private float[] magValues;

    public MagneticMonitor(int sensorInt, SensorChangedListener listener) {
        super(sensorInt, listener);
        accValues = new float[3];
        magValues = new float[3];
        mValues = new float[3];
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                accValues[0] = event.values[0];
                accValues[1] = event.values[1];
                accValues[2] = event.values[2];
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magValues[0] = event.values[0];
                magValues[1] = event.values[1];
                magValues[2] = event.values[2];
                break;
            default:
                break;
        }
        float[] r = new float[9];
        if (SensorManager.getRotationMatrix(r, null, accValues, magValues)) {
            mValues[0] = r[0] * magValues[0] + r[1] * magValues[1] + r[2] * magValues[2];
            mValues[1] = r[3] * magValues[0] + r[4] * magValues[1] + r[5] * magValues[2];
            mValues[2] = r[6] * magValues[0] + r[7] * magValues[1] + r[8] * magValues[2];
            if (mListener != null) {
                mListener.onSensorChanged(mValues);
            }
        }
    }

}
