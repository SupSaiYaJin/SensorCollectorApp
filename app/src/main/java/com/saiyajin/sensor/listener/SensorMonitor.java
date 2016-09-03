package com.saiyajin.sensor.listener;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.TextView;

import com.saiyajin.sensor.R;

import java.text.DateFormat;
import java.util.Date;

public class SensorMonitor implements SensorEventListener{
	public final static int NEWORIENTATION = 10000;
	public final static int FIXEDMAGNETIC = 10001;

	protected String[] description;
	protected int SensorInt;
    protected TextView textView;
	protected float[] values;

    SensorMonitor(int SensorInt, String[] description, Activity activity) {
        this.SensorInt = SensorInt;
        this.description = description;
        textView = (TextView)activity.findViewById(R.id.monitorview);
        values = new float[description.length];
    }

	@Override
	public void onAccuracyChanged(Sensor sensor,int accuracy){}
	
	@Override
	public void onSensorChanged(SensorEvent event)
	{
		if(event.sensor.getType() == SensorInt)
		{
			for(int i = 0;i < values.length; ++i)
				values[i] = event.values[i];
            show();
		}
    }

    public void show(){StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < description.length; ++i) {
            stringBuilder.append(description[i]);
            stringBuilder.append(values[i]);
            stringBuilder.append('\n');
        }
        textView.setText(stringBuilder.toString());
    }

	public int getSensorInt(){
		return SensorInt;
	}
	
	public float[] getValues(){
		return values.clone();
	}
		
	public String getSaveString(int count){
		Date now = new Date();
		DateFormat timeformat = DateFormat.getDateTimeInstance();
		String time = timeformat.format(now);
		String ret = count + "," + time + "\n";
		for(float v : values)
			ret += count + "," + v + "\n";
		return ret;
	}
}