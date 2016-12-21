package com.mobitant.bestfood;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobitant.bestfood.item.FoodInfoItem;
import com.mobitant.bestfood.lib.GeoLib;
import com.mobitant.bestfood.lib.GoLib;
import com.mobitant.bestfood.lib.MyLog;
import com.mobitant.bestfood.lib.StringLib;

import org.parceler.Parcels;

/**
 * 맛집 위치를 선택하고 저장하는 액티비티
 */
public class BestFoodRegisterLocationFragment extends Fragment
        implements View.OnClickListener, OnMapReadyCallback,
                    GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
                    GoogleMap.OnMarkerDragListener {

    private static final int MAP_ZOOM_LEVEL_DEFAULT = 16;
    private static final int MAP_ZOOM_LEVEL_DETAIL = 18;
    public static final String INFO_ITEM = "INFO_ITEM";

    private final String TAG = this.getClass().getSimpleName();

    Context context;
    FoodInfoItem infoItem;
    GoogleMap map;

    TextView addressText;

    /**
     * FoodInfoItem 객체를 인자로 저장하는
     * BestFoodRegisterLocationFragment 인스턴스를 생성해서 반환한다.
     * @param infoItem 맛집 정보를 저장하는 객체
     * @return BestFoodRegisterLocationFragment 인스턴스
     */
    public static BestFoodRegisterLocationFragment newInstance(FoodInfoItem infoItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INFO_ITEM, Parcels.wrap(infoItem));

        BestFoodRegisterLocationFragment fragment = new BestFoodRegisterLocationFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    /**
     * 프래그먼트가 생성될 때 호출되며 인자에 저장된 FoodInfoItem를
     * BestFoodRegisterActivity에 currentItem를 저장한다.
     * @param savedInstanceState 프래그먼트가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            infoItem = Parcels.unwrap(getArguments().getParcelable(INFO_ITEM));
            if (infoItem.seq != 0) {
                BestFoodRegisterActivity.currentItem = infoItem;
            }
            MyLog.d(TAG, "infoItem " + infoItem);
        }
    }

    /**
     * fragment_bestfood_register_location.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View layout =
                inflater.inflate(R.layout.fragment_bestfood_register_location, container, false);

        return layout;
    }

    /**
     * onCreateView() 메소드 뒤에 호출되며 구글맵을 화면에 보여준다.
     * 그리고 화면 구성을 위한 작업을 한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.content_main, fragment).commit();
        }
        fragment.getMapAsync(this);

        addressText = (TextView) view.findViewById(R.id.bestfood_address);

        Button nextButton = (Button) view.findViewById(R.id.next);
        nextButton.setOnClickListener(this);
    }

    /**
     * 구글맵에서 마커가 클릭되었을 때 호출된다.
     * @param marker 클릭한 마커에 대한 정보를 가진 객체
     * @return 마커 이벤트를 처리했다면 true, 그렇지 않다면 false
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        movePosition(marker.getPosition(), MAP_ZOOM_LEVEL_DETAIL);

        return false;
    }

    /**
     * 구글맵이 준비되었을 때 호출되며 구글맵을 설정하고 기본 마커를 추가하는 작업을 한다.
     * @param map 구글맵 객체
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        String fineLocationPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;

        if (ActivityCompat.checkSelfPermission(context, fineLocationPermission)
                != PackageManager.PERMISSION_GRANTED) return;

        map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        map.setOnMarkerDragListener(this);
        map.setOnMapClickListener(this);

        UiSettings setting = map.getUiSettings();
        setting.setMyLocationButtonEnabled(true);
        setting.setCompassEnabled(true);
        setting.setZoomControlsEnabled(true);

        LatLng firstLatLng = new LatLng(infoItem.latitude, infoItem.longitude);
        if (infoItem.latitude != 0) {
            addMarker(firstLatLng, MAP_ZOOM_LEVEL_DEFAULT);
        }

        setAddressText(firstLatLng);
    }

    /**
     * 구글맵을 초기화하고
     * 인자로 넘어온 위도,경도, 줌레벨을 기반으로 마커를 생성해서 구글맵에 추가한다.
     * @param latLng 위도, 경도 객체
     * @param zoomLevel 줌레벨
     */
    private void addMarker(LatLng latLng, float zoomLevel) {
        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title("현재위치");
        marker.draggable(true);

        map.clear();
        map.addMarker(marker);

        movePosition(latLng, zoomLevel);
    }

    /**
     * 구글맵의 카메라를 위도와 경도 그리고 줌레벨을 기반으로 이동한다.
     * @param latlng 위도, 경도 객체
     * @param zoomLevel 줌레벨
     */
    private void movePosition(LatLng latlng, float zoomLevel) {
        CameraPosition cp = new CameraPosition.Builder().target((latlng)).zoom(zoomLevel).build();
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cp));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    /**
     * 사용자가 마커의 이동을 끝냈을 때 호출되며 최종 마커 위치를 저장한다.
     * @param marker 마커 객체
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        setCurrentLatLng(marker.getPosition());

        MyLog.d(TAG, "onMarkerDragEnd infoItem " + infoItem);
    }

    /**
     * 지정된 latLng의 위도와 경도를 infoItem에 저장한다.
     * @param latLng 위도, 경도 객체
     */
    private void setCurrentLatLng(LatLng latLng) {
        infoItem.latitude = latLng.latitude;
        infoItem.longitude = latLng.longitude;

        setAddressText(latLng);
    }

    /**
     * 클릭이벤트를 처리하며 맛집 정보를 담당하는 프래그먼트로 이동한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        GoLib.getInstance().goFragment(getFragmentManager(),
                R.id.content_main, BestFoodRegisterInputFragment.newInstance(infoItem));
    }

    /**
     * 사용자가 맵을 클릭했을 때 호출되며 현재 위도경도를 저장하고 마커를 추가한다.
     * @param latLng 위도, 경도 객체
     */
    @Override
    public void onMapClick(LatLng latLng) {
        MyLog.d(TAG, "onMapClick " + latLng);
        setCurrentLatLng(latLng);

        addMarker(latLng, map.getCameraPosition().zoom);

    }

    /**
     * 위도 경도를 기반으로 주소를 addressText 뷰에 출력한다.
     * @param latLng 위도, 경도 객체
     */
    private void setAddressText(LatLng latLng) {
        MyLog.d(TAG, "setAddressText " + latLng);
        Address address = GeoLib.getInstance().getAddressString(context, latLng);

        String addressStr = GeoLib.getInstance().getAddressString(address);

        if (!StringLib.getInstance().isBlank(addressStr)) {
            addressText.setText(addressStr);
        }
    }
}
