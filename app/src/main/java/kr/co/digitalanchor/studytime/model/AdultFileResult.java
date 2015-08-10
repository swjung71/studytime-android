package kr.co.digitalanchor.studytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Seung Wook Jung on 2015-08-03.
 */
@Root(name = "AdultFileResult")
public class AdultFileResult {

    @Element(name = "ResultCode")
    int resultCode;

    @Element(name = "ResultMessage", required = false)
    String resultMessage;

    @ElementList(name = "FileName", required = false, inline = true)
    ArrayList<String> fileName;

    public int getResultCode() {  return resultCode;   }
    public void setResultCode(int resultCode) {  this.resultCode = resultCode;  }
    public String getResultMessage() {
        return resultMessage;
    }
    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
    public ArrayList<String> getFileName() {
        return fileName;
    }
    public void setFileName(ArrayList<String> fileName) {
        this.fileName = fileName;
    }
}
