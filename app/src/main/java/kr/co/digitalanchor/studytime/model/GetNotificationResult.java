package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thomas on 2015-08-25.
 */
@Root(name = "GetNotificationResult", strict = false)
public class GetNotificationResult {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;


    @ElementList(name = "Notification", required = false, inline = true)
    ArrayList<NotificationModel> notificationModels;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public ArrayList<NotificationModel> getNotificationModels() {
        return notificationModels;
    }

    public void setNotificationModels(ArrayList<NotificationModel> notificationModels) {
        this.notificationModels = notificationModels;
    }
}
