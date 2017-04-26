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

package dong.lan.map.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.Collections;
import java.util.List;

import dong.lan.map.R;
import dong.lan.map.service.Config;
import dong.lan.map.service.LocationService;
import dong.lan.map.utils.InputUtils;
import dong.lan.map.utils.MapHelper;
import dong.lan.permission.CallBack;
import dong.lan.permission.Permission;

public class PickLocationActivity extends AppCompatActivity {

    private static final String TAG = PickLocationActivity.class.getSimpleName();

    void back() {
        finish();
    }

    @Override
    public void finish() {
        double lat = 0;
        double lng = 0;
        if (pickMarker != null) {
            lat = pickMarker.getPosition().latitude;
            lng = pickMarker.getPosition().longitude;
        } else {
            toast("没有选择位置信息");
        }
        Intent locData = new Intent();
        locData.putExtra(Config.LATITUDE, lat);
        locData.putExtra(Config.LONGITUDE, lng);
        locData.putExtra(Config.LOC_ADDRESS, locationText.getText().toString());
        setResult(Config.RESULT_LOCATION, locData);
        super.finish();
    }

    private void toast(String text) {
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    EditText searchInput;

    EditText cityInput;

    void search() {
        String searchText = searchInput.getText().toString();
        String city = cityInput.getText().toString();
        if (TextUtils.isEmpty(city)) {
            toast("请输入正确的城市名称");
            return;
        }
        if (TextUtils.isEmpty(searchText)) {
            toast("搜索内容不能为空");
        }
        InputUtils.hideInputKeyboard(searchInput);
        locationText.setVisibility(View.GONE);
        if (poiSearch == null) {
            poiSearch = PoiSearch.newInstance();
            poiSearch.setOnGetPoiSearchResultListener(poiSearchResultListener);
        }
        PoiCitySearchOption option = new PoiCitySearchOption();
        option.city(city);
        option.keyword(searchText);
        option.pageCapacity(10);
        option.pageNum(1);
        poiSearch.searchInCity(option);
    }
    MapView mapView;
    EditText locationText;
    private BaiduMap baiduMap;

    private BitmapDescriptor pickFlag;
    private Marker pickMarker;
    private GeoCoder geoSearch;
    private PoiSearch poiSearch;
    private LocationService.LocationCallback locationCallback = new LocationService.LocationCallback() {
        @Override
        public void onLocation(BDLocation bdLocation,String error) {
            baiduMap.setMyLocationEnabled(true);
            MyLocationConfiguration configuration = new MyLocationConfiguration(
                    MyLocationConfiguration.LocationMode.NORMAL, true,
                    BitmapDescriptorFactory.fromResource(R.drawable.location));
            baiduMap.setMyLocationConfigeration(configuration);
            MapHelper.setLocation(bdLocation, baiduMap, isFirstLocation);
            isFirstLocation = false;
           LocationService.service().unregisterCallback(PickLocationActivity.this);
            setCityText(bdLocation.getCity());
        }
    };

    private void setCityText(String cityText) {
//        cityInput.setText(cityText);
    }

    private OnGetGeoCoderResultListener geoCoderResultListener = new OnGetGeoCoderResultListener() {
        @Override
        public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
            Log.d(TAG, "onGetGeoCodeResult: " + geoCodeResult);
        }

        @Override
        public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
            if (reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                if (locationText.getVisibility() == View.GONE) {
                    locationText.setVisibility(View.VISIBLE);
                }
                String addr = "";
                if (!TextUtils.isEmpty(reverseGeoCodeResult.getAddress())) {
                    addr = reverseGeoCodeResult.getAddress();
                } else if (null != reverseGeoCodeResult.getAddressDetail()) {
                    ReverseGeoCodeResult.AddressComponent component = reverseGeoCodeResult.getAddressDetail();
                    addr = component.city + "-" + component.street;
                }
                if (reverseGeoCodeResult.getPoiList() == null && TextUtils.isEmpty(addr)) {
                    locationText.setText("");
                    toast("无法识别位置信息");
                } else {
                    for (PoiInfo p : reverseGeoCodeResult.getPoiList()) {
                        Log.d(TAG, "onGetReverseGeoCodeResult: " + p.address);
                    }
                    locationText.setText(addr);
                }
            } else {
                toast("没有搜索结果");
            }
            if (locationText.getText().length() <= 0)
                locationText.setHint("无识别结果,请手动输入");
        }
    };

    private OnGetPoiSearchResultListener poiSearchResultListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {

            if (poiResult == null || poiResult.getAllPoi() == null) {
                toast("无搜索结果");
                return;
            }
            for (int i = 0, s = poiResult.getAllPoi().size(); i < s; i++) {
                LatLng loc = poiResult.getAllPoi().get(i).location;
                MapHelper.drawMarker(baiduMap, loc,
                        BitmapDescriptorFactory.fromResource(R.drawable.location));
                if (i == 0) {
                    MapHelper.setLocation(loc, baiduMap, true);
                    locationText.setVisibility(View.VISIBLE);
                    locationText.setText(poiResult.getAllPoi().get(i).address);
                }
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
            Log.d(TAG, "onGetPoiDetailResult: " + poiDetailResult);
        }

        @Override
        public void onGetPoiIndoorResult(PoiIndoorResult poiIndoorResult) {
            Log.d(TAG, "onGetPoiIndoorResult: " + poiIndoorResult);
        }
    };

    private boolean isFirstLocation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        initView();
    }

    private void initView() {

        findViewById(R.id.pick_location_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        findViewById(R.id.pick_location_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
        locationText = (EditText) findViewById(R.id.pick_location_text);
        searchInput = (EditText) findViewById(R.id.pick_location_input);
        cityInput = (EditText) findViewById(R.id.pick_location_input_city);
        mapView = (MapView) findViewById(R.id.bdMap);
        mapView.setLogoPosition(LogoPosition.logoPostionRightTop);
        baiduMap = mapView.getMap();
        baiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (geoSearch == null) {
                    geoSearch = GeoCoder.newInstance();
                    geoSearch.setOnGetGeoCodeResultListener(geoCoderResultListener);
                    pickFlag = BitmapDescriptorFactory.fromResource(R.drawable.location_flag);
                    pickMarker = MapHelper.drawMarker(baiduMap, latLng, pickFlag);
                } else {
                    pickMarker.setPosition(latLng);
                }
                ReverseGeoCodeOption option = new ReverseGeoCodeOption();
                option.location(latLng);
                geoSearch.reverseGeoCode(option);
            }
        });


        Permission.instance().check(new CallBack<List<String>>() {
            @Override
            public void onResult(List<String> result) {
                if (result == null) {
                    LocationService.service().
                            registerCallback(PickLocationActivity.this, locationCallback);
                } else {
                    toast("缺少定位权限,无法获取位置信息");
                }
            }
        }, this, Collections.singletonList(Manifest.permission.ACCESS_FINE_LOCATION));

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
        mapView.onDestroy();
        super.onDestroy();
        if (geoSearch != null)
            geoSearch.destroy();
        if (pickFlag != null)
            pickFlag.recycle();
        pickFlag = null;
        pickMarker = null;
        LocationService.service().unregisterCallback(this);
    }
}
