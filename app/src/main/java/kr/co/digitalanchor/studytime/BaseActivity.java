package kr.co.digitalanchor.studytime;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.igaworks.IgawCommon;


/**
 * Created by Thomas on 2015-06-10.
 */
public class BaseActivity extends Activity {

    private final long INTERVAL = 1000;

    private long draft = 0;

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            onHandleMessage(msg);
        }
    };


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

    protected void onHandleMessage(Message msg) {


    }

    public Handler getHandler() {
        return mHandler;
    }

    /**
     * 짧은 더블 클릭 방지
     *
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
}
