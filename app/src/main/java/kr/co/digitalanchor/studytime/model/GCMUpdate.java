package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-07-28.
 */
@Root(name = "GCMUpdate")
public class GCMUpdate {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "IsChild", required = false)
    int isChild;

    @Element(name = "GCM", required = false)
    String GCM;

    @Element(name = "ID", required = false)
    String id;

    @Element(name = "Version", required = false)
    String version;

    public GCMUpdate() {

        tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
    }

    public int getIsChild() {
        return isChild;
    }

    public void setIsChild(int isChild) {
        this.isChild = isChild;
    }

    public String getGCM() {
        return GCM;
    }

    public void setGCM(String GCM) {
        this.GCM = GCM;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
