package kr.co.digitalanchor.studytime.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.igaworks.adbrix.IgawAdbrix;
import com.orhanobut.logger.Logger;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.chat.ParentChatActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.AllPackageResultForParent;
import kr.co.digitalanchor.studytime.model.CoinResult;
import kr.co.digitalanchor.studytime.model.ExceptionApp;
import kr.co.digitalanchor.studytime.model.GeneralResult;
import kr.co.digitalanchor.studytime.model.LoginModel;
import kr.co.digitalanchor.studytime.model.PackageElementForP;
import kr.co.digitalanchor.studytime.model.ParentOnOff;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-12.
 */
public class ControlChildExActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener, SlidingUpPanelLayout.PanelSlideListener,
        AdapterView.OnItemClickListener {

    final int REQUEST_ON_OFF = 50001;

    final int REQUEST_APP_LIST = 50002;

    final int REQUEST_EXCEPT_APP = 50003;

    SlidingUpPanelLayout mSlideLayout;

    StickyGridHeadersGridView mGridView;

    ImageView mPanelToggleButton;

    TextView mLabelPoint;

    ImageButton mButtonPoint;

    ImageButton mButtonMenu;

    ImageButton mButtonToggle;

    ImageButton mButtonChat;

    ImageButton mButtonUse;

    Button mButtonCloseGuide;

    Button mButtonConfirm;

    View mImgGuide;

    MenuPopup mMenu;

    DBHelper mHelper;

    Child mChild;

    String mChildID;

    List<PackageElementForP> packages;

    AppGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_child_ex);

        mHelper = new DBHelper(getApplicationContext());

        getData();

        initView();

        firstVisit();
    }


    private void initView() {

        mSlideLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
//        mSlideLayout.setTouchEnabled(false);
        mSlideLayout.setPanelSlideListener(this);

        mPanelToggleButton = (ImageView) findViewById(R.id.buttonToggle);
        mPanelToggleButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {

                    mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                } else if (mSlideLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {

                    mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);

                }
            }
        });

        mGridView = (StickyGridHeadersGridView) findViewById(R.id.gridView);
        mGridView.setOnItemClickListener(this);

        packages = new ArrayList<PackageElementForP>();

        mAdapter = new AppGridAdapter(getApplicationContext(), R.layout.layout_app_header,
                R.layout.layout_item_app, packages);

        mGridView.setAdapter(mAdapter);

        mGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                mSlideLayout.requestDisallowInterceptTouchEvent(true);

                return false;
            }
        });

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

        mButtonConfirm = (Button) findViewById(R.id.buttonConfirm);
        mButtonConfirm.setOnClickListener(this);

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

        mChild = mHelper.getChild(childId);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_ON_OFF:

                Account account = mHelper.getAccountInfo();

                if (mChild.getIsOFF() == 0) {

                    if (account.getCoin() > 0) {

                        requestOnOff();

                    } else {

                        Toast.makeText(getApplicationContext(), "코인이 부족합니다.",
                                Toast.LENGTH_SHORT).show();
                    }

                } else {

                    Intent intent = new Intent();
                    intent.setClass(getApplicationContext(), InputPwdActivity.class);

                    intent.putExtra("ChildID", mChild.getChildID());
                    intent.putExtra("Name", mChild.getName());

                    startActivity(intent);
                }

                break;

            case REQUEST_APP_LIST:

                requestAppList();

                break;

            case REQUEST_EXCEPT_APP:

                requestSetExceptionApp();

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

                sendEmptyMessage(REQUEST_ON_OFF);

                break;

            case R.id.buttonConfirm:

                sendEmptyMessage(REQUEST_EXCEPT_APP);

                break;

            default:

                break;
        }
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {

        Logger.d("slideOffset" + slideOffset);
    }

    @Override
    public void onPanelCollapsed(View panel) {

        mPanelToggleButton.setImageResource(R.drawable.icon_up_selector);
    }

    @Override
    public void onPanelExpanded(View panel) {

        sendEmptyMessage(REQUEST_APP_LIST);

        mPanelToggleButton.setImageResource(R.drawable.icon_down_selector);
    }

    @Override
    public void onPanelAnchored(View panel) {

    }

    @Override
    public void onPanelHidden(View panel) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        PackageElementForP item = packages.get(position);

        int exception = item.getExcepted_ex();

        if (exception == 0) {

            exception = 1;

        } else {

            exception = 0;
        }

        item.setExcepted_ex(exception);

        mAdapter.notifyDataSetChanged();
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

            mMenu.addNewNotice((account.getNotice() > 0) ? true : false);
        }

        if (mLabelPoint != null) {

            mLabelPoint.setText(String.valueOf(account.getCoin()));
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

        showLoading();

        IgawAdbrix.retention("onOff");

        final Account account = mHelper.getAccountInfo();

        ParentOnOff model = new ParentOnOff();

        model.setParentID(account.getID());
        model.setChildID(mChild.getChildID());
        model.setIsOff(mChild.getIsOFF() == 0 ? "1" : "0");
        model.setName(account.getName());
        model.setCoin(account.getCoin() - 1);

        SimpleXmlRequest request = HttpHelper.getParentOnOff(model,
                new Response.Listener<CoinResult>() {
                    @Override
                    public void onResponse(CoinResult response) {

                        Logger.d(response.toString());

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                if (mChild.getIsOFF() == 0) {

                                    mHelper.updateChildToggle(mChild.getChildID(), 1);

                                    mHelper.updateCoin(account.getID(), response.getCoin());

                                    mChild.setIsOFF(1);

                                } else {

                                    mHelper.updateChildToggle(mChild.getChildID(), 0);

                                    mChild.setIsOFF(0);

                                }

                                drawView();

                                toggleOnOff();

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

    private void firstVisit() {

        boolean isFirst = STApplication.getBoolean(StaticValues.IS_SHOW_GUIDE, true);

        if (isFirst) {

            STApplication.putBoolean(StaticValues.IS_SHOW_GUIDE, false);

            mImgGuide.setVisibility(View.VISIBLE);
        }
    }

    private void toggleOnOff() {

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

    private void requestAppList() {

        Account account = mHelper.getAccountInfo();

        LoginModel model = new LoginModel();

        model.setChildId(mChildID);
        model.setParentId(account.getID());

        SimpleXmlRequest request = HttpHelper.getPackageForParent(model,
                new Response.Listener<AllPackageResultForParent>() {

                    @Override
                    public void onResponse(AllPackageResultForParent response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                packages.clear();

                                List<PackageElementForP> list = response.getPackages();

                                for (PackageElementForP item : list) {

                                    item.setExcepted_ex(item.getExcepted());

                                    item.setName(AndroidUtils.convertFromUTF8(item.getName()));

                                    packages.add(item);
                                }

                                Collections.sort(packages, new Comparator<PackageElementForP>() {

                                    @Override
                                    public int compare(PackageElementForP lhs, PackageElementForP rhs) {

                                        return lhs.getName().compareTo(rhs.getName());
                                    }
                                });

                                Collections.sort(packages, new Comparator<PackageElementForP>() {
                                    @Override
                                    public int compare(PackageElementForP lhs, PackageElementForP rhs) {

                                        return rhs.getExcepted() - lhs.getExcepted();
                                    }
                                });

                                mAdapter.notifyDataSetChanged();

                                mGridView.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        mGridView.setSelection(0);
                                    }
                                });

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

    private void requestSetExceptionApp() {

        showLoading();

        Account account = mHelper.getAccountInfo();

        ExceptionApp model = new ExceptionApp();

        model.setParentId(account.getID());
        model.setChildId(mChildID);

        List<PackageElementForP> list = new ArrayList<>();

        for (PackageElementForP item : packages) {

            if (item.getExcepted() != item.getExcepted_ex()) {

                PackageElementForP itemClone = null;

                try {

                    itemClone = (PackageElementForP) item.clone();

                } catch (CloneNotSupportedException e) {

                    continue;
                }

                itemClone.setExcepted(item.getExcepted_ex());

                list.add(itemClone);
            }
        }

        if (list.size() <= 0) {

            dismissLoading();

            Toast.makeText(getApplicationContext(), "변경된 내용이 없습니다.", Toast.LENGTH_SHORT).show();

            return;
        }

        model.setPackages(list);

        SimpleXmlRequest request = HttpHelper.getSettingExceptionApp(model,
                new Response.Listener<GeneralResult>() {
                    @Override
                    public void onResponse(GeneralResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                dismissLoading();

                                mSlideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                                Toast.makeText(getApplicationContext(), "예외 앱이 적용되었습니다.",
                                        Toast.LENGTH_SHORT).show();

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

}
