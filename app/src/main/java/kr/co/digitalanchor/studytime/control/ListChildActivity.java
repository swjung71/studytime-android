package kr.co.digitalanchor.studytime.control;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.adpopcorn.IgawAdpopcornExtension;
import com.igaworks.interfaces.IgawRewardItem;
import com.igaworks.interfaces.IgawRewardItemEventListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.NewNoticeResult;
import kr.co.digitalanchor.studytime.model.Notice;
import kr.co.digitalanchor.studytime.model.ParentLoginResult;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.SetCoin;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.NotificationActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.StaticValues.NEW_MESSAGE_ARRIVED;
import static kr.co.digitalanchor.studytime.StaticValues.REGISTER_CHILD;
import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ListChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener, AdapterView.OnItemClickListener,
        IgawRewardItemEventListener {

    private final int REQUEST_UPDATE_COIN = 50001;

    private final int REQUEST_UPDATE_DATA = 50002;

    TextView mLabelPoint;

    ImageButton mButtonPoint;

    ImageButton mButtonMenu;

    ListView mList;

    MenuPopup mMenu;

    View mHeader;

    View mFooter;

    TextView mBadge;

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

        getNewNotice();

    }

    public void initView() {

        mLabelPoint = (TextView) findViewById(R.id.labelPoint);

        mButtonPoint = (ImageButton) findViewById(R.id.buttonPoint);
        mButtonPoint.setOnClickListener(this);

        mButtonMenu = (ImageButton) findViewById(R.id.buttonMenu);
        mButtonMenu.setOnClickListener(this);

        mMenu = new MenuPopup(getApplicationContext());
        mMenu.setOnClickMenuItemListener(this);

        mBadge = (TextView) findViewById(R.id.badge);

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


        if (registerChildReceiver == null) {

            registerChildReceiver = new RegisterChildReceiver();
        }

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(NEW_MESSAGE_ARRIVED);
        intentFilter.addAction(REGISTER_CHILD);

        registerReceiver(registerChildReceiver, intentFilter);

        requestSyncData();

        updateBadge();
    }

    @Override
    protected void onStop() {
        super.onStop();

        unregisterReceiver(registerChildReceiver);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (position < 1) {

            return;
        }

        Child child = mChildren.get(position - 1);

        showChildDetail(child);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.buttonPoint:

//                if (!isDuplicateRuns()) {
//
//                    showOfferWall();
//                }

                if (!isDuplicateRuns()) {

                    showPurchase();
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

            case REQUEST_UPDATE_DATA:

                drawView();

                getData();

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

    @Override
    public void onClickNotificationBooad() {

        showNotificationBoard();
    }

    private void drawView() {

        Account account = mHelper.getAccountInfo();

        if (mMenu != null) {

            mMenu.setName(account.getName());

            mMenu.addNewNotice((account.getNotice() > 0) ? true : false);
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

        IgawAdbrix.retention("withdraw");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), WithdrawActivity.class);

        startActivity(intent);
    }

    private void showChildDetail(Child child) {

        IgawAdbrix.retention("child");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), ControlChildExActivity.class);

        intent.putExtra("ChildID", child.getChildID());
        intent.putExtra("Name", child.getName());

        startActivity(intent);

    }

    private void showNotificationBoard() {

        IgawAdbrix.retention("notificationBoard");

        Intent intent = new Intent();

        intent.setClass(getApplicationContext(), NotificationActivity.class);

        startActivity(intent);
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

        if (!b) {

            return;
        }

        for (IgawRewardItem item : igawRewardItems) {

            point += item.getRewardQuantity();

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

                                mHelper.updateCoin(account.getID(), response.getCoin());

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

    private void requestSyncData() {

        final Account account = mHelper.getAccountInfo();

        if (TextUtils.isEmpty(account.getID())) {

            return;
        }

        showLoading();

        ParentModel model = new ParentModel();
        model.setParentId(account.getID());

        SimpleXmlRequest request = HttpHelper.getSyncParentData(model, new Response.Listener<ParentLoginResult>() {

            @Override
            public void onResponse(ParentLoginResult response) {

                switch (response.getResultCode()) {

                    case SUCCESS:

                        mHelper.updateAccount(response.getParentID(), 1, account.getName(),
                                Integer.parseInt(response.getCoin()), response.getEmail());

                        mHelper.insertChildren(response.getChildren());

                    default:

                        sendEmptyMessage(REQUEST_UPDATE_DATA);

                        dismissLoading();

                        break;
                }
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {


                sendEmptyMessage(REQUEST_UPDATE_DATA);

                dismissLoading();
            }
        });

        addRequest(request);
    }

    private void updateBadge() {

        Account account = mHelper.getAccountInfo();

        int count = account.getNotice();

        if (count < 1) {

            mBadge.setVisibility(View.GONE);

        } else if (count > 0 && count < 10) {

            mBadge.setText(String.valueOf(count));

            mBadge.setVisibility(View.VISIBLE);

        } else {

            mBadge.setText("9+");

            mBadge.setVisibility(View.VISIBLE);
        }
    }

    private void getNewNotice() {

        Account account = mHelper.getAccountInfo();

        ParentModel model = new ParentModel();

        model.setParentId(account.getID());

        SimpleXmlRequest request = HttpHelper.getNewNotice(model, new Response.Listener<NewNoticeResult>() {
            @Override
            public void onResponse(NewNoticeResult response) {

                switch (response.getResultCode()) {

                    case SUCCESS:

                        List<Notice> notices = response.getNotices();

                        if (!response.getHasNewNotice().equals("1")) {

                            return;
                        }

                        if (notices == null || notices.size() < 1) {

                            return;
                        }

                        Notice notice = notices.get(0);

                        int id = -1, mid = -1;

                        try {

                            id = Integer.parseInt(notice.getNoticeId());

                        } catch (NumberFormatException e) {


                            return;
                        }

                        if (id < 1) {


                            return;
                        }

                        mid = STApplication.getInt(StaticValues.RECENT_NOTICE_ID, -1);

                        if (id > mid) {

                            STApplication.putInt(StaticValues.RECENT_NOTICE_ID, id);

                            MaterialDialog.Builder builder = new MaterialDialog.Builder(ListChildActivity.this);

                            String content = null;

                            try {

                                content = AndroidUtils.convertFromUTF8(notice.getContent()).replaceAll("n", "").replaceAll("\\\\", "\n");

                                Logger.d(content);

                            } catch (NullPointerException e) {

                                return;
                            }

                            builder.title(AndroidUtils.convertFromUTF8(notice.getTitle()))
                                    .content(content)
                                    .positiveText("확인").callback(new MaterialDialog.SimpleCallback() {
                                @Override
                                public void onPositive(MaterialDialog materialDialog) {

                                    materialDialog.dismiss();
                                }
                            }).build().show();
                        }

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

            switch (intent.getAction()) {

                case StaticValues.REGISTER_CHILD:
                case StaticValues.NEW_MESSAGE_ARRIVED:

                    getData();

                    break;
            }
        }
    }
}
