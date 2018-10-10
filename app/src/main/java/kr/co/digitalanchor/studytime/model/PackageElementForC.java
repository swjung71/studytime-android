package kr.co.digitalanchor.studytime.model;

import android.graphics.drawable.Drawable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-08-03.
 */
public class PackageElementForC implements Cloneable {

    String packageName;

    String name;

    String id;

    Drawable iconUrl;

    int excepted;

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

    public Drawable getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(Drawable iconUrl) {
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

    public String getPackageName(){
        return this.packageName;
    }

    public void setPackageName(String packageName){
        this.packageName = packageName;
    }
}
