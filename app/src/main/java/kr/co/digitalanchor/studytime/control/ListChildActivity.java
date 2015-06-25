package kr.co.digitalanchor.studytime.control;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.igaworks.IgawCommon;
import com.igaworks.adpopcorn.IgawAdpopcorn;

import java.util.ArrayList;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.signup.ModPrivacyActivity;
import kr.co.digitalanchor.studytime.signup.WithdrawActivity;

/**
 * Created by Thomas on 2015-06-19.
 */
public class ListChildActivity extends BaseActivity implements View.OnClickListener,
        MenuPopup.OnClickMenuItemListener, AdapterView.OnItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_child_list);

        initView();

        mHelper = new DBHelper(getApplicationContext());
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

        drawView();

        getData();
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
    public void onClickWithdraw() {

        showWithDraw();

    }

    @Override
    public void onClickLogOut() {

        mHelper.clearAll();

        Toast.makeText(getApplicationContext(), "Log out", Toast.LENGTH_SHORT).show();
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
        intent.putExtra("Name",  child.getName());

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

        String [] tos = {account.getEmail()};

        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, tos);
        intent.putExtra(Intent.EXTRA_SUBJECT, "1:1 상담");

        startActivity(intent);
    }

    private void sendLink() {

        try {

            Intent intent = new Intent(Intent.ACTION_VIEW);

            intent.putExtra("addres", "");
            intent.putExtra("sms_body", "https://play.google.com/store/apps/details?id=kr.co.studytime");
            intent.setType("vnd.android-dir/mms-sms");

            startActivity(intent);

        } catch (Exception e) {

            Toast.makeText(getApplicationContext(),
                    "SMS fail, please try again later!", Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void getData() {

        mChildren = mHelper.getChild();

        mList.setAdapter(makeAdapter());
    }
}
