package com.saiyajin.sensor.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.text.DateFormat;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.saiyajin.sensor.R;

public class WifiActivity extends Activity {
	private WifiManager WM;
	private TextView wifi1;
	private TextView isWriting;

	private Handler handler;
	private Runnable runnable;
	long interval = 1000;
	int count=0;
	boolean run = false;
	boolean isSaveData = false;

	public final static String FILE_NAME = "检测结果.csv"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		WM = (WifiManager) WifiActivity.this.getSystemService(WIFI_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wifi);

		wifi1 = (TextView) findViewById(R.id.wifi1);
        isWriting =(TextView) findViewById(R.id.iswrite);

		Button scan = (Button) findViewById(R.id.scan);
		scan.setOnClickListener(new buttonListener());
		Button openWifi = (Button) findViewById(R.id.openwifi);
		openWifi.setOnClickListener(new buttonListener());
		Button write = (Button) findViewById(R.id.write);
		write.setOnClickListener(new buttonListener());
		Button conScan = (Button) findViewById(R.id.conscan);
		conScan.setOnClickListener(new buttonListener());

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				handler.postDelayed(runnable, interval);
				run = true;
				if (WM.getWifiState() == WifiManager.WIFI_STATE_ENABLED)
					scan();
				else {
					handler.removeCallbacks(runnable);
					Toast.makeText(WifiActivity.this, "WiFi未打开，请打开WiFi以检测",
							Toast.LENGTH_SHORT).show();
					run = false;
					wifi1.setText(R.string.wifi_not_open);
				}
			}
		};

		if (WM.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
			Toast.makeText(WifiActivity.this, "WiFi已打开", Toast.LENGTH_SHORT).show();
			wifi1.setText(R.string.wifi_opened);
		} else {
			Toast.makeText(WifiActivity.this, "WiFi未打开，请打开WiFi以检测", Toast.LENGTH_SHORT)
					.show();
			wifi1.setText(R.string.wifi_not_open);
		}
		
		 if (!isSaveData)
			 isWriting.setText("数据存储未开启!");
		 else
		     isWriting.setText(String.format("数据存储已开启！已存储%d个数据！", count));
		 
	}

	private class buttonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.scan:
				if (WM.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					if (run) {
						handler.removeCallbacks(runnable);
						run = false;
					}
					scan();
					Toast.makeText(WifiActivity.this, "单次扫描成功", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(WifiActivity.this, "WiFi未打开，请打开WiFi以检测",
							Toast.LENGTH_SHORT).show();
					wifi1.setText(R.string.wifi_not_open);
				}
				break;

			case R.id.openwifi:
				if (WM.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
					WM.setWifiEnabled(true);
					Toast.makeText(WifiActivity.this, "WiFi正在打开", Toast.LENGTH_SHORT)
							.show();
				} else
					Toast.makeText(WifiActivity.this, "别点我了！WiFi已经打开了！",
							Toast.LENGTH_SHORT).show();
				break;

			case R.id.write:
				if (isSaveData) {
					isSaveData = false;
					Toast.makeText(WifiActivity.this, "已停止数据存储", Toast.LENGTH_SHORT)
							.show();
					isWriting.setText("数据存储未开启!");
				} else {
					isSaveData = true;
					Toast.makeText(WifiActivity.this, "数据存储已开启", Toast.LENGTH_SHORT)
							.show();
					isWriting.setText(String.format("数据存储已开启！已存储%d个数据！", count));
				}
				break;

			case R.id.conscan:
				if (WM.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
					AlertDialog.Builder intervalSelection = new AlertDialog.Builder(
							WifiActivity.this);
					intervalSelection.setTitle("选择扫描频率")
							.setItems(
									new CharSequence[] { "1s", "2s", "3s", "4s" },
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											switch (which) {
											case 0:
												interval = 1000;
												handler.post(runnable);
												Toast.makeText(WifiActivity.this,
														"每1s扫描一次，点单次扫描停止",
														Toast.LENGTH_SHORT)
														.show();
												break;
											case 1:
												interval = 2000;
												handler.post(runnable);
												Toast.makeText(WifiActivity.this,
														"每2s扫描一次，点单次扫描停止",
														Toast.LENGTH_SHORT)
														.show();
												break;
											case 2:
												interval = 3000;
												handler.post(runnable);
												Toast.makeText(WifiActivity.this,
														"每3s扫描一次，点单次扫描停止",
														Toast.LENGTH_SHORT)
														.show();
												break;
											case 3:
												interval = 4000;
												handler.post(runnable);
												Toast.makeText(WifiActivity.this,
														"每4s扫描一次，点单次扫描停止",
														Toast.LENGTH_SHORT)
														.show();
												break;
											default:
												break;
											}
										}
									}).show().setCanceledOnTouchOutside(false);
				} else {
					Toast.makeText(WifiActivity.this, "WiFi未打开，请打开WiFi以检测",
							Toast.LENGTH_SHORT).show();
					wifi1.setText(R.string.wifi_not_open);
				}
             break;
			default:
				break;
			}
		}
	}
	
	@Override
	protected void onPause() {
		handler.removeCallbacks(runnable);
		super.onPause();
	
	}
	public void saveFile(String str) {  
	    FileOutputStream fileOutputStream;
	    File file = new File(Environment.getExternalStorageDirectory(),
	        FILE_NAME);
	    try {
	        fileOutputStream = new FileOutputStream(file,true);
	        fileOutputStream.write(str.getBytes());
	        fileOutputStream.close();
	    } catch (IOException e) {
	        e.printStackTrace();  
	    }
	}
	
	public void scan() {
		Date now=new Date();
		DateFormat timeFormat = DateFormat.getDateTimeInstance();
		String time = timeFormat.format(now);
		
		String str;
		WM.startScan();
		List<ScanResult> scanResultList = WM.getScanResults();
		int i;
		wifi1.setText(String.format("您的手机目前能搜到%d个WiFi热点\n\n", scanResultList.size()));
		ScanResult r;
		if(isSaveData){
			saveFile(++count+","+time+"\n");
			isWriting.setText(String.format("数据存储已开启！已存储%d个数据！", count));}
		for (i = 0; i < scanResultList.size(); i++) {
			r = scanResultList.get(i);
			str = "SSID：" + r.SSID + "\nMAC地址：" + r.BSSID + "\n信号强度：" + r.level
					+ "dBm" + "\n";
			wifi1.append(str+"\n");
			if(isSaveData)
			saveFile(count+","+r.BSSID+","+r.level+"\n");
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = new MenuInflater(this);
		inflater.inflate(R.menu.wifimenu, menu);
		return true;
	}

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.clearcount:
		    count=0;
		    if(isSaveData)
				isWriting.setText(String.format("数据存储已开启！已存储%d个数据！", count));
		    break;
		case R.id.sendwifidata:
			File file = new File(Environment.getExternalStorageDirectory(),FILE_NAME);
			Intent share = new Intent(Intent.ACTION_SEND);   
			share.putExtra(Intent.EXTRA_STREAM, 
			Uri.fromFile(file));
			share.setType("*/*");
			startActivity(Intent.createChooser(share, "发送到"));
			break;			
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
	}

}