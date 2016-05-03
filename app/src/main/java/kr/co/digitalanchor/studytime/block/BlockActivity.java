package kr.co.digitalanchor.studytime.block;

import android.os.Bundle;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-24.
 * LG폰의 guest모드에서 차단 화면이 나오지 않아서 Toast로 변경하였음
 * 현재는 사용하지 않고 있음
 */
public class BlockActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_block);

    }

    @Override
    protected void onPause() {
        super.onPause();

        finish();
    }

    @Override
    public void onBackPressed() {

    }
}
