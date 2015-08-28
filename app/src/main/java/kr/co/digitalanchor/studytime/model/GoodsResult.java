package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thomas on 2015-08-27.
 */
@Root(name = "GoodsResult", strict = false)
public class GoodsResult {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @ElementList(name = "Good", inline = true, required = false)
    List<Item> items;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
