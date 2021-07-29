package cn.zwz.admin;

import android.os.Bundle;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.Timer;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;     // 定义百度地图组件
    private BaiduMap mBaiduMap;   // 定义百度地图对象
    private LocationClient mLocationClient;  //定义LocationClient
    private BDLocationListener myListener = new MyLocationListener();  //定义位置监听
    private boolean isFirstLoc = true;  //定义第一次启动
    private MyLocationConfiguration.LocationMode mCurrentMode;  //定义当前定位模式
    private Timer timer;
    private String nowLongitude = "39.9002387755";
    private String nowLatitude = "116.3280692546";
    private String jsonStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);   //设置全屏显示
        SDKInitializer.initialize(getApplicationContext());   //初始化地图SDK
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.bmapView);  //获取地图组件
        mBaiduMap = mMapView.getMap();  //获取百度地图对象
        mLocationClient = new LocationClient(getApplicationContext());  //创建LocationClient类
        mLocationClient.registerLocationListener(myListener);   //注册监听函数
        initLocation();  //调用initLocation()方法，实现初始化定位


        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                HttpURLConnection connection=null;
                BufferedReader reader=null;
                String urls= "https://artskyhome.com:8080/xboot/trackRecorder/get";
                try {
                    URL url=new URL(urls);
                    connection=(HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setReadTimeout(5000);
                    connection.setConnectTimeout(5000);
                    InputStream in=connection.getInputStream();
                    reader = new BufferedReader( new InputStreamReader(in));
                    StringBuilder response =new StringBuilder();
                    String line;
                    while ((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    showResponse(response.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    if (reader!=null){
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (connection!=null){
                        connection.disconnect();
                    }
                }
                LatLng cenpt = new LatLng(Double.parseDouble(nowLatitude),Double.parseDouble(nowLongitude));
                MapStatus mapStatus = new MapStatus.Builder().target(cenpt).build(); // .zoom(18)
                MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
                mBaiduMap.setMapStatus(mapStatusUpdate);
            }
        },0,1000);
    }

    private void initLocation() {  //该方法实现初始化定位
        //创建LocationClientOption对象，用于设置定位方式
        LocationClientOption option = new LocationClientOption();
        option.setCoorType("bd09ll");  //设置坐标类型
        option.setScanSpan(1000);      //1秒定位一次
        option.setOpenGps(true);      //打开GPS
        mLocationClient.setLocOption(option);  //保存定位参数与信息
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;  //设置定位模式
        //设置自定义定位图标
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.drawable.point);
        //位置构造方式，将定位模式，定义图标添加其中
        MyLocationConfiguration config = new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker);
        mBaiduMap.setMyLocationConfigeration(config);  //地图显示定位图标
    }

    public class MyLocationListener implements BDLocationListener {  //设置定位监听器
        @Override
        public void onReceiveLocation(BDLocation location) {
            //销毁后不再处理新接收的位置
            if (location == null || mMapView == null)
                return;
            // 构造定位数据
            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100)
                    .latitude(Double.parseDouble(nowLatitude))
                    .longitude(Double.parseDouble(nowLongitude))
                    .build();
            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {  //如果是第一次定位,就定位到以自己为中心
                LatLng ll = new LatLng(Double.parseDouble(nowLatitude), Double.parseDouble(nowLongitude)); //获取用户当前经纬度
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);  //更新坐标位置
                mBaiduMap.animateMapStatus(u);                            //设置地图位置
                isFirstLoc = false;                                      //取消第一次定位
            }

        }
    }


    @Override
    protected void onStart() {  //地图定位与Activity生命周期绑定
        super.onStart();
        mBaiduMap.setMyLocationEnabled(true);
        mLocationClient.start();
    }


    @Override
    protected void onStop() {  //停止地图定位
        super.onStop();
        mBaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }


    @Override
    protected void onDestroy() {  //销毁地图
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    jsonStr = new String(response.getBytes(), "UTF-8");
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    nowLongitude = jsonObject.optString("longitude");
                    nowLatitude = jsonObject.optString("latitude");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
