package kr.co.digitalanchor.studytime.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.adpopcorn.IgawAdpopcornExtension;
import com.igaworks.interfaces.IgawRewardItem;
import com.igaworks.interfaces.IgawRewardItemEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.SetCoin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;

import static kr.co.digitalanchor.studytime.StaticValues.REGISTER_CHILD;
import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ListChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener, AdapterView.OnItemClickListener, IgawRewardItemEventListener {

    private final int REQUEST_UPDATE_COIN = 50001;

    TextView mLabelPoint;

    ImageButton mButtonPoint;

    ImageButton mButtonMenu;

    ListView mList;

    MenuPopup mMenu;

    View mHeader;

    View mFooter;

    ChildListAdapter mAdapter;

    ArrayList<Child> mChildren;

    DBHelper mHelper;

    RegisterChildReceiver registerChildReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_list);

        initView();

        mHelper = new DBHelper(getApplicationContext());

        IgawCommon.setClientRewardEventListener(this);

        IgawAdpopcornExtension.getClientPendingRewardItems(getApplicationContext());

    }

    public void initView() {

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        mButtonPoint = (ImageButton) findViewById(R.id.buttonPoint);
        mButtonPoint.setOnClickListener(this);

        mButtonMenu = (ImageButton) findViewById(R.id.buttonMenu);
        mButtonMenu.setOnClickListener(this);

        mMenu = new MenuPopup(getApplicationContext());
        mMenu.setOnClickMenuItemListener(this);

        findViewById(R.id.buttonSendLink).setOnClickListener(this);

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

        mHeader = inflater.inflate(R.layout.layout_child_header, null);
        mFooter = inflater.inflate(R.layout.layout_child_footer, null);
        mFooter.setOnClickListener(this);

        mList = (ListView) findViewById(R.id.list);

        mList.setOnItemClickListener(this);

        mList.setEmptyView(findViewById(android.R.id.empty));
        mList.addHeaderView(mHeader);
        mList.addFooterView(mFooter);

        mList.setAdapter(makeAdapter());

    }


    @Override
    protected void onStart() {
        super.onStart();

        Logger.d("onStart");

        drawView();

        getData();

        if (registerChildReceiver == null) {

            registerChildReceiver = new RegisterChildReceiver();
        }

        IntentFilter intentFilter = new IntentFilter(REGISTER_CHILD);

        registerReceiver(registerChildReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(registerChildReceiver);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Child child = mChildren.get(position - 1);

        showChildDetail(child);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonPoint:

                if (!isDuplicateRuns()) {

                    showOfferWall();
                }

                break;

            case R.id.buttonMenu:

                if (mMenu != null) {

                    mMenu.show(v);
                }

                break;

            case R.id.footerSendLink:
            case R.id.buttonSendLink:

                sendLink();

                break;

            default:

                break;
        }
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_UPDATE_COIN:

                break;

            default:

                Toast.makeText(getApplicationContext(), "onHandleMessage default", Toast.LENGTH_SHORT).show();

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
    public void onClickWithdraw() {

        showWithDraw();

    }

    @Override
    public void onClickLogOut() {

        STApplication.resetApplication();

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

    private ChildListAdapter makeAdapter() {

        if (mChildren == null) {

            mChildren = new ArrayList<>();
        }

        mAdapter = new ChildListAdapter(getApplicationContext(), 0, mChildren);

        return mAdapter;
    }

    private void showWithDraw() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), WithdrawActivity.class);

        startActivity(intent);
    }

    private void showOfferWall() {

        Account account = mHelper.getAccountInfo();

        IgawCommon.setUserId(account.getID());

//        AdPOPcornStyler.themeStyle.rewardThemeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.themeColor = Color.parseColor("#A65EA8");
//        AdPOPcornStyler.themeStyle.rewardCheckThemeColor = Color.parseColor("#A65EA8");

        IgawAdpopcorn.openOfferWall(ListChildActivity.this);

    }

    private void showChildDetail(Child child) {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ControlChildActivity.class);

        intent.putExtra("ChildID", child.getChildID());
        intent.putExtra("Name", child.getName());

        startActivity(intent);

    }

    private void showModifyInfo() {

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ModPrivacyActivity.class);

        startActivity(intent);
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

    private void sendLink() {

        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.putExtra("addres", "");
            intent.putExtra("sms_body", "https://play.google.com/store/apps/details?id="
                    + this.getPackageName());
            intent.setType("vnd.android-dir/mms-sms");

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(),
                    "SMS fail, please try again later!", Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void getData() {

        mChildren = mHelper.getChildren();

        mList.setAdapter(makeAdapter());
    }

    @Override
    public void onGetRewardInfo(boolean b, String s, IgawRewardItem[] igawRewardItems) {

        int point = 0;

        for (IgawRewardItem item : igawRewardItems) {

            point += item.getRewardQuantity();

            Logger.d("Reward " + point);

            item.didGiveRewardItem();
        }

        requestUpdatePoint(point);
    }

    @Override
    public void onDidGiveRewardItemResult(boolean b, String s, int i, String s1) {


    }

    private void requestUpdatePoint(int point) {

        final Account account = mHelper.getAccountInfo();

        final SetCoin model = new SetCoin();

        model.setParentID(account.getID());
        model.setCoin(account.getCoin() + point);

        SimpleXmlRequest request = HttpHelper.getUpdateCoin(model,
                new Response.Listener<CoinResult>() {

                    @Override
                    public void onResponse(CoinResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                mHelper.updateCoin(account.getID(), model.getCoin());

                                drawView();

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

    class RegisterChildReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Logger.d(intent.toString());

            if (intent.getAction().equals(StaticValues.REGISTER_CHILD)) {

                getData();
            }
        }
    }
}
