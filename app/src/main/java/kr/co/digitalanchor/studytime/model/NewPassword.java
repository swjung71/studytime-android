package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-28.
 */
@Root(name = "NewPass")
public class NewPassword {

    @Attribute(name = "xmlns")
    String tag;

    @Element(name = "Email")
    String email;

    public NewPassword() {
        this.tag = "http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
