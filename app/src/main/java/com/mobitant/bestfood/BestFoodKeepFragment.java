package com.mobitant.bestfood;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.mobitant.bestfood.adapter.KeepListAdapter;
import com.mobitant.bestfood.item.FoodInfoItem;
import com.mobitant.bestfood.item.GeoItem;
import com.mobitant.bestfood.item.KeepItem;
import com.mobitant.bestfood.lib.MyLog;
import com.mobitant.bestfood.remote.RemoteService;
import com.mobitant.bestfood.remote.ServiceGenerator;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 맛집 즐겨찾기 리스트를 보여주는 프래그먼트
 */
public class BestFoodKeepFragment extends Fragment {
    private final String TAG = this.getClass().getSimpleName();

    Context context;
    int memberSeq;

    RecyclerView keepRecyclerView;
    TextView noDataText;

    KeepListAdapter keepListAdapter;

    ArrayList<KeepItem> keepList = new ArrayList<>();

    /**
     * BestFoodKeepFragment 인스턴스를 생성한다.
     * @return BestFoodListFragment 인스턴스
     */
    public static BestFoodKeepFragment newInstance() {
        BestFoodKeepFragment f = new BestFoodKeepFragment();
        return f;
    }

    /**
     * fragment_bestfood_keep.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();

        memberSeq = ((MyApp)getActivity().getApplication()).getMemberSeq();

        View layout = inflater.inflate(R.layout.fragment_bestfood_keep, container, false);

        return layout;
    }

    /**
     * 프래그먼트가 일시 중지 상태가 되었다가 다시 보여질 때 호출된다.
     * BestFoodInfoActivity가 실행된 후,
     * 즐겨찾기 상태가 변경되었을 경우 이를 반영하는 용도로 사용한다.
     */
    @Override
    public void onResume() {
        super.onResume();

        MyApp myApp = ((MyApp) getActivity().getApplication());
        FoodInfoItem currentInfoItem = myApp.getFoodInfoItem();

        if (keepListAdapter != null && currentInfoItem != null) {
            keepListAdapter.setItem(currentInfoItem);
            myApp.setFoodInfoItem(null);

            if (keepListAdapter.getItemCount() == 0) {
                noDataText.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * onCreateView() 메소드 뒤에 호출되며 화면 뷰들을 설정한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.nav_keep);

        keepRecyclerView = (RecyclerView) view.findViewById(R.id.keep_list);
        noDataText = (TextView) view.findViewById(R.id.no_keep);

        keepListAdapter = new KeepListAdapter(context,
                R.layout.row_bestfood_keep, keepList, memberSeq);
        StaggeredGridLayoutManager layoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(
                StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        keepRecyclerView.setLayoutManager(layoutManager);
        keepRecyclerView.setAdapter(keepListAdapter);

        listKeep(memberSeq, GeoItem.getKnownLocation());
    }

    /**
     * 서버에서 즐겨찾기한 맛집 정보를 조회한다.
     * @param memberSeq 사용자 시퀀스
     * @param userLatLng 사용자 위도 경도 객체
     */
    private void listKeep(int memberSeq, LatLng userLatLng) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<ArrayList<KeepItem>> call
                = remoteService.listKeep(memberSeq, userLatLng.latitude, userLatLng.longitude);
        call.enqueue(new Callback<ArrayList<KeepItem>>() {
            @Override
            public void onResponse(Call<ArrayList<KeepItem>> call,
                                   Response<ArrayList<KeepItem>> response) {
                ArrayList<KeepItem> list = response.body();

                if (list == null) {
                    list = new ArrayList<>();
                }

                noDataText.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    MyLog.d(TAG, "list size " + list.size());
                    if (list.size() == 0) {
                        noDataText.setVisibility(View.VISIBLE);
                    } else {
                        keepListAdapter.setItemList(list);
                    }
                } else {
                    MyLog.d(TAG, "not success");
                }
            }

            @Override
            public void onFailure(Call<ArrayList<KeepItem>> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
                MyLog.d(TAG, t.toString());
            }
        });
    }
}