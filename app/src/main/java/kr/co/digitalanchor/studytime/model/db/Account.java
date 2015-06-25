package kr.co.digitalanchor.studytime.model.db;

/**
 * 개정정보를 가져오는 파일 (자녀 및 부모 또는 선생님 정보)
 * studytime 자녀용이면 자녀, 부모용이면 부모 또는 선생님
 * 자녀이면 isChild 0, 부모면 1, 선생님이면 2
 * Created by Seung Wook Jung on 2015-06-22.
 */
public class Account {

    private String ID;
    private int isChild;
    private String name;
    private String password;
    private String coin;
    private String email;

    public Account() {

        isChild = -1;

        ID = null;

        name = "";

        password = "";

        coin = "0";

        email = "";
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int isChild() {
        return isChild;
    }

    public void setIsChild(int isChild) {
        this.isChild = isChild;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public void setEmail(String email) {

        this.email = email;
    }

    public String getEmail() {

        return email;
    }
}
