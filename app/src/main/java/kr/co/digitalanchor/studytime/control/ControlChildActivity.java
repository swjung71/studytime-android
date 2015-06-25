package kr.co.digitalanchor.studytime.control;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.chat.ParentChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.monitor.MonitorService;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-12.
 */
public class ControlChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener {

    TextView mLabelPoint;

    ImageButton mButtonPoint;

    ImageButton mButtonMenu;

    CheckBox mButtonShutdown;

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

        initView();

        getData();

        mHelper = new DBHelper(getApplicationContext());
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

        drawView();
    }

    private void getData() {

        Bundle data = getIntent().getExtras();

        mChild = new Child();

        if (data == null) {

            finish();

            Logger.e("Extra is null");

            return;
        }

        if (data.containsKey("ChildID")) {

            mChild.setChildID(data.getString("ChildID"));
        }

        if (data.containsKey("Name")) {

            mChild.setName(data.getString("Name"));
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

                Logger.d("shutdown");

                testOnOff();

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

        Toast.makeText(getApplicationContext(), "onClickFAQ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickInquiry() {

        sendEmail();
    }

    @Override
    public void onClickLogOut() {

        mHelper.clearAll();

        Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickWithdraw() {

        showWithDraw();

    }

    private void drawView() {

        Account account = mHelper.getAccountInfo();

        if (mMenu != null) {

            mMenu.setName(account.getName());
        }

        if (mLabelPoint != null) {

            mLabelPoint.setText(account.getCoin());
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

    private void testOnOff() {

        String i = STApplication.getString("service", "off");

        Logger.d("testOnOff() " + i);

        if (i.equals("off")) {

            STApplication.putString("service", "on");

            startService(new Intent(getApplicationContext(), MonitorService.class));

        } else {

            STApplication.putString("service", "off");

            stopService(new Intent(getApplicationContext(), MonitorService.class));
        }

    }

    private void requestOnOff() {

        Account account = mHelper.getAccountInfo();

        ParentOnOff model = new ParentOnOff();

        model.setParentID(account.getID());
        model.setChildID(mChild.getChildID());
        model.setIsOff("1");

        HttpHelper.getParentOnOff(model,
                new Response.Listener<GeneralResult>() {
                    @Override
                    public void onResponse(GeneralResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

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
    }

    private void sendEmail() {

        Intent intent = new Intent(Intent.ACTION_SEND);

        Account account = mHelper.getAccountInfo();

        String text = account.getEmail() + "이 보낸메일 \n";

        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String [] {"support@digitalanchor.co.kr"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "1:1 상담");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }
}
