package dong.lan.map.service;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;

import java.util.HashMap;
import java.util.Map;


public class LocationService {
    private static final String TAG = LocationService.class.getSimpleName();
    private LocationClient client = null;
    private LocationClientOption mOption, DIYoption;
    private final byte[] lock = new byte[1];
    private BDLocation lastLocation;
    private Map<Object, LocationCallback> locationCallbackMap = new HashMap<>();

    private LocationService(){

    }

    private static LocationService service;

    public static LocationService service(){
        if(service == null)
            service = new LocationService();
        return service;
    }



    /***
     * 初始化百度定位服务
     *
     * @param locationContext 初始化百度定位客户端的上下文，建议使用applicationContext
     */
    public void init(Context locationContext) {
        synchronized (lock) {
            if (client == null) {
                SDKInitializer.initialize(locationContext);
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
                BDLocationListener locationListener = new BDLocationListener() {
                    @Override
                    public void onReceiveLocation(BDLocation bdLocation) {
                        if (bdLocation != null) {
                            String desc = "";
                            if (bdLocation.getLocType() == BDLocation.TypeServerError) {
                                desc = ("服务端网络定位失败，请尝试打开GPS定位");
                            } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {
                                desc = ("网络不同导致定位失败，请检查网络是否通畅");
                            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {
                                desc = ("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                            }
                            lastLocation = null;
                            lastLocation = bdLocation;
                            for (Object o : locationCallbackMap.keySet()) {
                                locationCallbackMap.get(o).onLocation(bdLocation,desc);
                            }
                        }
                    }

                    @Override
                    public void onConnectHotSpotMessage(String s, int i) {

                    }


                };
                client.registerLocationListener(locationListener);
            }
        }
    }


    public void registerCallback(Object key, LocationCallback callback) {
        Log.d(TAG, "location registerCallback:" + key.getClass().getSimpleName());
        locationCallbackMap.put(key, callback);
        if (!locationCallbackMap.isEmpty())
            start();
    }

    public void unregisterCallback(Object key) {
        Log.d(TAG, "location unregisterCallback:" + key.getClass().getSimpleName());
        locationCallbackMap.remove(key);
        if (locationCallbackMap.isEmpty())
            stop();
    }


    /***
     * 设置百度定位配置
     *
     * @param option 定位配置
     * @return isSuccessSetOption
     */
    public boolean setLocationOption(LocationClientOption option) {
        boolean isSuccess = false;
        if (option != null) {
            if (client.isStarted())
                client.stop();
            DIYoption = option;
            client.setLocOption(option);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void restart() {
        synchronized (lock) {
            if (client != null) {
                if (client.isStarted())
                    client.stop();
                client.start();
            }
        }

    }

    public LocationClientOption getOption() {
        return DIYoption;
    }

    /***
     * 获取默认的定位配置
     *
     * @return DefaultLocationClientOption 默认的定位配置
     */
    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(3000);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setNeedDeviceDirect(true);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
            //location >=7.0
            mOption.setIsNeedAltitude(true);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        }
        return mOption;
    }

    public BDLocation getLastLocation() {
        return lastLocation;
    }

    /**
     * 开启定位服务
     */
    public void start() {
        synchronized (lock) {
            if (client != null && !client.isStarted()) {
                client.start();
                Log.d(TAG, "location server start");
            }
        }
    }

    /**
     * 关闭定位服务
     */
    public void stop() {
        synchronized (lock) {
            if (client != null && client.isStarted()) {
                client.stop();
                Log.d(TAG, "location server stop");
            }
        }
    }


    public boolean isRunning() {
        return client != null && client.isStarted();
    }

    public interface LocationCallback {
        void onLocation(BDLocation location,String error);
    }
}
