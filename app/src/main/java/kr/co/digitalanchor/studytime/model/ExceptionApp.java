package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thomas on 2015-08-03.
 */
@Root(name = "ExceptionApp", strict = false)
public class ExceptionApp {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID", required = false)
    String parentId;

    @Element(name = "ChildID", required = false)
    String childId;

    @ElementList(name = "Packages", required = false, inline = true)
    List<PackageElementForP> packages;

    public ExceptionApp() {
        this.tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";

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

    public List<PackageElementForP> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageElementForP> packages) {
        this.packages = packages;
    }
}
