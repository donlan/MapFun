package dong.lan.map.utils;

import com.baidu.location.BDLocation;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

/**
 * Created by 梁桂栋 on 17-2-15 ： 下午8:51.
 * Email:       760625325@qq.com
 * GitHub:      github.com/donlan
 * description: SmartTrip
 */

public final class MapHelper {
    private MapHelper() {
    }


    public static String locationDesc(BDLocation location){
        StringBuffer sb = new StringBuffer(256);
        sb.append("time : ");
        /**
         * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
         * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
         */
        sb.append(location.getTime());
        sb.append("\nlocType : ");// 定位类型
        sb.append(location.getLocType());
        sb.append("\nlocType description : ");// *****对应的定位类型说明*****
        sb.append(location.getLocTypeDescription());
        sb.append("\nlatitude : ");// 纬度
        sb.append(location.getLatitude());
        sb.append("\nlontitude : ");// 经度
        sb.append(location.getLongitude());
        sb.append("\nradius : ");// 半径
        sb.append(location.getRadius());
        sb.append("\nCountryCode : ");// 国家码
        sb.append(location.getCountryCode());
        sb.append("\nCountry : ");// 国家名称
        sb.append(location.getCountry());
        sb.append("\ncitycode : ");// 城市编码
        sb.append(location.getCityCode());
        sb.append("\ncity : ");// 城市
        sb.append(location.getCity());
        sb.append("\nDistrict : ");// 区
        sb.append(location.getDistrict());
        sb.append("\nStreet : ");// 街道
        sb.append(location.getStreet());
        sb.append("\naddr : ");// 地址信息
        sb.append(location.getAddrStr());
        sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
        sb.append(location.getUserIndoorState());
        sb.append("\nDirection(not all devices have value): ");
        sb.append(location.getDirection());// 方向
        sb.append("\nlocationdescribe: ");
        sb.append(location.getLocationDescribe());// 位置语义化信息
        sb.append("\nPoi: ");// POI信息
        if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
            for (int i = 0; i < location.getPoiList().size(); i++) {
                Poi poi = (Poi) location.getPoiList().get(i);
                sb.append(poi.getName() + ";");
            }
        }
        if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
            sb.append("\nspeed : ");
            sb.append(location.getSpeed());// 速度 单位：km/h
            sb.append("\nsatellite : ");
            sb.append(location.getSatelliteNumber());// 卫星数目
            sb.append("\nheight : ");
            sb.append(location.getAltitude());// 海拔高度 单位：米
            sb.append("\ngps status : ");
            sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
            sb.append("\ndescribe : ");
            sb.append("gps定位成功");
        } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
            // 运营商信息
            if (location.hasAltitude()) {// *****如果有海拔高度*****
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
            }
            sb.append("\noperationers : ");// 运营商信息
            sb.append(location.getOperators());
            sb.append("\ndescribe : ");
            sb.append("网络定位成功");
        } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
            sb.append("\ndescribe : ");
            sb.append("离线定位成功，离线定位结果也是有效的");
        } else if (location.getLocType() == BDLocation.TypeServerError) {
            sb.append("\ndescribe : ");
            sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
        } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
            sb.append("\ndescribe : ");
            sb.append("网络不同导致定位失败，请检查网络是否通畅");
        } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
            sb.append("\ndescribe : ");
            sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
        }
        return sb.toString();
    }

    public static void setLocation(BDLocation location, BaiduMap baiduMap, boolean isFirst) {

        MyLocationData myLocationData = baiduMap.getLocationData();
        if (myLocationData == null) {
            myLocationData = new MyLocationData.Builder()
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .speed(location.getSpeed()).build();
        }
        baiduMap.setMyLocationData(myLocationData);
        if (isFirst) {
            LatLng ll = new LatLng(location.getLatitude(),
                    location.getLongitude());
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    public static void setLocation(LatLng location, BaiduMap baiduMap, boolean isFirst) {

        MyLocationData myLocationData = baiduMap.getLocationData();
        if (myLocationData == null) {
            myLocationData = new MyLocationData.Builder()
                    .latitude(location.latitude)
                    .longitude(location.longitude)
                    .build();
        }
        baiduMap.setMyLocationData(myLocationData);
        if (isFirst) {
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(location).zoom(18.0f);
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }



    public static Marker drawMarker(BaiduMap baiduMap, LatLng latLng, BitmapDescriptor bitmapDescriptor) {
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .perspective(true)
                .zIndex(3)
                .icon(bitmapDescriptor);
        return (Marker) baiduMap.addOverlay(option);
    }

    public static Marker drawMarker(BaiduMap baiduMap, LatLng latLng, BitmapDescriptor bitmapDescriptor,float anchorX,float anchorY) {
        OverlayOptions option = new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .perspective(true)
                .anchor(anchorX,anchorY)
                .zIndex(3)
                .icon(bitmapDescriptor);
        return (Marker) baiduMap.addOverlay(option);
    }

    public static void drawPolyLine(BaiduMap baiduMap, List<LatLng> points) {
        if (points == null || points.size() <= 1)
            return;
        PolylineOptions polylineOptions = new PolylineOptions().width(10).
                points(points).color(0xffE8484B);
        baiduMap.addOverlay(polylineOptions);
    }
}
