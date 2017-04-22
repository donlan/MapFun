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


import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.search.poi.PoiIndoorResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于显示indoorpoi的overly
 */
public class IndoorPoiOverlay extends OverlayManager {

    private static final int MAX_POI_SIZE = 10;

    private PoiIndoorResult mIndoorPoiResult = null;

    /**
     * 构造函数
     *
     * @param baiduMap
     *            该 IndoorPoiOverlay 引用的 BaiduMap 对象
     */
    public IndoorPoiOverlay(BaiduMap baiduMap) {
        super(baiduMap);
    }

    /**
     * 设置IndoorPoi数据
     *
     * @param indoorpoiResult
     *            设置indoorpoiResult数据
     */
    public void setData(PoiIndoorResult indoorpoiResult) {
        this.mIndoorPoiResult = indoorpoiResult;
    }

    @Override
    public final List<OverlayOptions> getOverlayOptions() {
        if (mIndoorPoiResult == null || mIndoorPoiResult.getmArrayPoiInfo() == null) {
            return null;
        }
        List<OverlayOptions> markerList = new ArrayList<OverlayOptions>();
        int markerSize = 0;
        for (int i = 0; i < mIndoorPoiResult.getmArrayPoiInfo().size()
                && markerSize < MAX_POI_SIZE; i++) {
            if (mIndoorPoiResult.getmArrayPoiInfo().get(i).latLng == null) {
                continue;
            }
            markerSize++;
            Bundle bundle = new Bundle();
            bundle.putInt("index", i);
            markerList.add(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromAssetWithDpi("Icon_mark"
                            + markerSize + ".png")).extraInfo(bundle)
                    .position(mIndoorPoiResult.getmArrayPoiInfo().get(i).latLng));

        }
        return markerList;
    }

    /**
     * 获取该 IndoorPoiOverlay 的 indoorpoi数据
     *
     * @return
     */
    public PoiIndoorResult getIndoorPoiResult() {
        return mIndoorPoiResult;
    }

    /**
     * 覆写此方法以改变默认点击行为
     *
     * @param i
     *            被点击的poi在
     *            {@link PoiIndoorResult#getmArrayPoiInfo()} } 中的索引
     * @return
     */
    public boolean onPoiClick(int i) {
        return false;
    }

    @Override
    public final boolean onMarkerClick(Marker marker) {
        if (!mOverlayList.contains(marker)) {
            return false;
        }
        if (marker.getExtraInfo() != null) {
            return onPoiClick(marker.getExtraInfo().getInt("index"));
        }
        return false;
    }

    @Override
    public boolean onPolylineClick(Polyline polyline) {
        // TODO Auto-generated method stub
        return false;
    }
}

