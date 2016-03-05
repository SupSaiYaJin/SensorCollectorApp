package com.saiyajin.sensor.activities;

import com.saiyajin.sensor.R;
import com.saiyajin.sensor.utils.DBHelper;

import android.app.ActionBar;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MultiScanActivity extends Activity {

	private SensorManager SM;
	private CheckBox checkAcc;
	private CheckBox checkMag;
	private CheckBox checkOri;
	private CheckBox checkGyr;
	private CheckBox checkGra;
	private CheckBox checkLin;
	private CheckBox checkRot;
	private TextView countText;
	private EditText setInterval;
	private Handler handler;
	private Runnable runnable;
	private DBHelper sqlHelper;
	int count = 0;
	int interval = 1000;
	float accValues[] = new float[3];
	float magValues[] = new float[3];
	float oriValues[] = new float[3];
	float gyrValues[] = new float[3];
	float graValues[] = new float[3];
	float linValues[] = new float[3];
	float rotValues[] = new float[3];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		SM = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiscan);

		checkAcc = (CheckBox) findViewById(R.id.chkacc);
		checkMag = (CheckBox) findViewById(R.id.chkmag);
		checkOri = (CheckBox) findViewById(R.id.chkori);
		checkGyr = (CheckBox) findViewById(R.id.chkgyr);
		checkGra = (CheckBox) findViewById(R.id.chkgra);
		checkLin = (CheckBox) findViewById(R.id.chklin);
		checkRot = (CheckBox) findViewById(R.id.chkrot);
		countText = (TextView) findViewById(R.id.count);
		setInterval = (EditText) findViewById(R.id.setinterval);
		Button start = (Button) findViewById(R.id.start);
		Button clearCount = (Button) findViewById(R.id.clearcount);
		Button stop = (Button) findViewById(R.id.stop);
		start.setOnClickListener(new buttonListener());
		clearCount.setOnClickListener(new buttonListener());
		stop.setOnClickListener(new buttonListener());

		countText.setText(String.format("已存储%d组数据", count));

		sqlHelper = new DBHelper(MultiScanActivity.this);
		// mySEL.onAccuracyChanged(SM.getDefaultSensor(Sensor.TYPE_ALL),
		// SensorManager.SENSOR_STATUS_ACCURACY_HIGH);

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				handler.postDelayed(runnable, interval);
				if (checkAcc.isChecked())
					sqlHelper.insert(Sensor.TYPE_ACCELEROMETER,
							System.currentTimeMillis(), accValues);
				if (checkMag.isChecked())
					sqlHelper.insert(Sensor.TYPE_MAGNETIC_FIELD,
							System.currentTimeMillis(), magValues);
				if (checkOri.isChecked())
					sqlHelper.insert(3,
							System.currentTimeMillis(), oriValues);
				if (checkGyr.isChecked())
					sqlHelper.insert(Sensor.TYPE_GYROSCOPE,
							System.currentTimeMillis(), gyrValues);
				if (checkGra.isChecked())
					sqlHelper.insert(Sensor.TYPE_GRAVITY,
							System.currentTimeMillis(), graValues);
				if (checkLin.isChecked())
					sqlHelper.insert(Sensor.TYPE_LINEAR_ACCELERATION,
							System.currentTimeMillis(), linValues);
				if (checkRot.isChecked())
					sqlHelper.insert(Sensor.TYPE_ROTATION_VECTOR,
							System.currentTimeMillis(), rotValues);
				count++;
				countText.setText(String.format("已存储%d组数据", count));
			}
		};
	}

	private class buttonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.start:
				if (TextUtils.isEmpty(setInterval.getText().toString()))
					Toast.makeText(MultiScanActivity.this, "请输入扫描间隔！",
							Toast.LENGTH_SHORT).show();
				else {
					interval = Integer.parseInt(setInterval.getText()
							.toString());
					handler.post(runnable);
					Toast.makeText(MultiScanActivity.this, "每" + interval + "ms扫描一次",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case R.id.clearcount:
				count = 0;
				countText.setText(String.format("已存储%d组数据", count));
				Toast.makeText(MultiScanActivity.this, "计数已清零", Toast.LENGTH_SHORT)
						.show();
				break;
			case R.id.stop:
				handler.removeCallbacks(runnable);
				Toast.makeText(MultiScanActivity.this, "扫描已停止", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}

		}
	}

	private SensorEventListener mySEL = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			/*
			 * switch(accuracy){ case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
			 * Toast.makeText(MultiScanActivity.this, "已选择精确度为高！",
			 * Toast.LENGTH_SHORT).show(); break; case
			 * SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
			 * Toast.makeText(MultiScanActivity.this, "已选择精确度为中！",
			 * Toast.LENGTH_SHORT).show(); break; case
			 * SensorManager.SENSOR_STATUS_ACCURACY_LOW:
			 * Toast.makeText(MultiScanActivity.this, "已选择精确度为低！",
			 * Toast.LENGTH_SHORT).show(); break; case
			 * SensorManager.SENSOR_STATUS_UNRELIABLE:
			 * Toast.makeText(MultiScanActivity.this, "已选择精确度为不可靠！",
			 * Toast.LENGTH_SHORT).show(); break; default: break; }
			 */
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
			case Sensor.TYPE_GYROSCOPE:
				gyrValues[0] = event.values[0];
				gyrValues[1] = event.values[1];
				gyrValues[2] = event.values[2];
				break;
			case Sensor.TYPE_GRAVITY:
				graValues[0] = event.values[0];
				graValues[1] = event.values[1];
				graValues[2] = event.values[2];
				break;
			case Sensor.TYPE_LINEAR_ACCELERATION:
				linValues[0] = event.values[0];
				linValues[1] = event.values[1];
				linValues[2] = event.values[2];
				break;
			case Sensor.TYPE_ROTATION_VECTOR:
				rotValues[0] = event.values[0];
				rotValues[1] = event.values[1];
				rotValues[2] = event.values[2];
				break;
			default:
				break;
			}
			if (magValues != null && accValues != null) {
				float[] r = new float[9];
				if (SensorManager.getRotationMatrix(r, null, accValues, magValues)) {
					SensorManager.getOrientation(r, oriValues);
				}
			}
		}
	};

	@Override
	protected void onResume() {
		super.onResume();
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_FASTEST);
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
				SensorManager.SENSOR_DELAY_FASTEST);
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_GRAVITY),
				SensorManager.SENSOR_DELAY_FASTEST);
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
				SensorManager.SENSOR_DELAY_FASTEST);
		SM.registerListener(mySEL, SM.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	@Override
	protected void onPause() {
		SM.unregisterListener(mySEL);
		handler.removeCallbacks(runnable);
		super.onPause();
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { MenuInflater
	 * inflater = new MenuInflater(this); inflater.inflate(R.menu.multimenu,
	 * menu); return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		/*
		 * case R.id.setaccuracy: AlertDialog.Builder interd = new
		 * AlertDialog.Builder(MultiScanActivity.this); interd.setTitle("选择数据精确度")
		 * .setItems(new CharSequence[] { "高", "中", "低", "不可靠" }, new
		 * DialogInterface.OnClickListener() {
		 * 
		 * @Override public void onClick(DialogInterface dialog, int which) {
		 * switch (which) { case 0:
		 * mySEL.onAccuracyChanged(SM.getDefaultSensor(Sensor.TYPE_ALL),
		 * SensorManager.SENSOR_STATUS_ACCURACY_HIGH); break; case 1:
		 * mySEL.onAccuracyChanged(SM.getDefaultSensor(Sensor.TYPE_ALL),
		 * SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM); break; case 2:
		 * mySEL.onAccuracyChanged(SM.getDefaultSensor(Sensor.TYPE_ALL),
		 * SensorManager.SENSOR_STATUS_ACCURACY_LOW); break; case 3:
		 * mySEL.onAccuracyChanged(SM.getDefaultSensor(Sensor.TYPE_ALL),
		 * SensorManager.SENSOR_STATUS_UNRELIABLE); break; default: break; } }
		 * }).show().setCanceledOnTouchOutside(false); break;
		 */
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}
