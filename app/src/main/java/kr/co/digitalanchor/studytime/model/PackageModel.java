package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-29.
 */
@Root(name = "Package")
public class PackageModel {

    @Element(name = "Hash", required = false)
    String hash;

    @Element(name = "PackageName", required = false)
    String packageName;

    @Element(name = "LabelName", required = false)
    String labelName;

    /**
     * ���� �Ǵ� ������Ʈ �ÿ��� ����
     */
    @Element(name = "PackageID", required = false)
    int packageId;

    @Element(name = "PackageVersion", required = false)
    String packageVersion;

    @Element(name = "Timestamp", required = false)
    String timestamp;

    @Element(name = "Is_ExceptionApp", required = false)
    int isExceptionApp;

    @Element(name = "Is_DefaultApp", required = false)
    int isDefaultApp;

    @Element(name = "HasIcon", required = false)
    int hasIcon;

    @Element(name = "Has_Icon_In_DB", required = false)
    int hasIconDB;

    @Element(name = "IconHash", required = false)
    String iconHash;

    /**
     * �߰� �� 0, ���� �� 1, ������Ʈ �� 2
     */
    @Element(name = "State", required = false)
    int state;

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(String packageVersion) {
        this.packageVersion = packageVersion;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getIsExceptionApp() {
        return isExceptionApp;
    }

    public void setIsExceptionApp(int isExceptionApp) {
        this.isExceptionApp = isExceptionApp;
    }

    public int getIsDefaultApp() {
        return isDefaultApp;
    }

    public void setIsDefaultApp(int isDefaultApp) {
        this.isDefaultApp = isDefaultApp;
    }

    public int getHasIcon() {
        return hasIcon;
    }

    public void setHasIcon(int hasIcon) {
        this.hasIcon = hasIcon;
    }

    public int getPackageId() {
        return packageId;
    }

    public void setPackageId(int packageId) {
        this.packageId = packageId;
    }

    public int getHasIconDB() {
        return hasIconDB;
    }

    public void setHasIconDB(int hasIconDB) {
        this.hasIconDB = hasIconDB;
    }

    public String getIconHash() {
        return iconHash;
    }

    public void setIconHash(String iconHash) {
        this.iconHash = iconHash;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
