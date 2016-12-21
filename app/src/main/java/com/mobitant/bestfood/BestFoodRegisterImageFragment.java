package com.mobitant.bestfood;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mobitant.bestfood.item.ImageItem;
import com.mobitant.bestfood.lib.BitmapLib;
import com.mobitant.bestfood.lib.FileLib;
import com.mobitant.bestfood.lib.GoLib;
import com.mobitant.bestfood.lib.MyLog;
import com.mobitant.bestfood.lib.MyToast;
import com.mobitant.bestfood.lib.RemoteLib;
import com.mobitant.bestfood.lib.StringLib;
import com.mobitant.bestfood.remote.RemoteService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

/**
 * 맛집 이미지를 등록하는 액티비티
 */
public class BestFoodRegisterImageFragment extends Fragment implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    public static final String INFO_SEQ = "INFO_SEQ";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;

    Activity context;
    int infoSeq;

    File imageFile;
    String imageFilename;

    EditText imageMemoEdit;
    ImageView infoImage;

    ImageItem imageItem;

    boolean isSavingImage = false;

    /**
     * FoodInfoItem 객체를 인자로 저장하는
     * BestFoodRegisterInputFragment 인스턴스를 생성해서 반환한다.
     * @param infoSeq 서버에 저장한 맛집 정보에 대한 시퀀스
     * @return BestFoodRegisterImageFragment 인스턴스
     */
    public static BestFoodRegisterImageFragment newInstance(int infoSeq) {
        Bundle bundle = new Bundle();
        bundle.putInt(INFO_SEQ, infoSeq);

        BestFoodRegisterImageFragment f = new BestFoodRegisterImageFragment();
        f.setArguments(bundle);

        return f;
    }

    /**
     * 프래그먼트가 생성될 때 호출되며 인자에 저장된 INFO_SEQ를 멤버 변수 infoSeq에 저장한다.
     * @param savedInstanceState 프래그먼트가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            infoSeq = getArguments().getInt(INFO_SEQ);
        }
    }

    /**
     * fragment_bestfood_register_image.xml 기반으로 뷰를 생성한다.
     * @param inflater XML를 객체로 변환하는 LayoutInflater 객체
     * @param container null이 아니라면 부모 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     * @return 생성한 뷰 객체
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View v = inflater.inflate(R.layout.fragment_bestfood_register_image, container, false);

        return v;
    }

    /**
     * onCreateView() 메소드 뒤에 호출되며 기본 정보 생성과 화면 처리를 한다.
     * @param view onCreateView() 메소드에 의해 반환된 뷰
     * @param savedInstanceState null이 아니라면 이전에 저장된 상태를 가진 객체
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageItem = new ImageItem();
        imageItem.infoSeq = infoSeq;

        imageFilename = infoSeq + "_" + String.valueOf(System.currentTimeMillis());
        imageFile = FileLib.getInstance().getImageFile(context, imageFilename);

        infoImage = (ImageView) view.findViewById(R.id.bestfood_image);
        imageMemoEdit = (EditText) view.findViewById(R.id.register_image_memo);

        ImageView imageRegister = (ImageView) view.findViewById(R.id.bestfood_image_register);
        imageRegister.setOnClickListener(this);

        view.findViewById(R.id.prev).setOnClickListener(this);
        view.findViewById(R.id.complete).setOnClickListener(this);
    }

    /**
     * 이미지를 촬영하고 그 결과를 받을 수 있는 액티비티를 시작한다.
     */
    private void getImageFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
        context.startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    /**
     * 앨범으로부터 이미지를 선택할 수 있는 액티비티를 시작한다.
     */
    private void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        context.startActivityForResult(intent, PICK_FROM_ALBUM);
    }

    /**
     * 클릭이벤트를 처리한다.
     * @param v 클릭한 뷰에 대한 정보
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bestfood_image_register) {
            showImageDialog(context);
        } else if (v.getId() == R.id.complete) {
            saveImage();
        } else if (v.getId() == R.id.prev) {
            GoLib.getInstance().goBackFragment(getFragmentManager());
        }
    }

    /**
     * 다른 액티비티를 실행한 결과를 처리하는 메소드
     * @param requestCode 액티비티를 실행하면서 전달한 요청 코드
     * @param resultCode 실행한 액티비티가 설정한 결과 코드
     * @param data 결과 데이터
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PICK_FROM_CAMERA) {
                Picasso.with(context).load(imageFile).into(infoImage);

            } else if (requestCode == PICK_FROM_ALBUM && data != null) {
                Uri dataUri = data.getData();

                if (dataUri != null) {
                    Picasso.with(context).load(dataUri).into(infoImage);

                    Picasso.with(context).load(dataUri).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            BitmapLib.getInstance().saveBitmapToFileThread(imageUploadHandler,
                                    imageFile, bitmap);
                            isSavingImage = true;
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
                }
            }
        }
    }

    /**
     * 사용자가 선택한 이미지와 입력한 메모를 ImageItem 객체에 저장한다.
     */
    private  void setImageItem() {
        String imageMemo = imageMemoEdit.getText().toString();
        if (StringLib.getInstance().isBlank(imageMemo)) {
            imageMemo = "";
        }

        imageItem.imageMemo = imageMemo;
        imageItem.fileName = imageFilename + ".png";
    }

    /**
     * 이미지를 서버에 업로드한다.
     */
    private void saveImage() {
        if (isSavingImage) {
            MyToast.s(context, R.string.no_image_ready);
            return;
        }
        MyLog.d(TAG, "imageFile.length() " + imageFile.length());

        if (imageFile.length() == 0) {
            MyToast.s(context, R.string.no_image_selected);
            return;
        }

        setImageItem();

        RemoteLib.getInstance().uploadFoodImage(infoSeq,
                imageItem.imageMemo, imageFile, finishHandler);
        isSavingImage = false;
    }

    /**
     * 이미지를 어떤 방식으로 선택할지에 대해 다이얼로그를 보여준다.
     * @param context 컨텍스트 객체
     */
    public void showImageDialog(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.title_bestfood_image_register)
                .setSingleChoiceItems(R.array.camera_album_category, -1,
                        new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            getImageFromCamera();
                        } else {
                            getImageFromAlbum();
                        }

                        dialog.dismiss();
                    }
                }).show();
    }

    Handler imageUploadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isSavingImage = false;
            setImageItem();
            Picasso.with(context).invalidate(RemoteService.IMAGE_URL + imageItem.fileName);
        }
    };

    Handler finishHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            context.finish();
        }
    };
}
