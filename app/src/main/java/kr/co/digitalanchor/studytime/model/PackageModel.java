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
     * 삭제 또는 업데이트 시에만 있음
     */
    @Element(name = "PackageID", required = false)
    String packageId;

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

    int changed;

    /**
     * add 0, delete 1, update 2
     */
    @Element(name = "State", required = false)
    int state;

    public PackageModel() {

        this.hash = null;
        this.packageName = null;
        this.labelName = null;
        this.packageId = null;
        this.packageVersion = null;
        this.timestamp = null;
        this.isExceptionApp = -1;
        this.isDefaultApp = -1;
        this.hasIcon = -1;
        this.hasIconDB = -1;
        this.iconHash = null;
        this.state = -1;
        this.changed = -1;
    }

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

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
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

    public int getChanged() {
        return changed;
    }

    public void setChanged(int changed) {
        this.changed = changed;
    }
}
