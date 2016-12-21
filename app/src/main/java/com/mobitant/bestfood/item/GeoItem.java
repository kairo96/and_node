package com.mobitant.bestfood.item;

import com.google.android.gms.maps.model.LatLng;

/**
 * 위치 정보를 저장하는 객체
 */
public class GeoItem {
    public static double knownLatitude;
    public static double knownLongitude;

    /**
     * 사용자의 위도, 경도 객체를 반환한다. 만약 사용자의 위치를 알 수 없다면 서울 위치를 반환한다.
     * @return LatLng 위도,경도 객체
     */
    public static LatLng getKnownLocation() {
        if (knownLatitude == 0 || knownLongitude == 0) {
            return new LatLng(37.566229, 126.977689);
        } else {
            return new LatLng(knownLatitude, knownLongitude);
        }
    }
}