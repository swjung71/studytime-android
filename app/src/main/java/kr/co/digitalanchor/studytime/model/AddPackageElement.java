package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-31.
 */
@Root(name = "Packages")
public class AddPackageElement {

    @Element(name = "Hash", required = false, data = true)
    String hash;

    @Element(name = "PackageName", required = false, data = true)
    String packageName;

    @Element(name = "LabelName", required = false, data = true)
    String labelName;

    @Element(name = "PackageVersion", required = false, data = true)
    String packageVersion;

    @Element(name = "Timestamp", required = false, data = true)
    String timestamp;

    @Element(name = "Is_ExceptionApp", required = false)
    int isExceptionApp;

    @Element(name = "Is_DefaultApp", required = false)
    int isDefaultApp;

    @Element(name = "HasIcon", required = false)
    int hasIcon;

    public AddPackageElement() {

        isExceptionApp = -1;

        isDefaultApp = -1;

        hasIcon = -1;
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
}
