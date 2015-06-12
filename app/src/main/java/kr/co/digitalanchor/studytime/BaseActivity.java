package kr.co.digitalanchor.studytime;

import android.app.Activity;

import com.igaworks.IgawCommon;

/**
 * Created by Thomas on 2015-06-10.
 */
public class BaseActivity extends Activity {

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
}
