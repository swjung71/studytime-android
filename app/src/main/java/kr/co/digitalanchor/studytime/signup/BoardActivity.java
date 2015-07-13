package kr.co.digitalanchor.studytime.signup;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.Board;
import kr.co.digitalanchor.studytime.model.FAQ;
import kr.co.digitalanchor.studytime.model.FAQResult;
import kr.co.digitalanchor.studytime.model.Notice;
import kr.co.digitalanchor.studytime.model.NoticesResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.view.ViewHolder;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-25.
 */
public class BoardActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private final int REQUEST_BOARD_CONTENT = 50001;

    private TextView mLabelTitle;

    private ListView mList;

    private ArrayList<Content> mContents;

    private  BoardAdapter mAdapter;

private boolean flag = true;

    /**
     * 게시판 종류
     * 0 : 공지사항
     * 1 : FAQ
     */
    private int Type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        initView();

        getIntentData();

        sendEmptyMessage(REQUEST_BOARD_CONTENT);
    }

    @Override
    protected void onHandleMessage(Message msg) {

        switch (msg.what) {

            case REQUEST_BOARD_CONTENT:

                switch (Type) {

                    case 0:

                        requestNotice();

                        break;

                    case 1:

                        requestFAQ();

                        break;

                    default:

                }

                break;

            default:

                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Content content = mContents.get(position);

        content.checked = !content.checked;

        mAdapter.notifyDataSetChanged();
    }

    private void initView() {

        setContentView(R.layout.activity_board);

        mLabelTitle = (TextView) findViewById(R.id.header);

        mList = (ListView) findViewById(R.id.list);

        mContents = new ArrayList<>();

        mAdapter = new BoardAdapter(getApplicationContext(), 0, mContents);

        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(this);

    }

    private void getIntentData() {

        try {

            Bundle data = getIntent().getExtras();

            Type = data.getInt("option", 0);

        } catch (NullPointerException e) {

            Type = 0;
        }

        if (Type == 0 ) {

            mLabelTitle.setText("공지사항");

            DBHelper helper = new DBHelper(getApplicationContext());

            helper.initNoticeCount();

        } else if (Type == 1) {

            mLabelTitle.setText("FAQ");

        }
    }

    private void requestNotice() {

        showLoading();

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        Board model = new Board(account.getID());

        SimpleXmlRequest request = HttpHelper.getNotice(model,
                new Response.Listener<NoticesResult>() {
                    @Override
                    public void onResponse(NoticesResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                fillNotices(response);

                                dismissLoading();


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

    private void requestFAQ() {

        showLoading();

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        Board model = new Board(account.getID());

        SimpleXmlRequest request = HttpHelper.getFAQ(model,
                new Response.Listener<FAQResult>() {
                    @Override
                    public void onResponse(FAQResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                fillFAQ(response);

                                dismissLoading();

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

    private void fillFAQ(FAQResult result) {

        ArrayList<FAQ> list = result.getFaqs();

        if (list == null) {

            return;
        }

        mContents.clear();

        for (FAQ item : list) {

            Content content = new Content();

            content.title = AndroidUtils.convertFromUTF8(item.getTitle());

            content.content = AndroidUtils.convertFromUTF8(item.getContent());

            content.category = AndroidUtils.convertFromUTF8(item.getCategory());

            content.content = content.content.replaceAll("\\\\n", System.getProperty("line.separator"));

            mContents.add(content);
        }

        mAdapter.notifyDataSetChanged();
    }

    private void fillNotices(NoticesResult result) {

        ArrayList<Notice> list = result.getNotices();

        if (list == null) {

            return;
        }

        mContents.clear();

        for (Notice item : list) {

            Content content = new Content();

            content.title = AndroidUtils.convertFromUTF8(item.getTitle());

            content.content = AndroidUtils.convertFromUTF8(item.getContent());

            content.date = AndroidUtils.convertFromUTF8(item.getDate());

            content.content = content.content.replaceAll("\\\\n", System.getProperty("line.separator"));

            mContents.add(content);
        }

        mAdapter.notifyDataSetChanged();
    }

    class Content {

        String title;

        String content;

        boolean checked;

        String date;

        String category;

        public Content () {

            checked = false;
        }
    }

    class BoardAdapter extends ArrayAdapter<Content> {

        private  ArrayList<Content> contents;

        private int opendPosition = -1;

        public BoardAdapter(Context context, int resId, ArrayList<Content> contents) {

            super(context, resId, contents);

            this.contents = contents;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View view = convertView;

            if (view == null) {

                LayoutInflater inflater = LayoutInflater.from(getContext());

                view = inflater.inflate(R.layout.layout_board_item, null);
            }

            final Content item = contents.get(position);

            TextView title = ViewHolder.get(view, R.id.labelTitle);

            title.setText(item.title);

            TextView content = ViewHolder.get(view, R.id.content);

            content.setText(item.content);

            View container = ViewHolder.get(view, R.id.container);

            CheckBox arrow = ViewHolder.get(view, R.id.checkArrow);

            arrow.setChecked(item.checked);

            container.setVisibility(item.checked ? View.VISIBLE : View.GONE);

            arrow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {

                        if (opendPosition > -1) {

                           contents.get(opendPosition).checked = false;
                        }

                        item.checked = true;

                        opendPosition = position;

                    } else {

                        if (opendPosition == position) {

                            opendPosition = -1;
                        }

                        item.checked = false;

                    }

                    notifyDataSetChanged();
                }
            });

            //TODO date

            return view;
        }
    }

}
