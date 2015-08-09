package kr.co.digitalanchor.studytime.gcm;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.SimpleXmlRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.List;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.chat.ChildChatActivity;
import kr.co.digitalanchor.studytime.chat.ParentChatActivity;
import kr.co.digitalanchor.studytime.control.ControlChildExActivity;
import kr.co.digitalanchor.studytime.control.ListChildActivity;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.ChatRead;
import kr.co.digitalanchor.studytime.model.ChatReadResult;
import kr.co.digitalanchor.studytime.model.ExceptionAppResult;
import kr.co.digitalanchor.studytime.model.LoginModel;
import kr.co.digitalanchor.studytime.model.PackageIDs;
import kr.co.digitalanchor.studytime.model.PackageModel;
import kr.co.digitalanchor.studytime.model.api.HttpHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.studytime.signup.BoardActivity;
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

        if (TextUtils.isEmpty(code)) {

            return;
        }

        Account account = mHelper.getAccountInfo();

        if (TextUtils.isEmpty(account.getID())) {

            return;
        }

        switch (code) {

            case "RE_REG":
            case "REG_CHILD":

                updateChildrenInfo(bundle);

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getNormalStart(account.getIsChild(), null));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            case "CHAT":

                requestNewMessage(bundle.getString("messageID"), bundle.getString("senderID"), bundle.getString("name"));

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getIntentNewMessage(bundle.getString("senderID"),
                                AndroidUtils.convertFromUTF8(bundle.getString("name")),
                                account.getIsChild()));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            case "CHAT_READ":

//                mHelper.updateChat(bundle.getString("messagePK"), bundle.getString("messageID"), 0, "");

                break;

            case "ONOFF":

                int isOff = Integer.parseInt(bundle.getString("isOff"));

                mHelper.updateOnOff(isOff);

                sendBroadcast(new Intent(StaticValues.ACTION_SERVICE_START));

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getNormalStart(account.getIsChild(), null));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            case "NOTICE":

                /*
                name : 보내는 사람 이름
                msg : 메시지 제목
                isGroup : 1
                time : 메시지 보내는 시간
                 */

                mHelper.addNoticeCount();

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getIntentNotice(account.getIsChild()));

                sendBroadcast(new Intent(StaticValues.NEW_NOTICE_ARRIVED));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            /**
             * 삭제 시도 (비번 틀림)
             */
            case "DELETE_TRY":

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getNormalStart(account.getIsChild(), null));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            /**
             * 삭제 성공
             */
            case "DELETE":

                // TODO : 어떻게 알려줄 것인가?

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getNormalStart(account.getIsChild(), null));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            case "BLOCK_APP_CHANGE":

                requestUpdateApp(bundle.getString("senderID"), bundle.getString("receiverID"));

                AndroidUtils.showNotification(STApplication.applicationContext, null,
                        bundle.getString("msg"), getNormalStart(account.getIsChild(), null));

                AndroidUtils.acquireCpuWakeLock(STApplication.applicationContext);

                break;

            default:

                break;
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void updateChildrenInfo(Bundle data) {

        if (data != null) {

            mHelper.insertChild(data.getString("senderID"), data.getString("name"));
        }

        sendBroadcast(new Intent(StaticValues.REGISTER_CHILD));
    }

    private PendingIntent getNormalStart(int isChild, Bundle bundle) {

        PendingIntent pIntent = null;

        Intent intent = null;

        switch (isChild) {

            case 0: // child

                intent = new Intent(STApplication.applicationContext, ChildChatActivity.class);

                if (bundle != null) {

                    intent.putExtras(bundle);
                }

                pIntent = PendingIntent.getActivity(STApplication.applicationContext, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                return pIntent;

            case 1: // parent

                intent = new Intent(STApplication.applicationContext, ListChildActivity.class);

                if (bundle != null) {

                    intent.putExtras(bundle);
                }

                pIntent = PendingIntent.getActivity(STApplication.applicationContext, 0,
                        intent, PendingIntent.FLAG_UPDATE_CURRENT);

                return pIntent;

            default:

                return null;
        }
    }

    private PendingIntent getIntentNotice(int isChild) {

        TaskStackBuilder stackBuilder = null;

        switch (isChild) {

            default:

                return null;

            case 0: // child

                Intent intent = new Intent(STApplication.applicationContext, ChildChatActivity.class);

                PendingIntent pIntent = PendingIntent.getActivity(STApplication.applicationContext,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                return pIntent;

            case 1: // parent

                Intent childrenList = new Intent(STApplication.applicationContext, ListChildActivity.class);

                Intent notice = new Intent(STApplication.applicationContext, BoardActivity.class);

                stackBuilder = TaskStackBuilder.create(STApplication.applicationContext);

                stackBuilder.addNextIntent(childrenList);
                stackBuilder.addNextIntent(notice);

                return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    private PendingIntent getIntentNewMessage(String senderId, String senderName, int isChild) {

        TaskStackBuilder stackBuilder = null;

        switch (isChild) {

            default:

                return null;

            case 0: // child

                Intent intent = new Intent(STApplication.applicationContext, ChildChatActivity.class);

                PendingIntent pIntent = PendingIntent.getActivity(STApplication.applicationContext,
                        0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                return pIntent;

            case 1: // parent

                Intent childrenList = new Intent(STApplication.applicationContext, ListChildActivity.class);

                Intent controlChild = new Intent(STApplication.applicationContext, ControlChildExActivity.class);

                controlChild.putExtra("ChildID", senderId);

                Intent chat = new Intent(STApplication.applicationContext, ParentChatActivity.class);
                chat.putExtra("ChildID", senderId);
                chat.putExtra("Name", senderName);

                stackBuilder = TaskStackBuilder.create(STApplication.applicationContext);

                stackBuilder.addNextIntent(childrenList);
                stackBuilder.addNextIntent(controlChild);
                stackBuilder.addNextIntent(chat);

                return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
        }
    }

    private void requestUpdateApp(String parentId, String childId) {

        LoginModel model = new LoginModel();

        model.setParentId(parentId);
        model.setChildId(childId);

        SimpleXmlRequest request = HttpHelper.getExceptionApp(model,
                new Response.Listener<ExceptionAppResult>() {
                    @Override
                    public void onResponse(ExceptionAppResult response) {

                        switch (response.getResultCode()) {

                            case SUCCESS:

                                List<PackageIDs> list = response.getPackages();

                                HashMap<String, Integer> map = new HashMap<String, Integer>();

                                if (list != null && list.size() > 0) {

                                    for (PackageIDs key : list) {

                                        map.put(key.getPackageId(), 1);
                                    }

                                }

                                List<PackageModel> packages = mHelper.getPackageListExcept();

                                for (PackageModel model : packages) {

                                    if (map.containsKey(model.getPackageId())) {

                                        model.setIsExceptionApp(1);

                                    } else {

                                        model.setIsExceptionApp(0);
                                    }
                                }

                                mHelper.setExceptPackages(packages);

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

                if (TextUtils.isEmpty(msg)) {
                    Logger.e(msg);
                }

                break;
        }
    }

    protected void handleError(VolleyError error) {

        Logger.e(error.toString());

    }
}
