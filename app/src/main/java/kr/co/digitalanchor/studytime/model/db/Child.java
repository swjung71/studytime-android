package kr.co.digitalanchor.studytime.model.db;

/**
 * study time 부모용인 경우, 자녀에 관한 정보
 * study time 자녀용인 경우, 부모 정보 (단 부모 이름은 없음)
 * Created by Seung Wook Jung on 2015-06-22.
 */
public class Child {

    private String childID;
    private int isChild;
    private String name;

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
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
}
