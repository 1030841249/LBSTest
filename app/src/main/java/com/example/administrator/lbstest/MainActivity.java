package com.example.administrator.lbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private TextView positionTextView;
    public  LocationClient mLocationClient;
    private BaiduMap baiduMap;
    private boolean isFirstLocate=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听
        mLocationClient.registerLocationListener(new MyLocationListener());
        //
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.d("wodecuowu", "onCreate: "+mLocationClient);
        positionTextView =(TextView)findViewById(R.id.position_textView);
        mapView=(MapView)findViewById(R.id.bdmapView);
        //
        baiduMap=mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        Log.d("wodecuowu", "onCreate: ");

        List<String> permissionList=new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(!permissionList.isEmpty()){
            String[] permission =permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permission,1);
        }else{
            requset();
        }

    }




    public class MyLocationListener implements BDLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

                Log.d("TAG", "onReceiveLocation: ");
                StringBuilder currentpostion = new StringBuilder();
                currentpostion.append(bdLocation.getLatitude() + bdLocation.getLongitude());
                currentpostion.append(bdLocation.getAddrStr());
                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation) {
                    currentpostion.append("<\n>网络定位");
                    navigateTo(bdLocation);
                }
                if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
                    currentpostion.append("<\nGps定位");
                    navigateTo(bdLocation);

                }
                positionTextView.setText(currentpostion);


        }
    }



    public void requset(){
        initLocation();
        mLocationClient.start();

    }

    private void navigateTo(BDLocation location){
        if(isFirstLocate){
            MapStatusUpdate update = MapStatusUpdateFactory
                    .newLatLng(new LatLng(location.getLatitude(),location.getLongitude()));
            baiduMap.animateMapStatus(update);
            update=MapStatusUpdateFactory
                    .zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate=false;
        }
        MyLocationData.Builder builder = new MyLocationData.Builder();
        builder.latitude(location.getLatitude());
        builder.longitude(location.getLongitude());
        MyLocationData date =builder.build();
        baiduMap.setMyLocationData(date);
    }

    public void initLocation(){
        LocationClientOption option =new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        //option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        mLocationClient.setLocOption(option);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result : grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意全部权限才可运行本程序",Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }

                }requset();
                break;
            default:break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mLocationClient.stop();
        baiduMap.setMyLocationEnabled(false);
    }
}
