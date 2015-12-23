package kr.co.digitalanchor.studytime.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 2015-10-26.
 */
public class FacebookProfile implements Parcelable {

  String id;

  String gender;

  String birthday;

  String email;

  String name;

  public FacebookProfile() {


  }


  public FacebookProfile(String id, String gender, String birthday,
                         String email, String name) {
    this.id = id;
    this.gender = gender;
    this.birthday = birthday;
    this.email = email;
    this.name = name;
  }

  public FacebookProfile(Parcel in) {

    readFromParcel(in);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getBirthday() {
    return birthday;
  }

  public void setBirthday(String birthday) {
    this.birthday = birthday;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {

    dest.writeString(id);
    dest.writeString(email);
    dest.writeString(gender);
    dest.writeString(name);
    dest.writeString(birthday);

  }

  private void readFromParcel(Parcel in) {

    this.id = in.readString();
    this.email = in.readString();
    this.gender = in.readString();
    this.name = in.readString();
    this.birthday = in.readString();

  }

  public static final Creator CREATOR = new Creator() {
    @Override
    public FacebookProfile createFromParcel(Parcel in) {

      return new FacebookProfile(in);
    }

    @Override
    public FacebookProfile[] newArray(int size) {
      return new FacebookProfile[size];
    }
  };
}
