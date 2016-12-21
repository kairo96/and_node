package com.mobitant.bestfood.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitant.bestfood.Constant;
import com.mobitant.bestfood.R;
import com.mobitant.bestfood.item.FoodInfoItem;
import com.mobitant.bestfood.item.KeepItem;
import com.mobitant.bestfood.lib.DialogLib;
import com.mobitant.bestfood.lib.GoLib;
import com.mobitant.bestfood.lib.MyLog;
import com.mobitant.bestfood.lib.StringLib;
import com.mobitant.bestfood.remote.RemoteService;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 맛집 정보 즐겨찾기 리스트의 아이템을 처리하는 어댑터
 */
public class KeepListAdapter extends RecyclerView.Adapter<KeepListAdapter.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    private int resource;
    private ArrayList<KeepItem> itemList;
    private int memberSeq;

    /**
     * 어댑터 생성자
     * @param context 컨텍스트 객체
     * @param resource 아이템을 보여주기 위해 사용할 리소스 아이디
     * @param itemList 아이템 리스트
     */
    public KeepListAdapter(Context context, int resource, ArrayList<KeepItem> itemList, int memberSeq) {
        this.context = context;
        this.resource = resource;
        this.itemList = itemList;
        this.memberSeq = memberSeq;
    }

    /**
     * 새로운 아이템 리스트를 설정한다.
     * @param itemList 새로운 아이템 리스트
     */
    public void setItemList(ArrayList<KeepItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    /**
     * 특정 아이템의 변경사항을 적용하기 위해 기본 아이템을 새로운 아이템으로 변경한다.
     * @param newItem 새로운 아이템
     */
    public void setItem(FoodInfoItem newItem) {
        for (int i=0; i < itemList.size(); i++) {
            KeepItem item = itemList.get(i);

            if (item.seq == newItem.seq && !newItem.isKeep) {
                itemList.remove(i);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 맛집 정보 시퀀스와 일치하는 아이템을 즐겨찾기 리스트에서 삭제한다.
     * @param seq
     */
    private void removeItem(int seq) {
        for (int i=0; i < itemList.size(); i++) {
            if (itemList.get(i).seq == seq) {
                itemList.remove(i);
                notifyItemChanged(i);
                break;
            }
        }
    }

    /**
     * 아이템 크기를 반환한다.
     * @return 아이템 크기
     */
    @Override
    public int getItemCount() {
        return this.itemList.size();
    }

    /**
     * 뷰홀더(ViewHolder)를 생성하기 위해 자동으로 호출된다.
     * @param parent 부모 뷰그룹
     * @param viewType 새로운 뷰의 뷰타입
     * @return 뷰홀더 객체
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);

        return new ViewHolder(v);
    }

    /**
     * 뷰홀더(ViewHolder)와 아이템을 리스트 위치에 따라 연동한다.
     * @param holder 뷰홀더 객체
     * @param position 리스트 위치
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final KeepItem item = itemList.get(position);
        MyLog.d(TAG, "getView " + item);

        if (item.isKeep) {
            holder.keep.setImageResource(R.drawable.ic_keep_on);
        } else {
            holder.keep.setImageResource(R.drawable.ic_keep_off);
        }

        holder.name.setText(item.name);
        holder.description.setText(
                StringLib.getInstance().getSubString(context,
                                        item.description, Constant.MAX_LENGTH_DESCRIPTION));

        setImage(holder.image, item.imageFilename);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoLib.getInstance().goBestFoodInfoActivity(context, item.seq);
            }
        });

        holder.keep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogLib.getInstance().showKeepDeleteDialog(context, keepHandler, memberSeq, item.seq);
            }
        });
    }
    /**
     * 이미지를 설정한다.
     * @param imageView  이미지를 설정할 뷰
     * @param fileName 이미지 파일이름
     */
    private void setImage(ImageView imageView, String fileName) {
        MyLog.d(TAG, "setImage fileName " + fileName);

        if (StringLib.getInstance().isBlank(fileName)) {
            Picasso.with(context).load(R.drawable.bg_bestfood_drawer).into(imageView);
        } else {
            Picasso.with(context).load(RemoteService.IMAGE_URL + fileName).into(imageView);
        }
    }

    /**
     * 즐겨찾기 리스트에서 해당 아이템을 삭제하기 위한 핸들러
     */
    Handler keepHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            removeItem(msg.what);
        }
    };

    /**
     * 아이템을 보여주기 위한 뷰홀더 클래스
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView keep;
        TextView name;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image);
            keep = (ImageView) itemView.findViewById(R.id.keep);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }
}
