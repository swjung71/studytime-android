package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "Parent")
public class Board {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID")
    String parentId;

    public Board() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public Board(String id) {

        this();

        parentId = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
