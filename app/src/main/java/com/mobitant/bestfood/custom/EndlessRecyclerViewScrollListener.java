package com.mobitant.bestfood.custom;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * 리사이클러뷰에서 스크롤 할 경우, 데이터를 추가적으로 가지고 올 수 있도록 해주는 추상 클래스
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
    //새로운 아이템을 로드하기 위한 현재 스크롤 위치 하단의 아이템 개수
    private int visibleThreshold = 5;
    // 로딩할 페이지 인덱스
    private int currentPage = 0;
    // 최근 로딩 후의 전체 아이템 개수
    private int previousTotalItemCount = 0;
    // 로딩하고 있는 중인지에 대한 상태
    private boolean loading = true;
    // 시작 페이지 인덱스
    private int startingPageIndex = 0;

    RecyclerView.LayoutManager layoutManager;

    /**
     * LinearLayoutManager를 위한 생성자
     * @param layoutManager LinearLayoutManager
     */
    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    /**
     * GridLayoutManager를 위한 생성자
     * @param layoutManager GridLayoutManager
     */
    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    /**
     * StaggeredGridLayoutManager를 위한 생성자
     * @param layoutManager StaggeredGridLayoutManager
     */
    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    /**
     * 현재 화면의 여러 스팬 중에서 가장 마지막에 보이는 아이템의 위치를 반환한다.
     * @param lastVisibleItemPositions 마지막에 보이는 아이템의 포지션 값들
     * @return 가장 마지막 아이템 위치
     */
    public int getLastVisibleItemPosition(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    /**
     * 스크롤된 후에 호출되는 컬백 메소드이며 아이템을 추가로 로딩해야 하는지를 체크한다.
     * @param view 리사이클러뷰
     * @param dx 수평으로 스크롤된 양
     * @param dy 수직으로 스크롤된 양
     */
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = 0;
        int totalItemCount = layoutManager.getItemCount();

        if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions =
                    ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition = getLastVisibleItemPosition(lastVisibleItemPositions);
        } else if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition =
                    ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            lastVisibleItemPosition =
                    ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }

        // 새로 로딩한 전체 아이템 개수가 이전에 설정된 전체 아이템 개수보다 작을 경우
        // 상태를 초기화한다. 이런 경우는 리사이클러뷰의 데이터가 초기화된 경우에 발생한다.
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }

        // 현재 로딩중인 상태이고 새로 로딩한 전체 아이템 개수가 이전에 저장한 전체 아이템 개수보다
        // 크다면 로딩이 완료한 것으로 간주한다.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        // 로딩중이 아니고 화면에 보이는 마지막 아이템 위치에 visibleThreshold을 더한 값이
        // totalItemCount 보다 큰 경우 새로 로딩할 아이템이 있는 것으로 간주한다.
        // 이때 onLoadMore() 메소드가 호출된다.
        if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
            currentPage++;
            onLoadMore(currentPage, totalItemCount, view);
            loading = true;
        }
    }

    /**
     * 아이템을 더 로딩하기 위한 메소드로서 해당 메소드를 직접 작성해야 한다.
     * @param page 로딩할 페이지
     * @param totalItemsCount 전체 아이템 개수
     * @param view 리사이클러뷰
     */
    public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

}