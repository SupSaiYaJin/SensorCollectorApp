package com.saiyajin.sensor.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.saiyajin.sensor.R;
import com.saiyajin.sensor.listener.SensorMonitor;
import com.saiyajin.sensor.listener.SensorMonitorFactory;
import com.saiyajin.sensor.listener.SensorsEnum;
import com.saiyajin.sensor.utils.DBHelper;
import com.saiyajin.sensor.utils.Utils;

import java.io.File;

public class MonitorActivity extends Activity {
    private DBHelper dataBaseHelper;
    private String fileName;
    private SensorManager sManager;
    private SensorMonitor sMonitor;
    private int count = 0;
    private boolean saveData = false;
    private Handler handler;
    private Runnable runnable;
    private long interval = 2000;
    private TextView monitorSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.monitor);
        sManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        dataBaseHelper = new DBHelper(this);
        monitorSave = (TextView) findViewById(R.id.monitorsave);
        monitorSave.setText(String.format("已存储%d个数据！", count));
        SensorsEnum sensorType = (SensorsEnum) this.getIntent().getSerializableExtra("SensorType");
        fileName = sensorType.toString() + "Data.txt";
        sMonitor = SensorMonitorFactory.createSensorMonitor(sensorType, this);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(runnable, interval);
                ++count;
                Utils.saveFile(sMonitor.getSaveString(count), fileName);
                dataBaseHelper.insert(sMonitor.getSensorInt(), System.currentTimeMillis(), sMonitor.getValues());
                monitorSave.setText(String.format("已存储%d个数据！", count));
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sMonitor.getSensorInt() == SensorMonitor.NEWORIENTATION || sMonitor.getSensorInt() == SensorMonitor.FIXEDMAGNETIC) {
            sManager.registerListener(sMonitor, sManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
            sManager.registerListener(sMonitor, sManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI);
        } else {
            sManager.registerListener(sMonitor, sManager.getDefaultSensor(sMonitor.getSensorInt()), SensorManager.SENSOR_DELAY_UI);
        }
        if (saveData) {
            handler.post(runnable);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        sManager.unregisterListener(sMonitor);
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
                dataBaseHelper.insert(sMonitor.getSensorInt(), System.currentTimeMillis(), sMonitor.getValues());
                ++count;
                Utils.saveFile(sMonitor.getSaveString(count), fileName);
                Toast.makeText(MonitorActivity.this, "单次扫描成功", Toast.LENGTH_SHORT)
                        .show();
                monitorSave.setText(String.format("已存储%d个数据！", count));
                break;
            case R.id.ifsavedata:
                if (saveData) {
                    saveData = false;
                    handler.removeCallbacks(runnable);
                    Toast.makeText(MonitorActivity.this, "连续数据存储已关闭",
                            Toast.LENGTH_SHORT).show();
                } else {
                    saveData = true;
                    Toast.makeText(MonitorActivity.this, "连续数据存储已开启",
                            Toast.LENGTH_SHORT).show();
                    handler.post(runnable);
                }
                break;
            case R.id.counttozero:
                count = 0;
                Toast.makeText(MonitorActivity.this, "计数已清零", Toast.LENGTH_SHORT)
                        .show();
                monitorSave.setText(String.format("已存储%d个数据！", count));
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
                                                interval = 1000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每1s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 1:
                                                interval = 2000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每2s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 2:
                                                interval = 3000;
                                                Toast.makeText(MonitorActivity.this,
                                                        "每3s存储一次", Toast.LENGTH_SHORT)
                                                        .show();
                                                break;
                                            case 3:
                                                interval = 4000;
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
                File file = new File(Environment.getExternalStorageDirectory(), fileName);
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

}
