package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Seung Wook Jung on 2015-08-03.
 */
@Root(name = "GetAdultDB")
public class GetAdultDB {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ChildID", required = false)
    String childID;

    @Element(name = "Date", required = false)
    String date;

    public GetAdultDB() {

        tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
    }

    public String getMChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getDate() {  return date; }

    public void setDate(String date) {
        this.date = date;
    }
}
