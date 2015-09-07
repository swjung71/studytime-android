package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-09-07.
 */
@Root(name = "GPSRequest", strict = false)
public class GPSRequest {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID", required = false)
    String parentId;

    @Element(name = "ChildID", required = false)
    String childId;

    @Element(name = "RequestID", required = false)
    String requestId;

    @Element(name = "Timestamp", required = false)
    String timeStamp;

    public GPSRequest() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
