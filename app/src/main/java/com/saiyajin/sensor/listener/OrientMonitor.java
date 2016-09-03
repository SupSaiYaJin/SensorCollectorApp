package com.saiyajin.sensor.listener;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.saiyajin.sensor.R;

/**
 * Created by SaiYa on 15/12/3.
 */
public class OrientMonitor extends SensorMonitor {
    private float[] accValues;
    private float[] magValues;

    public OrientMonitor(int sensorInt, SensorChangedListener listener) {
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
            SensorManager.getOrientation(r, mValues);
            mValues[0] = (float) Math.toDegrees(mValues[0]);
            mValues[1] = (float) Math.toDegrees(mValues[1]);
            mValues[2] = (float) Math.toDegrees(mValues[2]);
            if (mListener != null) {
                mListener.onSensorChanged(mValues);
            }
        }
    }

}
