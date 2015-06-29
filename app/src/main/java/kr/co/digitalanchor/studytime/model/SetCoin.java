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
    String tag;

    @Element(name = "ParentID")
    String parentID;

    @Element(name = "Coin")
    int coin;

    public SetCoin() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public SetCoin(String parentID, int coin) {

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

    public int getCoin() {

        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }
}
