package com.mobitant.bestfood;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobitant.bestfood.item.MemberInfoItem;
import com.mobitant.bestfood.lib.EtcLib;
import com.mobitant.bestfood.lib.MyLog;
import com.mobitant.bestfood.lib.MyToast;
import com.mobitant.bestfood.lib.StringLib;
import com.mobitant.bestfood.remote.RemoteService;
import com.mobitant.bestfood.remote.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 프로필을 설정할 수 있는 액티비티
 */
public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = this.getClass().getSimpleName();
    Context context;

    ImageView profileIconImage;
    ImageView profileIconChangeImage;
    EditText nameEdit;
    EditText sextypeEdit;
    EditText birthEdit;
    EditText phoneEdit;

    MemberInfoItem currentItem;

    /**
     * 액티비티를 생성하고 화면을 구성한다.
     * @param savedInstanceState 액티비티가 새로 생성되었을 경우, 이전 상태 값을 가지는 객체
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        context = this;

        currentItem = ((MyApp) getApplication()).getMemberInfoItem();

        setToolbar();
        setView();
    }

    /**
     * 화면이 보여질 때 호출되며 사용자 정보를 기반으로 프로필 아이콘을 설정한다.
     */
    @Override
    protected void onResume() {
        super.onResume();

        MyLog.d(TAG, RemoteService.MEMBER_ICON_URL + currentItem.memberIconFilename);

        if (StringLib.getInstance().isBlank(currentItem.memberIconFilename)) {
            Picasso.with(this).load(R.drawable.ic_person).into(profileIconImage);
        } else {
            Picasso.with(this)
                    .load(RemoteService.MEMBER_ICON_URL + currentItem.memberIconFilename)
                    .into(profileIconImage);
        }
    }

    /**
     * 액티비티 툴바를 설정한다.
     */
    private void setToolbar() {
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.profile_setting);
        }
    }

    /**
     * 액티비티 화면을 설정한다.
     */
    private void setView() {
        profileIconImage = (ImageView) findViewById(R.id.profile_icon);
        profileIconImage.setOnClickListener(this);

        profileIconChangeImage = (ImageView) findViewById(R.id.profile_icon_change);
        profileIconChangeImage.setOnClickListener(this);

        nameEdit = (EditText) findViewById(R.id.profile_name);
        nameEdit.setText(currentItem.name);

        sextypeEdit = (EditText) findViewById(R.id.profile_sextype);
        sextypeEdit.setText(currentItem.sextype);
        sextypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSexTypeDialog();
            }
        });

        birthEdit = (EditText) findViewById(R.id.profile_birth);
        birthEdit.setText(currentItem.birthday);
        birthEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBirthdayDialog();
            }
        });

        String phoneNumber = EtcLib.getInstance().getPhoneNumber(context);

        phoneEdit = (EditText) findViewById(R.id.profile_phone);
        phoneEdit.setText(phoneNumber);

        TextView phoneStateEdit = (TextView) findViewById(R.id.phone_state);
        if (phoneNumber.startsWith("0")) {
            phoneStateEdit.setText("(" + getResources().getString(R.string.device_number) + ")");
        } else {
            phoneStateEdit.setText("(" + getResources().getString(R.string.phone_number) + ")");
        }
    }

    /**
     * 성별을 선택할 수 있는 다이얼로그를 보여준다.
     */
    private void setSexTypeDialog() {
        final String[] sexTypes = new String[2];
        sexTypes[0] = getResources().getString(R.string.sex_man);
        sexTypes[1] = getResources().getString(R.string.sex_woman);

        new AlertDialog.Builder(this)
                .setItems(sexTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which >= 0) {
                            sextypeEdit.setText(sexTypes[which]);
                        }
                        dialog.dismiss();
                    }
                }).show();
    }

    /**
     * 생일을 선택할 수 있는 다이얼로그를 보여준다.
     */
    private void setBirthdayDialog() {
        GregorianCalendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String myMonth;
                if (monthOfYear + 1 < 10) {
                    myMonth = "0" + (monthOfYear + 1);
                } else {
                    myMonth = "" + (monthOfYear + 1);
                }

                String myDay;
                if (dayOfMonth < 10) {
                    myDay = "0" + dayOfMonth;
                } else {
                    myDay = "" + dayOfMonth;
                }

                String date = year + " " + myMonth + " " + myDay;
                birthEdit.setText(date);
            }
        }, year, month, day).show();
    }

    /**
     * 오른쪽 상단 메뉴를 구성한다.
     * 닫기 메뉴만이 설정되어 있는 menu_close.xml를 지정한다.
     * @param menu 메뉴 객체
     * @return 메뉴를 보여준다면 true, 보여주지 않는다면 false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    /**
     * 왼쪽 화살표 메뉴(android.R.id.home)를 클릭했을 때와
     * 오른쪽 상단 닫기 메뉴를 클릭했을 때의 동작을 지정한다.
     * 여기서는 모든 버튼이 액티비티를 종료한다.
     * @param item 메뉴 아이템 객체
     * @return 메뉴를 처리했다면 true, 그렇지 않다면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                close();
                break;

            case R.id.action_submit:
                save();
                break;
        }

        return true;
    }

    /**
     * 사용자가 입력한 정보를 MemberInfoItem 객체에 저장해서 반환한다.
     * @return 사용자 정보 객체
     */
    private MemberInfoItem getMemberInfoItem() {
        MemberInfoItem item = new MemberInfoItem();
        item.phone = EtcLib.getInstance().getPhoneNumber(context);
        item.name = nameEdit.getText().toString();
        item.sextype = sextypeEdit.getText().toString();
        item.birthday = birthEdit.getText().toString().replace(" ", "");

        return item;
    }

    /**
     * 기존 사용자 정보와 새로 입력한 사용자 정보를 비교해서 변경되었는지를 파악한다.
     * @param newItem 사용자 정보 객체
     * @return 변경되었다면 true, 변경되지 않았다면 false
     */
    private boolean isChanged(MemberInfoItem newItem) {
        if (newItem.name.trim().equals(currentItem.name)
                && newItem.sextype.trim().equals(currentItem.sextype)
                && newItem.birthday.trim().equals(currentItem.birthday)) {
            Log.d(TAG, "return " + false);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 사용자가 이름을 입력했는지를 확인한다.
     * @param newItem 사용자가 새로 입력한 정보 객체
     * @return 입력하지 않았다면 true, 입력했다면 false
     */
    private boolean isNoName(MemberInfoItem newItem) {
        if (StringLib.getInstance().isBlank(newItem.name)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 화면이 닫히기 전에 변경 유무를 확인해서
     * 변경사항이 있다면 저장하고 없다면 화면을 닫는다.
     */
    private void close() {
        MemberInfoItem newItem = getMemberInfoItem();

        if (!isChanged(newItem) && !isNoName(newItem)) {
            finish();
        } else if (isNoName(newItem)) {
            MyToast.s(context, R.string.name_need);
            finish();
        } else {
            new AlertDialog.Builder(this).setTitle(R.string.change_save)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            save();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        }
    }

    /**
     * 사용자가 입력한 정보를 저장한다.
     */
    private void save() {
        final MemberInfoItem newItem = getMemberInfoItem();

        if (!isChanged(newItem)) {
            MyToast.s(this, R.string.no_change);
            finish();
            return;
        }

        MyLog.d(TAG, "insertItem " + newItem.toString());

        RemoteService remoteService =
                ServiceGenerator.createService(RemoteService.class);

        Call<String> call = remoteService.insertMemberInfo(newItem);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String seq = response.body();
                    try {
                        currentItem.seq = Integer.parseInt(seq);
                        if (currentItem.seq == 0) {
                            MyToast.s(context, R.string.member_insert_fail_message);
                            return;
                        }
                    } catch (Exception e) {
                        MyToast.s(context, R.string.member_insert_fail_message);
                        return;
                    }
                    currentItem.name = newItem.name;
                    currentItem.sextype = newItem.sextype;
                    currentItem.birthday = newItem.birthday;
                    finish();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }

    /**
     * 뒤로가기 버튼을 클릭했을 때, close() 메소드를 호출한다.
     */
    @Override
    public void onBackPressed() {
        close();
    }

    /**
     * 프로필 아이콘이나 프로필 아이콘 변경 뷰를 클릭했을 때, 프로필 아이콘을 변경할 수 있도록
     * startProfileIconChange() 메소드를 호출한다.
     * @param v 클릭한 뷰 객체
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_icon || v.getId() == R.id.profile_icon_change) {
            startProfileIconChange();
        }
    }

    /**
     * ProfileIconActivity를 실행해서 프로필 아이콘을 변경할 수 있게 한다.
     */
    private void startProfileIconChange() {
        Intent intent = new Intent(this, ProfileIconActivity.class);
        startActivity(intent);
    }
}