package kr.co.digitalanchor.studytime.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.chat.ParentChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.SetCoin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-12.
 */
public class ControlChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener {

    final int REQUEST_ON_OFF = 50001;
    final int REQUEST_UPDATE_COIN = 50002;

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

    Child mChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_child);

        mHelper = new DBHelper(getApplicationContext());

        getData();

        initView();

        firstVisit();
    }

    private void initView() {

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        mButtonMenu = (ImageButton) findViewById(R.id.buttonMenu);
        mButtonMenu.setOnClickListener(this);

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        mButtonPoint = (ImageButton) findViewById(R.id.buttonPoint);
        mButtonPoint.setOnClickListener(this);

        mButtonToggle = (ImageButton) findViewById(R.id.buttonShutdown);
        mButtonToggle.setOnClickListener(this);

        if (mChild.getIsOFF() == 0)
            mButtonToggle.setImageResource(R.drawable.button_on_selector);

        else
            mButtonToggle.setImageResource(R.drawable.button_off_selector);

        mButtonChat = (ImageButton) findViewById(R.id.buttonChat);
        mButtonChat.setOnClickListener(this);

        mButtonUse = (ImageButton) findViewById(R.id.buttonUse);
        mButtonUse.setOnClickListener(this);

        mButtonCloseGuide = (Button) findViewById(R.id.buttonClose);
        mButtonCloseGuide.setOnClickListener(this);

        mImgGuide = findViewById(R.id.imgGuide);

        mMenu = new MenuPopup(getApplicationContext());
        mMenu.setOnClickMenuItemListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        drawView();
    }

    private void getData() {

        Bundle data = getIntent().getExtras();

        if (data == null) {

            finish();

            Logger.e("Extra is null");

            return;
        }

        if (data.containsKey("ChildID")) {

            mChild = mHelper.getChild(data.getString("ChildID"));

            if (mChild == null)
                finish();
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_ON_OFF:

                requestOnOff();

                break;

            case REQUEST_UPDATE_COIN:

                Account account = mHelper.getAccountInfo();

                if (account.getCoin() > 0) {

                    requestUpdatePoint();

                } else {

                    Toast.makeText(getApplicationContext(), "코인이 부족합니다.", Toast.LENGTH_SHORT).show();
                }

                break;

            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonPoint:

                showOfferWall();

                break;

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

                if (mChild.getIsOFF() == 0 ) {

                    sendEmptyMessage(REQUEST_UPDATE_COIN);

                } else {

                    sendEmptyMessage(REQUEST_ON_OFF);
                }

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

    private void drawView() {

        Account account = mHelper.getAccountInfo();

        if (mMenu != null) {

            mMenu.setName(account.getName());
        }

        if (mLabelPoint != null) {

            mLabelPoint.setText(String.valueOf(account.getCoin()));
        }
    }

    private void showOfferWall() {

        Account account = mHelper.getAccountInfo();

        IgawCommon.setUserId(account.getID());

//        AdPOPcornStyler.themeStyle.rewardThemeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.themeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.rewardCheckThemeColor = Color.parseColor("#A65EA8");

        IgawAdpopcorn.openOfferWall(ControlChildActivity.this);
    }

    private void showWithDraw() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), WithdrawActivity.class);

        startActivity(intent);
    }

    private void showChat() {

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

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ModPrivacyActivity.class);

        startActivity(intent);
    }

    private void showFAQ() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), BoardActivity.class);

        intent.putExtra("option", 1);

        startActivity(intent);
    }

    private void showNotice() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), BoardActivity.class);

        intent.putExtra("option", 0);

        startActivity(intent);
    }

    private void requestUpdatePoint() {

        showLoading();

        final Account account = mHelper.getAccountInfo();

        final SetCoin model = new SetCoin();

        model.setParentID(account.getID());
        model.setCoin(account.getCoin() - 1);

        SimpleXmlRequest request = HttpHelper.getUpdateCoin(model,
                new Response.Listener<GeneralResult>() {

                    @Override
                    public void onResponse(GeneralResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                mHelper.updateCoin(account.getID(), model.getCoin());

                                sendEmptyMessage(REQUEST_ON_OFF);

                                break;

                            default:

                                handleResultCode(response.getResultCode(),
                                        response.getResultMessage());

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

    private void requestOnOff() {

        Account account = mHelper.getAccountInfo();

        ParentOnOff model = new ParentOnOff();

        model.setParentID(account.getID());
        model.setChildID(mChild.getChildID());
        model.setIsOff(mChild.getIsOFF() == 0 ? "1" : "0");
        model.setName(account.getName());

        SimpleXmlRequest request = HttpHelper.getParentOnOff(model,
                new Response.Listener<GeneralResult>() {
                    @Override
                    public void onResponse(GeneralResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                if (mChild.getIsOFF() == 0) {

                                    mHelper.updateChildToggle(mChild.getChildID(), 1);

                                    mChild.setIsOFF(1);

                                    mButtonToggle.setImageResource(R.drawable.button_off_selector);

                                } else {

                                    mHelper.updateChildToggle(mChild.getChildID(), 0);

                                    mChild.setIsOFF(0);

                                    mButtonToggle.setImageResource(R.drawable.button_on_selector);

                                }

                                dismissLoading();

                                break;

                            default:

                                handleResultCode(response.getResultCode(), response.getResultMessage());

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

    private void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        Account account = mHelper.getAccountInfo();

        String text = account.getEmail() + "이 보낸메일 \n";

        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@digitalanchor.co.kr"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "1:1 상담");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }

    private void firstVisit() {

        boolean isFirst = STApplication.getBoolean(StaticValues.IS_SHOW_GUIDE, true);

        if (isFirst) {

            STApplication.putBoolean(StaticValues.IS_SHOW_GUIDE, false);

            mImgGuide.setVisibility(View.VISIBLE);
        }
    }
}
