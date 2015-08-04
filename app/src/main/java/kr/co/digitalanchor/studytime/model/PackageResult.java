package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-31.
 */
@Root(name = "Package")
public class PackageResult {

    @Element(name = "PackageName", required = false)
    String packageName;

    @Element(name = "PackageID", required = false)
    String packageId;

    @Element(name = "Hash", required = false)
    String hash;

    @Element(name = "Has_Icon_In_DB", required = false)
    int doExistInDB;

    @Element(name = "IconHash", required = false)
    String iconHash;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getDoExistInDB() {
        return doExistInDB;
    }

    public void setDoExistInDB(int doExistInDB) {
        this.doExistInDB = doExistInDB;
    }

    public String getIconHash() {
        return iconHash;
    }

    public void setIconHash(String iconHash) {
        this.iconHash = iconHash;
    }
}
