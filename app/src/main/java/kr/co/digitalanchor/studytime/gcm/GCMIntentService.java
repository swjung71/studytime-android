package kr.co.digitalanchor.studytime.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orhanobut.logger.Logger;

import java.util.List;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.ChatRead;
import kr.co.digitalanchor.studytime.model.ChatReadResult;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.model.db.Child;
import kr.co.digitalanchor.studytime.monitor.MonitorService;
import kr.co.digitalanchor.utils.AndroidUtils;

import static kr.co.digitalanchor.studytime.model.api.HttpHelper.SUCCESS;

/**
 * Created by Thomas on 2015-06-16.
 */
public class GCMIntentService extends IntentService {

    private final int REQUEST_READ_NEW_MESSAGE = 50001;


    DBHelper mHelper;

    RequestQueue mQueue;

    public GCMIntentService() {

        super("GCMIntentService");

        mHelper = new DBHelper(STApplication.applicationContext);

        mQueue = Volley.newRequestQueue(STApplication.applicationContext);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)
                || GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {

            GCMBroadcastReceiver.completeWakefulIntent(intent);

            return;
        }

        Bundle bundle = intent.getExtras();

        if (bundle.isEmpty()) {

            GCMBroadcastReceiver.completeWakefulIntent(intent);

            return;
        }

        if (!GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
            // Release the wake lock provided by the WakefulBroadcastReceiver.
            GCMBroadcastReceiver.completeWakefulIntent(intent);

            return;
        }

        Logger.d(bundle.toString());

        String code = bundle.getString("code");

        switch (code) {

            case "RE_REG":
            case "REG_CHILD":

                updateChildrenInfo(bundle);

                break;

            case "CHAT":


//                mHelper.insertChat(0, bundle.getString("senderID"),
//                        bundle.getString("receiverID"), bundle.getString("name"),
//                        bundle.getString("msg"), bundle.getString("time"), 1, 0, 0);

//                (String messageId, String roomId, String guestId, String guestName,
//                    String senderId, String unreadCount, int isGroup, int isChild) {

//                Bundle[{
//                          code=CHAT,
//                          senderID=6
//                          name=유정효,
//                          messageID=204,
//                          recieverID=11,
//                          time=2015-06-28 14:34:33, from=245104271755, msg=간나다어온,  collapse_key=collapseKey1435469673912,  isGroup=0,  isChild=1}]

                requestNewMessage(bundle.getString("messageID"), bundle.getString("senderID"), bundle.getString("name"));

                break;

            case "CHAT_READ":

//                mHelper.updateChat(bundle.getString("messagePK"), bundle.getString("messageID"), 0, "");

                break;

            case "ONOFF":

                int isOff = Integer.parseInt(bundle.getString("isOff"));

//                mHelper.insertOnOff(isOff);

                if (isOff == 0) {

                    stopService(new Intent(STApplication.applicationContext, MonitorService.class));

                } else {

                    startService(new Intent(STApplication.applicationContext, MonitorService.class));
                }

                break;

            case "NOTICE":

                /*
                name : 보내는 사람 이름
                msg : 메시지 제목
                isGroup : 1
                time : 메시지 보내는 시간
                 */

                break;

            default:

                break;
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void updateChildrenInfo(Bundle data) {

        mHelper.insertChild(data.getString("senderID"),  data.getString("name"));

        sendBroadcast(new Intent(StaticValues.REGISTER_CHILD));

    }

    private void requestNewMessage(String messageId, final String senderId, final String senderName) {

        if (TextUtils.isEmpty(messageId)) {

            return;
        }

        Account account = mHelper.getAccountInfo();

        ChatRead model = new ChatRead();

        model.setMessageID(messageId);
        model.setReaderID(account.getID());
        model.setIsChild(String.valueOf(account.getIsChild()));

        SimpleXmlRequest request = HttpHelper.getReadChat(model,
                new Response.Listener<ChatReadResult>() {
                    @Override
                    public void onResponse(ChatReadResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                Account account = mHelper.getAccountInfo();

                                mHelper.insertMessageFromGCM(response.getMessageId(), senderId, senderId,
                                        senderName, response.getCounter(), 0, 0,
                                        AndroidUtils.convertFromUTF8(response.getMessage()),
                                        response.getTime(), response.getMsgType());

                                sendBroadcast(new Intent(StaticValues.NEW_MESSAGE_ARRIVED));

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

    protected void addRequest(SimpleXmlRequest request) {

        try {

            mQueue.add(request);

        } catch (Exception e) {

            Logger.e(e.toString());

        }
    }

    protected void handleResultCode(int code, String msg) {

        switch (code) {

            default:

                Toast.makeText(getApplicationContext(),
                        TextUtils.isEmpty(msg) ? "알수 없는 오류" : msg,
                        Toast.LENGTH_SHORT).show();

                break;
        }
    }

    protected void handleError(VolleyError error) {

        Logger.e(error.toString());

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

}
