package com.saiyajin.sensor.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.saiyajin.sensor.R;
import com.saiyajin.sensor.listener.SensorsEnum;
import com.saiyajin.sensor.utils.DBHelper;
import com.saiyajin.sensor.view.BottomTabView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnPageChangeListener, OnClickListener {

    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private FragmentPagerAdapter mAdapter;
    private List<BottomTabView> mTabIndicator = new ArrayList<>();
    private DBHelper sqlHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sqlHelper = new DBHelper(this);
        mViewPager = (ViewPager) findViewById(R.id.pager);

        initDatas();

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initDatas() {
        mTabs.add(new IntroFragment());
        mTabs.add(new InfoFragment());
        mTabs.add(new SensorFragment());

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                return mTabs.get(arg0);
            }
        };

        initTabIndicator();

    }

    private void initTabIndicator() {
        BottomTabView one = (BottomTabView) findViewById(R.id.id_indicator_one);
        BottomTabView two = (BottomTabView) findViewById(R.id.id_indicator_two);
        BottomTabView three = (BottomTabView) findViewById(R.id.id_indicator_three);

        mTabIndicator.add(one);
        mTabIndicator.add(two);
        mTabIndicator.add(three);

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);

        one.setIconAlpha(1.0f);
    }

    @Override
    public void onPageSelected(int arg0) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
            int positionOffsetPixels) {

        if (positionOffset > 0) {
            BottomTabView left = mTabIndicator.get(position);
            BottomTabView right = mTabIndicator.get(position + 1);

            left.setIconAlpha(1 - positionOffset);
            right.setIconAlpha(positionOffset);
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onClick(View v) {

        resetOtherTabs();

        switch (v.getId()) {
            case R.id.id_indicator_one:
                mTabIndicator.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0, false);
                break;
            case R.id.id_indicator_two:
                mTabIndicator.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1, false);
                break;
            case R.id.id_indicator_three:
                mTabIndicator.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2, false);
                break;
        }

    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicator.size(); i++) {
            mTabIndicator.get(i).setIconAlpha(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent1 = new Intent();
        intent1.setClass(MainActivity.this, SelfIntroActivity.class);
        Intent intentp = new Intent("android.intent.action.CALL",
                Uri.parse("tel:" + "18601242331"));
        switch (item.getItemId()) {
            case R.id.selfintro:
                intent1.setClass(MainActivity.this, SelfIntroActivity.class);
                MainActivity.this.startActivity(intent1);
                break;
            case R.id.phoneto:
                MainActivity.this.startActivity(intentp);
                break;
            case R.id.cleardb:
                sqlHelper.clearall();
                Toast.makeText(MainActivity.this, "数据库已清空！", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
        return true;
    }


    public static class IntroFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.intro, container, false);
        }
    }

    public static class InfoFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.info, container, false);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            TextView sl = (TextView) getActivity()
                    .findViewById(R.id.sensorlist);
            SensorManager sm = (SensorManager) getActivity()
                    .getSystemService(Context.SENSOR_SERVICE);
            String str;
            List<Sensor> allSensors = sm.getSensorList(Sensor.TYPE_ALL);
            int i;
            sl.setText(String.format("您的手机有%d个传感器\n", allSensors.size()));
            Sensor s;
            for (i = 0; i < allSensors.size(); i++) {
                s = allSensors.get(i);
                str = "设备名称：" + s.getName() + "\n供应商名称：" + s.getVendor()
                        + "\n最小数据间隔（微秒）：" + s.getMinDelay() + "\n分辨率："
                        + s.getResolution() + "\n";
                switch (s.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        sl.append("\n加速度传感器\n" + str);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        sl.append("\n陀螺仪传感器\n" + str);
                        break;
                    case Sensor.TYPE_LIGHT:
                        sl.append("\n环境光线传感器\n" + str);
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        sl.append("\n电磁场传感器\n" + str);
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        sl.append("\n方向传感器\n" + str);
                        break;
                    case Sensor.TYPE_PRESSURE:
                        sl.append("\n压力传感器\n" + str);
                        break;
                    case Sensor.TYPE_PROXIMITY:
                        sl.append("\n距离传感器\n" + str);
                        break;
                    case Sensor.TYPE_AMBIENT_TEMPERATURE:
                        sl.append("\n环境温度传感器\n" + str);
                        break;
                    case Sensor.TYPE_TEMPERATURE:
                        sl.append("\n温度传感器\n" + str);
                        break;
                    case Sensor.TYPE_GRAVITY:
                        sl.append("\n重力传感器\n" + str);
                        break;
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        sl.append("\n线性加速度传感器\n" + str);
                        break;
                    case Sensor.TYPE_ROTATION_VECTOR:
                        sl.append("\n旋转矢量传感器\n" + str);
                        break;
                    default:
                        sl.append("\n未知传感器\n" + str);
                        break;
                }

            }
        }
    }

    public static class SensorFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.sensor, container, false);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            ListView sensorlist = (ListView) getActivity().findViewById(R.id.sensorlistview);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.sensors, android.R.layout.simple_list_item_1);
            sensorlist.setAdapter(adapter);
            sensorlist.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View arg1,
                        int pos, long id) {
                    int position = (int) parent.getItemIdAtPosition(pos);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), MonitorActivity.class);
                    switch (position) {
                        case 0:
                            intent.putExtra("SensorType", SensorsEnum.Accelerometer);
                            break;
                        case 1:
                            intent.putExtra("SensorType", SensorsEnum.Orient);
                            break;
                        case 2:
                            intent.putExtra("SensorType", SensorsEnum.Magnetic);
                            break;
                        case 3:
                            intent.putExtra("SensorType", SensorsEnum.Temperature);
                            break;
                        case 4:
                            intent.putExtra("SensorType", SensorsEnum.Light);
                            break;
                        case 5:
                            intent.putExtra("SensorType", SensorsEnum.Gyroscope);
                            break;
                        case 6:
                            intent.putExtra("SensorType", SensorsEnum.Pressure);
                            break;
                        case 7:
                            intent.putExtra("SensorType", SensorsEnum.Proximity);
                            break;
                        case 8:
                            intent.setClass(getActivity(), WifiActivity.class);
                            break;
                        case 9:
                            intent.putExtra("SensorType", SensorsEnum.Gravity);
                            break;
                        case 10:
                            intent.putExtra("SensorType", SensorsEnum.LinearA);
                            break;
                        case 11:
                            intent.putExtra("SensorType", SensorsEnum.Rotation);
                            break;
                        case 12:
                            intent.setClass(getActivity(), MultiScanActivity.class);
                        default:
                            break;
                    }
                    getActivity().startActivity(intent);
                }
            });
            super.onActivityCreated(savedInstanceState);
        }

    }
}
