package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-09-08.
 */
@Root(name = "GPSResult", strict = false)
public class GPSResultParent {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @Element(name = "GPSResultCode", required = false)
    String GPSResultCode;

    @Element(name = "ParentID", required = false)
    String parentId;

    @Element(name = "ChildID", required = false)
    String childId;

    @Element(name = "Timestamp", required = false)
    String timestamp;

    @Element(name = "RequestID", required = false)
    String requestId;

    @Element(name = "Title", required = false)
    String address;

    @Element(name = "Latitude", required = false)
    String latitude;

    @Element(name = "Longitude", required = false)
    String longitude;

    @Element(name = "Accuracy", required = false)
    String accuracy;

    @Element(name = "Type", required = false)
    String type;

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

    public String getGPSResultCode() {
        return GPSResultCode;
    }

    public void setGPSResultCode(String GPSResultCode) {
        this.GPSResultCode = GPSResultCode;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
