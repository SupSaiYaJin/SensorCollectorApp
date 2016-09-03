package com.saiyajin.sensor.listener;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import java.text.DateFormat;
import java.util.Date;

public class SensorMonitor implements SensorEventListener {
    public final static int NEWORIENTATION = 10000;
    public final static int FIXEDMAGNETIC = 10001;

    public interface SensorChangedListener {
        void onSensorChanged(float[] values);
    }

    private int mSensorInt;
    float[] mValues;
    SensorChangedListener mListener;

    public SensorMonitor(int sensorInt, SensorChangedListener listener) {
        mSensorInt = sensorInt;
        mListener = listener;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == mSensorInt) {
            mValues = event.values;
            if (mListener != null) {
                mListener.onSensorChanged(mValues);
            }
        }
    }

    public int getSensorInt() {
        return mSensorInt;
    }

    public float[] getValues() {
        return mValues.clone();
    }

    public String getSaveString(int count) {
        Date now = new Date();
        DateFormat timeformat = DateFormat.getDateTimeInstance();
        String time = timeformat.format(now);
        String ret = count + "," + time + "\n";
        for (float v : mValues)
            ret += count + "," + v + "\n";
        return ret;
    }
}