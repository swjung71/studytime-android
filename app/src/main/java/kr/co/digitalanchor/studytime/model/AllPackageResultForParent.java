package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thomas on 2015-08-03.
 */
@Root(name = "AllPackageResultForParent", strict = false)
public class AllPackageResultForParent {

    @Element(name = "ParentID", required = false)
    String parentId;

    @Element(name = "ChildID", required = false)
    String childId;

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @ElementList(name = "Packages", inline = true, required = false)
    List<PackageElementForP> packages;

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

    public List<PackageElementForP> getPackages() {
        return packages;
    }

    public void setPackages(List<PackageElementForP> packages) {
        this.packages = packages;
    }
}
