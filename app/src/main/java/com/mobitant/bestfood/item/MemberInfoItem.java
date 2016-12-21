package com.mobitant.bestfood.item;

import com.google.gson.annotations.SerializedName;

/**
 * 사용자 정보를 저장하는 객체
 */
public class MemberInfoItem {
    public int seq;
    public String phone;
    public String name;
    public String sextype;
    public String birthday;
    @SerializedName("member_icon_filename") public String memberIconFilename;
    @SerializedName("reg_date") public String regDate;

    @Override
    public String toString() {
        return "MemberInfoItem{" +
                "seq=" + seq +
                ", phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", sextype='" + sextype + '\'' +
                ", birthday='" + birthday + '\'' +
                ", memberIconFilename='" + memberIconFilename + '\'' +
                ", regDate='" + regDate + '\'' +
                '}';
    }
}
