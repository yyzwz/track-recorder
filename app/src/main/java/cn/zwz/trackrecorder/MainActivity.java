package cn.zwz.trackrecorder;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends Activity {
    private TextView text;  //定义用于显示LocationProvider的TextView组件
    private TextView timeText;  //定义用于显示LocationProvider的TextView组件
    private Timer timer;
    private String nowLongitude;
    private String nowLatitude;
    private Double addLongitude = 0.011000000;
    private Double addLatitude = 0.003000000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);   //设置全屏显示
        text = (TextView) findViewById(R.id.location);  //获取显示Location信息的TextView组件
        timeText = (TextView) findViewById(R.id.timeText);
        //获取系统的LocationManager对象
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //添加权限检查
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //设置每一秒获取一次location信息
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,      //GPS定位提供者
                1000,       //更新数据时间为1秒
                1,      //位置间隔为1米
                //位置监听器
                new LocationListener() {  //GPS定位信息发生改变时触发，用于更新位置信息
                    @Override
                    public void onLocationChanged(Location location) {
                        //GPS信息发生改变时，更新位置
                        locationUpdates(location);
                    }
                    @Override
                    //位置状态发生改变时触发
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }
                    @Override
                    //定位提供者启动时触发
                    public void onProviderEnabled(String provider) {
                    }
                    @Override
                    //定位提供者关闭时触发
                    public void onProviderDisabled(String provider) {
                    }
                });
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        locationUpdates(location);    //将最新的定位信息传递给创建的locationUpdates()方法中

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection connection=null;
                        BufferedReader reader=null;
                        String urls= "https://artskyhome.com:8080/xboot/trackRecorder/set?longitude="
                                + nowLongitude + "&latitude=" + nowLatitude;
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
                    }
                }).start();
            }
        },0,1000);
    }
    public void locationUpdates(Location location) {  //获取指定的查询信息
        //如果location不为空时
        if (location != null) {
            StringBuilder stringBuilder = new StringBuilder();        //使用StringBuilder保存数据
            //获取经度、纬度、等属性值
            stringBuilder.append("您的行踪正在被记录！");
            stringBuilder.append("\n当前经度：");
            stringBuilder.append(location.getLongitude());
            nowLongitude = "" + (location.getLongitude()+ addLongitude);
            nowLatitude = "" + (location.getLatitude() + addLatitude);
            stringBuilder.append("\n当前纬度：");
            stringBuilder.append(location.getLatitude());
            stringBuilder.append("\n启动时间：");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");    //设置日期时间格式
            stringBuilder.append(dateFormat.format(new Date(location.getTime())));
            text.setText(stringBuilder);            //显示获取的信息
        } else {
            text.setText("没有获取到GPS信息");
        }
    }
}