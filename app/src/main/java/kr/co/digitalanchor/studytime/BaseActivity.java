package kr.co.digitalanchor.studytime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.igaworks.adpopcorn.style.AdPOPcornStyler;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.dialog.CustomProgressDialog;
import kr.co.digitalanchor.studytime.model.db.Account;


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

        STApplication.addActivity(this);

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

        STApplication.removeActivity(this);

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
            mLoading.setCancelable(false);
        }

        mLoading.show("");
    }

    protected void dismissLoading() {

        if (mLoading != null) {

            mLoading.dismiss("");
        }
    }

    protected void addRequest(SimpleXmlRequest request) {

        try {

            mQueue.add(request);

        } catch (Exception e) {

            Logger.e(e.toString());

            dismissLoading();
        }
    }

    protected void handleResultCode(int code, String msg) {

        dismissLoading();

        switch (code) {

            case 1000:

                msg = "서버 오류입니다. (1000)";

                break;

            case 1001:

                msg = "서버 오류입니다. (1001)";

                break;

            case 1002:

                msg = "서버 오류입니다. (1002)";

                break;

            case 1003:

                msg = "이메일 전송에 실패하였습니다. (1003)";

                break;

            case 1004:

                msg = "이메일 전송에 실패하였습니다. (1004)";

                break;

            case 1005:

                msg = "이메일이나 비밀번호를 정확히 입력하십시오. (1005)";

                break;

            case 1006:

                msg = "이메일을 입력하십시오. (1006)";

                break;

            case 1007:

                msg = "중복 이메일이니 다른 이메일을 이용하십시오. (1007)";

                break;

            case 1008:

                msg = "비밀번호가 잘못되었습니다. (1008)";

                break;

            case 1009:

                msg = "서버 오류입니다. (1009)";

                break;

            case 1010:

                msg = "서버 오류입니다. (1010)";

                break;

            case 1020:

                msg = "등록할 수 있는 자녀의 수를 초과하였습니다. (1020)";

                break;

            default:

                msg = "알수없는 오류입니다.";

                break;
        }

        Toast.makeText(getApplicationContext(),msg, Toast.LENGTH_SHORT).show();
    }

    protected void handleError(VolleyError error) {

        dismissLoading();

        if (error instanceof ServerError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof TimeoutError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void startMonitorService() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));
            }
        }, 1000);

    }

    protected void showOfferWall() {

        String id = STApplication.getString(StaticValues.AD_ID);

        if (TextUtils.isEmpty(id)) {

            return;
        }

        IgawCommon.setUserId(id);

        Logger.d("ad id" + id);

        AdPOPcornStyler.themeStyle.rewardThemeColor = Color.parseColor("#A65EA8");
        AdPOPcornStyler.themeStyle.themeColor = Color.parseColor("#A65EA8");
        AdPOPcornStyler.themeStyle.rewardCheckThemeColor = Color.parseColor("#A65EA8");
        AdPOPcornStyler.offerwall.Title = "하트얻기";

        IgawAdpopcorn.openOfferWall(this);

    }

    protected void sendEmail() {

        IgawAdbrix.retention("oneToOne");

        Intent intent = new Intent(Intent.ACTION_SEND);

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        String text = "이름:\n전화번호:\n" + account.getEmail() + "이 보낸메일 \n";

        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@digitalanchor.co.kr"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "1:1 상담");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }
}
