package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thomas on 2015-06-25.
 */
@Root(name = "FAQResult")
public class FAQResult {

    @Element(name = "ResultCode", required = false)
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @ElementList(name = "FAQ", required = false, inline = true)
    ArrayList<FAQ> faqs;

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

    public ArrayList<FAQ> getFaqs() {
        return faqs;
    }

    public void setFaqs(ArrayList<FAQ> faqs) {
        this.faqs = faqs;
    }
}
