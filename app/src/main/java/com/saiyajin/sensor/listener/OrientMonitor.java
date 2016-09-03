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
    private float currentori;
    private ImageView imageView;
    float[] avalues;
    float[] mvalues;

    OrientMonitor(int SensorInt, String[] description, Activity activity) {
        super(SensorInt, description, activity);
        avalues = new float[3];
        mvalues = new float[3];
        imageView = new ImageView(activity);
        imageView.setImageResource(R.drawable.compass);
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.monitorlayout);
        layout.addView(imageView);
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
            SensorManager.getOrientation(r, values);
            values[0] = (float) Math.toDegrees(values[0]);
            values[1] = (float) Math.toDegrees(values[1]);
            values[2] = (float) Math.toDegrees(values[2]);
        }
        show();
    }

    @Override
    public void show() {
        super.show();
        float ori = values[0];
        if (ori < 0)
            ori = ori + 360;
        RotateAnimation ra = new RotateAnimation(currentori, -ori, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(200);
        ra.setFillAfter(true);
        imageView.startAnimation(ra);
        currentori = -ori;
    }
}
