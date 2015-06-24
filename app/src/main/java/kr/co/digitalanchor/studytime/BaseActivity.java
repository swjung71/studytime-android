package kr.co.digitalanchor.studytime;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.igaworks.IgawCommon;

import kr.co.digitalanchor.studytime.dialog.CustomProgressDialog;


/**
 * Created by Thomas on 2015-06-10.
 */
public class BaseActivity extends Activity {

    private final long INTERVAL = 1000;

    private long draft = 0;

    protected RequestQueue mQueue;

    protected CustomProgressDialog mLoading;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            onHandleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    protected void onResume() {

        super.onResume();

        IgawCommon.startSession(this);
    }

    @Override
    protected void onPause() {

        super.onPause();

        IgawCommon.endSession();
    }

    @Override
    protected void onDestroy() {

        if (mLoading != null) {

            if (mLoading.isShowing()) {

                mLoading.dismiss("");
            }

            mLoading = null;
        }

        super.onDestroy();
    }

    protected void onHandleMessage(Message msg) {


    }

    public Handler getHandler() {
        return mHandler;
    }

    public void sendEmptyMessage(int what) {

        if (mHandler != null) {

            mHandler.sendEmptyMessage(what);
        }
    }

    public void sendMessage(int what, Bundle data) {

        if (mHandler != null && data != null) {

            Message msg = new Message();

            msg.what = what;

            msg.setData(data);

            mHandler.sendMessage(msg);
        }
    }


    /**
     * 짧은 더블 클릭 방지
     */
    public boolean isDuplicateRuns() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - draft;

        if (0 <= intervalTime && INTERVAL >= intervalTime) {

            return true;

        } else {

            draft = tempTime;

            return false;
        }
    }

    protected void showLoading() {

        if (mLoading == null) {

            mLoading = new CustomProgressDialog(this);
        }

        mLoading.show("");
    }

    protected void dismissLoading() {

        if (mLoading != null && mLoading.isShowing()) {

            mLoading.dismiss("");
        }
    }
}
