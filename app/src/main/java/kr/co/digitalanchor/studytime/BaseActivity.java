package kr.co.digitalanchor.studytime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.igaworks.IgawCommon;
import com.igaworks.adbrix.IgawAdbrix;
import com.igaworks.adpopcorn.IgawAdpopcorn;
import com.orhanobut.logger.Logger;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.dialog.CustomProgressDialog;
import kr.co.digitalanchor.studytime.model.db.Account;


/**
 * Created by Thomas on 2015-06-10.
 */
public class BaseActivity extends FragmentActivity {

    protected final int INPUT_ADD_INFO = 20001;

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
    protected void onStart() {
        super.onStart();


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
    protected void onStop() {

        super.onStop();

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

    public void sendEmptyMessage(int what, long delay) {

        if (mHandler != null) {

            mHandler.sendEmptyMessageDelayed(what, delay);
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

    protected void showLoading(String title) {

        if (mLoading == null) {

            mLoading = new CustomProgressDialog(this);
            mLoading.setCancelable(false);
            mLoading.setTitle(title);
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
            case 1001:
            case 1002:

                msg = "서버 오류입니다.";

                break;

            case 1003:

                msg = "이메일 전송에 실패하였습니다.";

                break;

            case 1004:

                msg = "이메일 전송에 실패하였습니다.";

                break;

            case 1005:

                msg = "이메일이나 비밀번호를 정확히 입력하십시오.";

                break;

            case 1006:

                msg = "이메일을 입력하십시오.";

                break;

            case 1007:

                msg = "중복 이메일이니 다른 이메일을 이용하십시오.";

                break;

            case 1008:

                msg = "비밀번호가 잘못되었습니다.";

                break;

            case 1009:

                msg = "서버 오류입니다.";

                break;

            case 1010:

                msg = "서버 오류입니다.";

                break;

            case 1020:

                msg = "등록할 수 있는 자녀의 수를 초과하였습니다.";

                break;

            case 1024:

                msg = "등록된 이메일이 아닙니다.";

                break;

            case 1028:

                msg = "비밀번호를 입력하세요.";

                break;

            case 1029:

                msg = "사용기간이 만료되었습니다.";

                break;

            case 1030:

                msg = "이미 등록된 기기입니다.";

                break;

            default:

                msg = "알수없는 오류입니다.";

                break;
        }

        Logger.e(msg);

        Toast.makeText(getApplicationContext(), msg + " (" + code + ")", Toast.LENGTH_SHORT).show();
    }

    protected void handleError(VolleyError error) {

        dismissLoading();

        Logger.e(error.toString());

        if (error instanceof ServerError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof TimeoutError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof ParseError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof NetworkError) {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

        } else if (error instanceof NoConnectionError) {

            MaterialDialog.Builder builder = new MaterialDialog.Builder(this);

            builder.title("알림").content("네트워크가 꺼져있습니다. 네트워크 상태를 확인하시고 앱을 다시 시작해주세요.")
                    .positiveText("확인")
                    .callback(new MaterialDialog.SimpleCallback() {
                        @Override
                        public void onPositive(MaterialDialog materialDialog) {

                            STApplication.stopAllActivity();

                            materialDialog.dismiss();
                        }
                    }).build().show();

        } else {

            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    protected void startMonitorService() {


        sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

    }

    @Deprecated
    protected void showOfferWall() {

        String id = STApplication.getString(StaticValues.AD_ID);

        if (TextUtils.isEmpty(id)) {

            return;
        }

        IgawCommon.setUserId(id);

        Logger.d("ad id" + id);


        IgawAdpopcorn.openOfferWall(this);

    }

    protected void sendEmail() {

        IgawAdbrix.retention("oneToOne");

        Intent intent = new Intent(Intent.ACTION_SEND);

        DBHelper helper = new DBHelper(getApplicationContext());
        Account account = helper.getAccountInfo();

        String text = "이름:\n전화번호:\n" + account.getEmail() + "이 보낸메일 \n";

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@digitalanchor.co.kr"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "1:1 상담");
        intent.putExtra(Intent.EXTRA_TEXT, text);

        startActivity(intent);
    }
}
