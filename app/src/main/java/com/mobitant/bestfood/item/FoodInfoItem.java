package com.mobitant.bestfood.item;

import com.google.gson.annotations.SerializedName;

/**
 * 맛집 정보를 저장하는 객체
 */
@org.parceler.Parcel
public class FoodInfoItem {
    public int seq;
    @SerializedName("member_seq") public int memberSeq;
    public String name;
    public String tel;
    public String address;
    public double latitude;
    public double longitude;
    public String description;
    @SerializedName("reg_date") public String regDate;
    @SerializedName("mod_date") public String modDate;
    @SerializedName("user_distance_meter") public double userDistanceMeter;
    @SerializedName("is_keep") public boolean isKeep;
    @SerializedName("image_filename") public String imageFilename;

    @Override
    public String toString() {
        return "FoodInfoItem{" +
                "seq=" + seq +
                ", memberSeq=" + memberSeq +
                ", name='" + name + '\'' +
                ", tel='" + tel + '\'' +
                ", address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", description='" + description + '\'' +
                ", regDate='" + regDate + '\'' +
                ", modDate='" + modDate + '\'' +
                ", userDistanceMeter=" + userDistanceMeter +
                ", isKeep=" + isKeep +
                ", imageFilename='" + imageFilename + '\'' +
                '}';
    }
}
