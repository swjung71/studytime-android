package kr.co.digitalanchor.studytime.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.orhanobut.logger.Logger;

import kr.co.digitalanchor.studytime.STApplication;
import kr.co.digitalanchor.studytime.StaticValues;
import kr.co.digitalanchor.studytime.database.DBHelper;
import kr.co.digitalanchor.studytime.model.db.Account;
import kr.co.digitalanchor.utils.AndroidUtils;

/**
 * Created by Thomas on 2015-06-16.
 */
public class GCMIntentService extends IntentService {

    DBHelper mHelper;

    public GCMIntentService() {

        super("GCMIntentService");

        mHelper = new DBHelper(STApplication.applicationContext);

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

            case "REQ_REG":
            case "REG_CHILD":


//                mHelper.insertChild(bundle.getString("senderID"), 0, bundle.getString("name"));

                break;

            case "CHAT":


//                mHelper.insertChat(0, bundle.getString("senderID"),
//                        bundle.getString("receiverID"), bundle.getString("name"),
//                        bundle.getString("msg"), bundle.getString("time"), 1, 0, 0);

//                (String messageId, String roomId, String guestId, String guestName,
//                    String senderId, String unreadCount, int isGroup, int isChild) {

//                Bundle[{time=2015-06-27 22:59:55, collapse_key=collapseKey1435413595021, isGroup=0, recieverID=11,
//                        name=유정효, senderID=6, android.support.content.wakelockid=56,
//                        from=245104271755, code=CHAT, msg=어어러ㅓㄹ, isChild=1}]

                Account account = mHelper.getAccountInfo();

                mHelper.insertMessageFromGCM(account.getParentId(), bundle.getString("senderID"), bundle.getString("senderID"),
                        bundle.getString("name"), "0", 0, 0, bundle.getString("msg"), AndroidUtils.getCurrentTime4Chat());

                sendBroadcast(new Intent(StaticValues.NEW_MESSAGE_ARRIVED));

                break;

            case "CHAT_READ":

//                mHelper.updateChat(bundle.getString("messagePK"), bundle.getString("messageID"), 0, "");

                break;

            case "ONOFF":

                mHelper.insertOnOff(Integer.parseInt(bundle.getString("isOff")));

                break;

            default:

                break;
        }

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

}
