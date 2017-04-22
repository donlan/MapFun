/*
 *   Copyright 2016, donlan(梁桂栋)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *   Email me: stonelavender@hotmail.com
 */

package com.baidu.mapapi.overlayutil;

import android.graphics.Color;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.busline.BusLineResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示一条公交详情结果的Overlay
 */
public class BusLineOverlay extends OverlayManager {

    private BusLineResult mBusLineResult = null;

    /**
     * 构造函数
     * 
     * @param baiduMap
     *            该BusLineOverlay所引用的 BaiduMap 对象
     */
    public BusLineOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 设置公交线数据
     * 
     * @param result
     *            公交线路结果数据
     */
    public void setData(BusLineResult result) {
        this.mBusLineResult = result;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {

        if (mBusLineResult == null || mBusLineResult.getStations() == null) {
            return null;
        }
        List<OverlayOptions> overlayOptionses = new ArrayList<OverlayOptions>();
        for (BusLineResult.BusStation station : mBusLineResult.getStations()) {
            overlayOptionses.add(new MarkerOptions()
                    .position(station.getLocation())
                            .zIndex(10)
                                    .anchor(0.5f, 0.5f)
                                            .icon(BitmapDescriptorFactory
                                                    .fromAssetWithDpi("Icon_bus_station.png")));
        }

        List<LatLng> points = new ArrayList<LatLng>();
        for (BusLineResult.BusStep step : mBusLineResult.getSteps()) {
            if (step.getWayPoints() != null) {
                points.addAll(step.getWayPoints());
            }
        }
        if (points.size() > 0) {
            overlayOptionses
                    .add(new PolylineOptions().width(10)
                            .color(Color.argb(178, 0, 78, 255)).zIndex(0)
                                    .points(points));
        }
        return overlayOptionses;
    }

    /**
     * 覆写此方法以改变默认点击行为
     * 
     * @param index
     *            被点击的站点在
     *            {@link BusLineResult#getStations()}
     *            中的索引
     * @return 是否处理了该点击事件
     */
    public boolean onBusStationClick(int index) {
        if (mBusLineResult.getStations() != null
                && mBusLineResult.getStations().get(index) != null) {
            Log.i("baidumapsdk", "BusLineOverlay onBusStationClick");
        }
        return false;
    }

    public final boolean onMarkerClick(Marker marker) {
        if (mOverlayList != null && mOverlayList.contains(marker)) {
            return onBusStationClick(mOverlayList.indexOf(marker));
        } else {
            return false;
        }
        
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}
