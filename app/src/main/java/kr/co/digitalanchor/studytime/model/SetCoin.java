package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "SetCoin")
public class SetCoin {

    @Attribute(name = "xmlns")
    final String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "Coin")
    String coin;

    public SetCoin() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public SetCoin(String parentID, String coin) {

        this();

        this.parentID = parentID;

        this.coin = coin;
    }

    public String getParentID() {
        return parentID;
    }

    public void setParentID(String parentID) {
        this.parentID = parentID;
    }

    public String getCoin() {

        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }
}
