package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.widget.ListView;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-08-24.
 */
public class NotificationActivity extends BaseActivity {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        listView = (ListView) findViewById(R.id.list);
    }
}
