package kr.co.digitalanchor.studytime.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.StringRequest;
import com.igaworks.adbrix.IgawAdbrix;
import com.orhanobut.logger.Logger;


import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.chat.ParentChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.dialog.SettingOffDialog;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.ParentOnOff2;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.model.db.Child2;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.NotificationActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;
import kr.co.digitalanchor.utils.StringValidator;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;
import static kr.co.digitalanchor.utils.AndroidUtils.getCurrentTimeIncludeMs;

/**
 * Created by Thomas on 2015-06-12.
 */
public class ControlChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener {

    final int REQUEST_ON_OFF = 50001;

    TextView mLabelPoint;

    ImageButton mButtonPoint;

    ImageButton mButtonMenu;

    ImageButton mButtonToggle;

    ImageButton mButtonChat;

    ImageButton mButtonUse;

    Button mButtonCloseGuide;

    View mImgGuide;

    MenuPopup mMenu;

    DBHelper mHelper;

    Child2 mChild;

    String mChildID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_child);

        //mHelper = new DBHelper(getApplicationContext());
        mHelper = DBHelper.getInstance(getApplicationContext());

        getData();

        initView();

        firstVisit();
    }


    private void initView() {

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        mButtonMenu = (ImageButton) findViewById(R.id.buttonMenu);
        mButtonMenu.setOnClickListener(this);

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        /*mButtonPoint = (ImageButton) findViewById(R.id.buttonPoint);
        mButtonPoint.setOnClickListener(this);*/

        mButtonToggle = (ImageButton) findViewById(R.id.buttonShutdown);
        mButtonToggle.setOnClickListener(this);

      /*  mButtonChat = (ImageButton) findViewById(R.id.buttonChat);
        mButtonChat.setOnClickListener(this);*/

        mButtonUse = (ImageButton) findViewById(R.id.buttonUse);
        mButtonUse.setOnClickListener(this);

        mButtonCloseGuide = (Button) findViewById(R.id.buttonClose);
        mButtonCloseGuide.setOnClickListener(this);

        mImgGuide = findViewById(R.id.imgGuide);

        mMenu = new MenuPopup(getApplicationContext());
        mMenu.setOnClickMenuItemListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        getChildModel(mChildID);

        drawView();

        toggleOnOff();
    }

    private void getData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            finish();

            Logger.e("Extra is null");

            return;
        }

        mChildID = data.getString("ChildID");
    }

    private void getChildModel(String childId) {

        mChild = mHelper.getChild2(childId);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_ON_OFF:

                Account account = mHelper.getAccountInfo();

                if (mChild.getIsOFF() == 0) {

                    writeLog();
                    requestOnOff(0, null);

                    /*if (account.getCoin() > 0) {

                        requestOnOff();

                    } else {

                        Toast.makeText(getApplicationContext(), "코인이 부족합니다.", Toast.LENGTH_SHORT).show();

                    }*/

                } else {

                    requestOnOff(1, null);


                   /* final SettingOffDialog dialog = new SettingOffDialog(ControlChildActivity.this);

                    dialog.setCallback(new SettingOffDialog.OnSimpleCallback() {
                        @Override
                        public void onClickConfirm(int select, String password) {

                            if (StringValidator.isPassword(password)) {

                                requestOnOff(select, password);

                                dialog.dismiss();

                            } else {

                                Toast.makeText(ControlChildActivity.this,
                                        "비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onClickCancel() {

                            dialog.dismiss();
                        }
                    });

                    dialog.show();*/
                }

                break;

            default:
                break;
        }
    }

    private void requestOnOff(int min, String password) {

        showLoading();

        final Account account = mHelper.getAccountInfo();

        ParentOnOff2 model = new ParentOnOff2();

        model.setPhoneno(mChild.getPhoneNum());

        // 현재가 0이면 1로 바꿀 것임 따라서 1이면 off이며 현재 1이면 0으로 바뀜 따라서 on으로 호출해야 함
        model.setCmd(mChild.getIsOFF() == 0 ? "resume" : "pause");

        StringRequest request = HttpHelper.getParentOnOffForiOS(model, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Logger.i(response.toString());
                if(response.toString().contains("ok")) {

                    //0이면 on 1이면 off
                    if (mChild.getIsOFF() == 0) {
                        mHelper.updateChildToggle(mChild.getChildID(), 1);
                        mChild.setIsOFF(1);
                    } else {
                        mHelper.updateChildToggle(mChild.getChildID(), 0);
                        mChild.setIsOFF(0);
                    }
                }

                requestOnOff();
                drawView();

                toggleOnOff();

                dismissLoading();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.i(error.getMessage());
                Toast.makeText(getApplicationContext(), "자녀용 iOS를 on/off하던 도중 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                dismissLoading();
            }
        });

        addRequestForIOS(request);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            /*case R.id.buttonPoint:

                showOfferWall();

                break;*/

            case R.id.buttonMenu:

                if (mMenu != null) {

                    mMenu.show(v);
                }

                break;

            case R.id.buttonChat:

                showChat();

                break;

            case R.id.buttonUse:

                showGuide(true);

                break;

            case R.id.buttonClose:

                showGuide(false);

                break;

            case R.id.buttonShutdown:

                sendEmptyMessage(REQUEST_ON_OFF);


                break;

            default:

                break;
        }
    }

    @Override
    public void onClickModify() {

        showModifyInfo();
    }

    @Override
    public void onClickFAQ() {

        showFAQ();
    }

    @Override
    public void onClickInquiry() {

        sendEmail();
    }

    @Override
    public void onClickLogOut() {

        STApplication.resetApplication();
    }

    @Override
    public void onClickWithdraw() {

        showWithDraw();

    }

    @Override
    public void onClickNotice() {

        showNotice();
    }

    @Override
    public void onClickNotificationBooad() {

        showNotificationBoard();
    }

    private void showNotificationBoard() {

        IgawAdbrix.retention("notificationBoard");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), NotificationActivity.class);

        startActivity(intent);
    }

    private void drawView() {

        Account account = mHelper.getAccountInfo();

        if (mMenu != null) {

            mMenu.setName(account.getName());

            mMenu.addNewNotice((account.getNotice() > 0) ? true : false);
        }

        if (mLabelPoint != null) {

//            mLabelPoint.setText(String.valueOf(account.getCoin()));

            mLabelPoint.setText(getResources().getString(R.string.payment_info, mChild.getName(),
                    String.valueOf(mChild.getRemainingDays())));
        }
    }

    private void showWithDraw() {

        IgawAdbrix.retention("withdraw");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), WithdrawActivity.class);

        startActivity(intent);
    }

    private void showChat() {

        IgawAdbrix.retention("chatRoom");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ParentChatActivity.class);

        intent.putExtra("ChildID", mChild.getChildID());
        intent.putExtra("Name", mChild.getName());

        startActivity(intent);
    }

    private void showGuide(boolean flag) {

        if (flag) {

            mImgGuide.setVisibility(View.VISIBLE);

        } else {

            mImgGuide.setVisibility(View.GONE);
        }
    }

    private void showModifyInfo() {

        IgawAdbrix.retention("personalInfo");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ModPrivacyActivity.class);

        startActivity(intent);
    }

    private void showFAQ() {

        IgawAdbrix.retention("faq");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), BoardActivity.class);

        intent.putExtra("option", 1);

        startActivity(intent);
    }

    private void showNotice() {

        IgawAdbrix.retention("notices");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), BoardActivity.class);

        intent.putExtra("option", 0);

        startActivity(intent);
    }

    private void requestOnOff() {

        //showLoading();

        IgawAdbrix.retention("onOff");

        final Account account = mHelper.getAccountInfo();

        ParentOnOff model = new ParentOnOff();

        model.setParentID(account.getID());
        model.setChildID(mChild.getChildID());
        model.setIsOff(mChild.getIsOFF() == 0 ? "0" : "1");
        model.setName(account.getName());
        //model.setCoin(account.getCoin() - 1);


        SimpleXmlRequest request = HttpHelper.getParentOnOffWithoutPass(model,
                new Response.Listener<CoinResult>() {
                    @Override
                    public void onResponse(CoinResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                if (mChild.getIsOFF() == 0) {

                                    //mHelper.updateChildToggle(mChild.getChildID(), 1);

                                    //mHelper.updateCoin(account.getID(), response.getCoin());

                                    //mChild.setIsOFF(1);

                                } else {

                                    //mHelper.updateChildToggle(mChild.getChildID(), 0);

                                    //mChild.setIsOFF(0);

                                }

                                /*drawView();

                                toggleOnOff();*/

                                //dismissLoading();

                                break;

                            default:

                                handleResultCode(response.getResultCode(), response.getResultMessage());
                                //dismissLoading();
                                break;
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        handleError(error);
                    }
                });

        addRequest(request);
    }

    private void firstVisit() {

        boolean isFirst = STApplication.getBoolean(StaticValues.IS_SHOW_GUIDE, true);

        if (isFirst) {

            STApplication.putBoolean(StaticValues.IS_SHOW_GUIDE, false);

            mImgGuide.setVisibility(View.VISIBLE);
        }
    }

    private void toggleOnOff() {

        Logger.i("ControlChildActivity :O isOff : " + mChild.getIsOFF());

        switch (mChild.getIsOFF()) {

            case 0:

                mButtonToggle.setImageResource(R.drawable.button_on_selector);

                break;

            case 1:

                mButtonToggle.setImageResource(R.drawable.button_off_selector);

                break;

            default:

                break;
        }
    }

    private void writeLog(){
        File file = getFileSystem();

        Logger.i("File path : " + file.getAbsolutePath());

        //File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS), "logForOnOff.txt");

        //String filename = "logForOnOffParent";
        FileOutputStream outputStream;
        try{

            String value = "[잠금명령전송] " + getCurrentTimeIncludeMs() + "\n";

            outputStream = new FileOutputStream(file, true);
            outputStream.write(value.getBytes());
            outputStream.close();

        }catch (Exception e){
            Logger.e(e.getMessage());
        }


    }

    private File getFileSystem(){

        File file = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_ALARMS), "logForOnOff_iOS.txt");

        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*if (!file.mkdirs()) {
                Logger.e("Directory not created");
            }else{
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }

        return file;
    }
}
