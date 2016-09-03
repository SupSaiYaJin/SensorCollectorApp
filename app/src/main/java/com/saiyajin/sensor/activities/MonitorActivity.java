package com.saiyajin.sensor.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.saiyajin.sensor.R;
import com.saiyajin.sensor.listener.MagneticMonitor;
import com.saiyajin.sensor.listener.OrientMonitor;
import com.saiyajin.sensor.listener.SensorMonitor;
import com.saiyajin.sensor.listener.SensorsEnum;
import com.saiyajin.sensor.utils.DBHelper;
import com.saiyajin.sensor.utils.Utils;

import java.io.File;

public class MonitorActivity extends AbsActivity {
    private DBHelper mDataBaseHelper;
    private String mFileName;
    private SensorManager mSensorManager;
    private SensorMonitor mMonitor;
    private int mCount = 0;
    private boolean mIsSaveData = false;
    private Handler mHandler;
    private Runnable mRunnable;
    private long mInterval = 2000;
    private TextView mMonitorSave;
    private TextView mMonitorView;
    private LinearLayout mMonitorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor_activity);
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        mDataBaseHelper = new DBHelper(this);
        mMonitorSave = (TextView) findViewById(R.id.txt_monitor_save);
        mMonitorView = (TextView) findViewById(R.id.txt_monitor_view);
        mMonitorLayout = (LinearLayout) findViewById(R.id.monitor_layout);
        TextView titleView = (TextView) findViewById(R.id.txt_title);
        mMonitorSave.setText(String.format("已存储%d个数据！", mCount));
        SensorsEnum sensorType = (SensorsEnum) this.getIntent().getSerializableExtra("SensorType");
        titleView.setText(sensorType.getTitle());
        mFileName = sensorType.toString() + "Data.txt";
        mMonitor = createSensorMonitor(sensorType);
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.postDelayed(mRunnable, mInterval);
                ++mCount;
                Utils.saveFile(mMonitor.getSaveString(mCount), mFileName);
                mDataBaseHelper.insert(mMonitor.getSensorInt(), System.currentTimeMillis(), mMonitor.getValues());
                mMonitorSave.setText(String.format("已存储%d个数据！", mCount));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMonitor.getSensorInt() == SensorMonitor.NEWORIENTATION || mMonitor.getSensorInt() == SensorMonitor.FIXEDMAGNETIC) {
            mSensorManager.registerListener(mMonitor, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mMonitor, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        } else {
            mSensorManager.registerListener(mMonitor, mSensorManager.getDefaultSensor(mMonitor.getSensorInt()), SensorManager.SENSOR_DELAY_UI);
        }
        if (mIsSaveData) {
            mHandler.post(mRunnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacks(mRunnable);
        mSensorManager.unregisterListener(mMonitor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.datasave, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.singlesave:
                mDataBaseHelper.insert(mMonitor.getSensorInt(), System.currentTimeMillis(), mMonitor.getValues());
                ++mCount;
                Utils.saveFile(mMonitor.getSaveString(mCount), mFileName);
                Toast.makeText(MonitorActivity.this, "单次扫描成功", Toast.LENGTH_SHORT)
                        .show();
                mMonitorSave.setText(String.format("已存储%d个数据！", mCount));
                break;
            case R.id.ifsavedata:
                if (mIsSaveData) {
                    mIsSaveData = false;
                    mHandler.removeCallbacks(mRunnable);
                    Toast.makeText(MonitorActivity.this, "连续数据存储已关闭",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mIsSaveData = true;
                    Toast.makeText(MonitorActivity.this, "连续数据存储已开启",
                            Toast.LENGTH_SHORT).show();
                    mHandler.post(mRunnable);
                }
                break;
            case R.id.counttozero:
                mCount = 0;
                Toast.makeText(MonitorActivity.this, "计数已清零", Toast.LENGTH_SHORT)
                        .show();
                mMonitorSave.setText(String.format("已存储%d个数据！", mCount));
                break;
            case R.id.setinterval:
                AlertDialog.Builder intervalSelection = new AlertDialog.Builder(
                        MonitorActivity.this);
                intervalSelection.setTitle("选择扫描频率")
                        .setItems(new CharSequence[]{"1s", "2s", "3s", "4s"},
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        switch (which) {
                                            case 0:
                                                mInterval = 1000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每1s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 1:
                                                mInterval = 2000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每2s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 2:
                                                mInterval = 3000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每3s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 3:
                                                mInterval = 4000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每4s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                }).show().setCanceledOnTouchOutside(false);
                break;
            case R.id.senddata:
                File file = new File(Environment.getExternalStorageDirectory(), mFileName);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                share.setType("*/*");
                startActivity(Intent.createChooser(share, "发送到"));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = this.getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private SensorMonitor createSensorMonitor(SensorsEnum type) {
        final String[] description;
        int sensorType;
        switch (type) {
            case Accelerometer:
                description = new String[3];
                description[0] = "x方向上加速度为(m/s2)：";
                description[1] = "y方向上加速度为(m/s2)：";
                description[2] = "z方向上加速度为(m/s2)：";
                sensorType = Sensor.TYPE_ACCELEROMETER;
                break;
            case Gravity:
                description = new String[3];
                description[0] = "x方向上重力加速度为(m/s2)：";
                description[1] = "y方向上重力加速度为(m/s2)：";
                description[2] = "z方向上重力加速度为(m/s2)：";
                sensorType = Sensor.TYPE_GRAVITY;
                break;
            case Gyroscope:
                description = new String[3];
                description[0] = "x方向的角速度为(rad/s)：";
                description[1] = "y方向的角速度为(rad/s)：";
                description[2] = "z方向的角速度为(rad/s)：";
                sensorType = Sensor.TYPE_GYROSCOPE;
                break;
            case Light:
                description = new String[1];
                description[0] = "光的强度为(lux)：";
                sensorType = Sensor.TYPE_LIGHT;
                break;
            case LinearA:
                description = new String[3];
                description[0] = "x方向上线性加速度为(m/s2)：";
                description[1] = "y方向上线性加速度为(m/s2)：";
                description[2] = "z方向上线性加速度为(m/s2)：";
                sensorType = Sensor.TYPE_LINEAR_ACCELERATION;
                break;
            case Pressure:
                description = new String[1];
                description[0] = "当前的压强为(hPa)：";
                sensorType = Sensor.TYPE_PRESSURE;
                break;
            case Proximity:
                description = new String[1];
                description[0] = "对象与手机的距离:";
                sensorType = Sensor.TYPE_PROXIMITY;
                break;
            case Rotation:
                description = new String[3];
                description[0] = "x方向的旋转矢量为：";
                description[1] = "y方向的旋转矢量为：";
                description[2] = "z方向的旋转矢量为：";
                sensorType = Sensor.TYPE_ROTATION_VECTOR;
                break;
            case Temperature:
                description = new String[1];
                description[0] = "当前的温度为(°C)：";
                sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE;
                break;
            case Magnetic:
                description = new String[3];
                description[0] = "终端x方向的磁场分量为(uT)：";
                description[1] = "终端y方向的磁场分量为(uT)：";
                description[2] = "终端z方向的磁场分量为(uT)：";
                sensorType = Sensor.TYPE_MAGNETIC_FIELD;
                break;
            case FixedMagnetic:
                description = new String[3];
                description[0] = "大地x方向的磁场分量为(uT)：";
                description[1] = "大地y方向的磁场分量为(uT)：";
                description[2] = "大地z方向的磁场分量为(uT)：";
                sensorType = SensorMonitor.FIXEDMAGNETIC;
                break;
            case Orient:
                description = new String[3];
                description[0] = "手机的方位角azimuth为：";
                description[1] = "手机的倾斜角pitch为：";
                description[2] = "手机的旋转角roll为：";
                sensorType = SensorMonitor.NEWORIENTATION;
                break;
            default:
                return null;
        }
        if (sensorType == SensorMonitor.FIXEDMAGNETIC) {
            return new MagneticMonitor(sensorType, new SensorMonitor.SensorChangedListener() {
                @Override
                public void onSensorChanged(float[] values) {
                    mMonitorView.setText(getDescription(description, values));
                }
            });
        } else if (sensorType == SensorMonitor.NEWORIENTATION) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.topMargin = 80;
            params.gravity = Gravity.CENTER;
            final ImageView compassView = new ImageView(this);
            compassView.setImageResource(R.drawable.compass);
            mMonitorLayout.addView(compassView, params);
            return new OrientMonitor(sensorType, new SensorMonitor.SensorChangedListener() {
                @Override
                public void onSensorChanged(float[] values) {
                    mMonitorView.setText(getDescription(description, values));
                    compassView.setRotation(- values[0]);
                }
            });
        } else {
            return new SensorMonitor(sensorType, new SensorMonitor.SensorChangedListener() {
                @Override
                public void onSensorChanged(float[] values) {
                    mMonitorView.setText(getDescription(description, values));
                }
            });
        }
    }

    private CharSequence getDescription(String[] descs, float[] values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < descs.length; ++i) {
            sb.append(descs[i]).append(values[i]).append('\n');
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

}
