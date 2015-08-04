package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-08-04.
 */
@Root(name = "Icon")
public class IconModel {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "Hash")
    String hash;

    @Element(name = "IconHash")
    String iconHash;

    @Element(name = "PackageVersion")
    String packageVersion;

    @Element(name = "IsUpdate")
    int isUpdate;

    public IconModel() {
        this.tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
    }

    public String getIconHash() {
        return iconHash;
    }

    public void setIconHash(String iconHash) {
        this.iconHash = iconHash;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public int getIsUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(int isUpdate) {
        this.isUpdate = isUpdate;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
