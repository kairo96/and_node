package com.mobitant.bestfood.lib;

import android.os.Handler;

import com.mobitant.bestfood.remote.RemoteService;
import com.mobitant.bestfood.remote.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 즐겨찾기 관련 라이브러리
 */
public class KeepLib {
    public final String TAG = KeepLib.class.getSimpleName();
    private volatile static KeepLib instance;

    public static KeepLib getInstance() {
        if (instance == null) {
            synchronized (KeepLib.class) {
                if (instance == null) {
                    instance = new KeepLib();
                }
            }
        }
        return instance;
    }

    /**
     * 즐겨찾기 추가를 서버에 요청한다.
     * @param handler 결과를 응답할 핸들러
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void insertKeep(final Handler handler, int memberSeq, final int infoSeq) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.insertKeep(memberSeq, infoSeq);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    MyLog.d(TAG, "insertKeep " + response);
                    handler.sendEmptyMessage(infoSeq);
                } else { // 등록 실패
                    MyLog.d(TAG, "response error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
            }
        });
    }

    /**
     * 즐겨찾기 삭제를 서버에 요청한다.
     * @param handler 결과를 응답할 핸들러
     * @param memberSeq 사용자 일련번호
     * @param infoSeq 맛집 정보 일련번호
     */
    public void deleteKeep(final Handler handler, int memberSeq, final int infoSeq) {
        RemoteService remoteService = ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.deleteKeep(memberSeq, infoSeq);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    MyLog.d(TAG, "deleteKeep " + response);
                    handler.sendEmptyMessage(infoSeq);
                } else { // 등록 실패
                    MyLog.d(TAG, "response error " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                MyLog.d(TAG, "no internet connectivity");
            }
        });
    }
}
