package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thomas on 2015-07-31.
 */
@Root(name = "AllPackage")
public class AddPackageModel {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "ParentID", required = false, data = true)
    String parentId;

    @Element(name = "ChildID", required = false, data = true)
    String childId;

    @ElementList(name = "Package", required = false, inline = true)
    List<AddPackageElement> packages;

    public AddPackageModel() {

        this.tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
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

    public List<AddPackageElement> getPackages() {
        return packages;
    }

    public void setPackages(List<AddPackageElement> packages) {
        this.packages = packages;
    }
}
