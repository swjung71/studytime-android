package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-06-17.
 */
@Root(name = "ParentLogin")
public class ParentLogin {

    @Attribute(name = "xmlns", required = false)
    String tag;

    @Element(name = "Email", required = false)
    String email;

    @Element(name = "Password", required = false)
    String password;

    public ParentLogin() {

        tag ="http://studytime.digitalanchor.co.kr/ParentRequestDataModel";
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
