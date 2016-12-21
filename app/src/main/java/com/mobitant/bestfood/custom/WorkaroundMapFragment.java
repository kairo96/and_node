package com.mobitant.bestfood.custom;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.SupportMapFragment;

/**
 * 스크롤 내부의 구글맵의 스크롤을 정상적으로 동작시키기 위한 커스텀 클래스
 */
public class WorkaroundMapFragment extends SupportMapFragment {
    private OnTouchListener mListener;

    /**
     * 뷰를 생성해서 반환한다.
     * @param layoutInflater 레이아웃 인플레이터 객체
     * @param viewGroup 뷰그룹
     * @param savedInstance 번들 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);

        TouchableWrapper frameLayout = new TouchableWrapper(getActivity());

        int bgColor = ContextCompat.getColor(getActivity(), android.R.color.transparent);

        frameLayout.setBackgroundColor(bgColor);

        ((ViewGroup) layout).addView(frameLayout,
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        return layout;
    }

    /**
     * 터치 리스너를 설정한다.
     * @param listener
     */
    public void setListener(OnTouchListener listener) {
        mListener = listener;
    }

    /**
     * 터치 리스너 인터페이스
     */
    public interface OnTouchListener {
        void onTouch();
    }

    /**
     * 터치 가능한 영역을 처리하기 위한 래퍼 클래스
     */
    public class TouchableWrapper extends FrameLayout {

        public TouchableWrapper(Context context) {
            super(context);
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mListener.onTouch();
                    break;
                case MotionEvent.ACTION_UP:
                    mListener.onTouch();
                    break;
            }
            return super.dispatchTouchEvent(event);
        }
    }
}
