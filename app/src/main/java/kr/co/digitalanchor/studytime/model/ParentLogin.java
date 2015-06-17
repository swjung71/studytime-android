package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ParentLogin")
public class ParentLogin {


    @Attribute(name = "xmlns")
    final String tag;

    @Element(name = "Email")
    String email;

    @Element(name = "Password")
    String password;

    public ParentLogin() {

        tag = "http://studytime.digitalanchor.co.kr/ParentRequstDataModel";
    }

    public ParentLogin(String email, String password) {

        this();

        this.email = email;

        this.password = password;

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
