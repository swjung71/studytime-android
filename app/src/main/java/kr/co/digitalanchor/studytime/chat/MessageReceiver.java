package kr.co.digitalanchor.studytime.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.orhanobut.logger.Logger;

import static kr.co.digitalanchor.studytime.StaticValues.NEW_MESSAGE_ARRIVED;

/**
 * Created by Thomas on 2015-06-27.
 */
public class MessageReceiver extends BroadcastReceiver {

    interface OnMessageListener {

        void onReceiveMessage();
    }

    OnMessageListener messageListener;

    public void setMessageListener(OnMessageListener listener) {

        messageListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(NEW_MESSAGE_ARRIVED)) {

            Logger.d("receive Message");

            if (messageListener != null) {

                messageListener.onReceiveMessage();
            }
        }
    }
}
