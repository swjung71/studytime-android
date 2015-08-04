package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-08-03.
 */
@Root(name = "Packages", strict = false)
public class PackageElementForP implements Cloneable {

    @Element(name = "LabelName", required = false)
    String name;

    @Element(name = "PackageID", required = false)
    String id;

    @Element(name = "IconPath", required = false)
    String iconUrl;

    @Element(name = "Is_ExceptionApp", required = false)
    int excepted;

    @Element(name = "Is_Exception", required = false)
    int excepted_ex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getExcepted() {
        return excepted;
    }

    public void setExcepted(int excepted) {
        this.excepted = excepted;
    }

    public int getExcepted_ex() {
        return excepted_ex;
    }

    public void setExcepted_ex(int excepted_ex) {
        this.excepted_ex = excepted_ex;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        return super.clone();

    }
}
