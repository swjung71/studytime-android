package kr.co.digitalanchor.studytime.model;

import android.os.Parcel;
import android.os.Parcelable;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

/**
 * Created by Thomas on 2015-10-22.
 */
@Root(name = "data", strict = false)
public class NaverUserInfo implements Parcelable {

  @Path("result/")
  @Element(name = "resultcode", required = false)
  int resultCode;

  @Path("response/")
  @Element(name = "email", required = false)
  String email;

  @Path("response/")
  @Element(name = "nickname", required = false)
  String nickname;

  @Path("response/")
  @Element(name = "age", required = false)
  String age;

  @Path("response/")
  @Element(name = "gender", required = false)
  String gender;

  @Path("response/")
  @Element(name = "name", required = false)
  String name;

  @Path("response/")
  @Element(name = "birthday", required = false)
  String birthday;

  public NaverUserInfo() {

    resultCode = -1;
  }

  public NaverUserInfo(int resultCode, String email, String nickname,
                       String age, String gender, String name, String birthday) {

    this.resultCode = resultCode;
    this.email = email;
    this.nickname = nickname;
    this.age = age;
    this.gender = gender;
    this.name = name;
    this.birthday = birthday;
  }

  public NaverUserInfo(Parcel in) {

    readFromParcel(in);
  }

  public int getResultCode() {
    return resultCode;
  }

  public void setResultCode(int resultCode) {
    this.resultCode = resultCode;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getNickname() {
    return nickname;
  }

  public void setNickname(String nickname) {
    this.nickname = nickname;
  }

  public String getAge() {
    return age;
  }

  public void setAge(String age) {
    this.age = age;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeInt(resultCode);
    dest.writeString(email);
    dest.writeString(nickname);
    dest.writeString(age);
    dest.writeString(gender);
    dest.writeString(name);
    dest.writeString(birthday);

  }

  private void readFromParcel(Parcel in) {

    this.resultCode = in.readInt();
    this.email = in.readString();
    this.nickname = in.readString();
    this.age = in.readString();
    this.gender = in.readString();
    this.name = in.readString();
    this.birthday = in.readString();
  }

  public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
    @Override
    public NaverUserInfo createFromParcel(Parcel in) {

      return new NaverUserInfo(in);
    }

    @Override
    public NaverUserInfo[] newArray(int size) {
      return new NaverUserInfo[size];
    }
  };
}
