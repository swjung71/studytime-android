package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "GetVersion")
public class GetVersion {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "Is_Android")
    int isAndroid;

    @Element(name = "Is_Child")
    int isChild;

    public GetVersion() {

        tag = "http://studytime.digitalanchor.co.kr/RequestDataModel";
    }

    public int getIsAndroid() {
        return isAndroid;
    }

    public void setIsAndroid(int isAndroid) {
        this.isAndroid = isAndroid;
    }

    public int getIsChild() {
        return isChild;
    }

    public void setIsChild(int isChild) {
        this.isChild = isChild;
    }
}
