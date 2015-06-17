package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "Parent")
public class ParentPrivacyInfo {

    @Attribute(name = "xmlns")
    final String tag;

    @Element(name = "ParentID")
    String parentID;

    public ParentPrivacyInfo() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public ParentPrivacyInfo(String parentID) {

        this();

        this.parentID = parentID;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }
}
