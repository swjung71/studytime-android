package kr.co.digitalanchor.studytime.signup;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.GetNotificationResult;
import kr.co.digitalanchor.studytime.model.NotificationModel;
import kr.co.digitalanchor.studytime.model.ParentModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.view.ViewHolder;
import kr.co.digitalanchor.utils.AndroidUtils;

/**
 * Created by Thomas on 2015-08-24.
 */
public class NotificationActivity extends BaseActivity {

    private final int REQUEST_ALARM_LIST = 50001;

    ListView listView;

    List<NotificationModel> list;

    AlarmAdapter alarmAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        listView = (ListView) findViewById(R.id.list);

        list = new ArrayList<>();

        alarmAdapter = new AlarmAdapter(getApplicationContext(), 0, list);

        listView.setAdapter(alarmAdapter);

        sendEmptyMessage(REQUEST_ALARM_LIST);
    }

    @Override
    protected void onHandleMessage(Message msg) {
        super.onHandleMessage(msg);

        switch (msg.what) {

            case REQUEST_ALARM_LIST:

                getAlarmList();

                break;

        }
    }

    public void getAlarmList() {

        showLoading();

        ParentModel model = new ParentModel();

        DBHelper dbHelper = new DBHelper(getApplicationContext());

        Account account = dbHelper.getAccountInfo();

        model.setParentId(account.getID());

        final SimpleXmlRequest request = HttpHelper.getAlarmList(model,
                new Response.Listener<GetNotificationResult>() {
                    @Override
                    public void onResponse(GetNotificationResult response) {

                        switch (response.getResultCode()) {

                            case HttpHelper.SUCCESS:

                                dismissLoading();

                                List<NotificationModel> items = response.getNotificationModels();

                                for (NotificationModel item : items) {

                                    item.setMsg(AndroidUtils.convertFromUTF8(item.getMsg()));

                                    list.add(item);
                                }

                                alarmAdapter.notifyDataSetChanged();

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

    class AlarmAdapter extends ArrayAdapter<NotificationModel> {

        private Context context;

        private List<NotificationModel> items;

        public AlarmAdapter(Context context, int resource, List<NotificationModel> items) {

            super(context, resource, items);

            this.context = context;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {

                LayoutInflater inflater = LayoutInflater.from(context);

                convertView = inflater.inflate(R.layout.layout_notification_item, null);
            }

            NotificationModel model = items.get(position);

            TextView icon = ViewHolder.get(convertView, R.id.icon);
            TextView content = ViewHolder.get(convertView, R.id.noti_content);

            content.setText(model.getMsg());

            Logger.d(model.getMsg() + " " + model.getCode() + " " + position);

            switch (model.getCode()) {

                case "CHAT":

                    icon.setBackgroundResource(R.drawable.icon_noti3);
                    icon.setText("대화");

                    break;

                case "ONOFF":

                    icon.setBackgroundResource(R.drawable.icon_noti3);
                    icon.setText("제어");

                    break;

                case "NOTICE":

                    icon.setBackgroundResource(R.drawable.icon_noti1);
                    icon.setText("공지");

                    break;

                case "REG_CHILD":
                case "RE_REG":

                    icon.setBackgroundResource(R.drawable.icon_noti3);
                    icon.setText("등록");

                    break;

                case "DELETE_TRY":
                case "DELETE":

                    icon.setBackgroundResource(R.drawable.icon_noti2);
                    icon.setText("삭제");

                    break;
            }

            return convertView;
        }
    }
}
