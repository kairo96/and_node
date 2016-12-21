package com.mobitant.bestfood;

import android.app.Application;
import android.os.StrictMode;

import com.mobitant.bestfood.item.FoodInfoItem;
import com.mobitant.bestfood.item.MemberInfoItem;

/**
 * 앱 전역에서 사용할 수 있는 클래스
 */
public class MyApp extends Application {
    private MemberInfoItem memberInfoItem;
    private FoodInfoItem foodInfoItem;

    @Override
    public void onCreate() {
        super.onCreate();

        // FileUriExposedException 문제를 해결하기 위한 코드
        // 관련 설명은 책의 [참고] 페이지 참고
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
    }

    public MemberInfoItem getMemberInfoItem() {
        if (memberInfoItem == null) memberInfoItem = new MemberInfoItem();

        return memberInfoItem;
    }

    public void setMemberInfoItem(MemberInfoItem item) {
        this.memberInfoItem = item;
    }

    public int getMemberSeq() {
        return memberInfoItem.seq;
    }

    public void setFoodInfoItem(FoodInfoItem foodInfoItem) {
        this.foodInfoItem = foodInfoItem;
    }

    public FoodInfoItem getFoodInfoItem() {
        return foodInfoItem;
    }
}
