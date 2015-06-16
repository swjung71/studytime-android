package kr.co.digitalanchor.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class HTTPUtils {

    public static int m_nBootingTimeOut = 0;
    public static int m_nMessageTimeOut = 60000;
    public static int m_nTimeOut = 60000;
    public static int m_nUploadTimeOut = 600000;

    static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

        public boolean verify(String hostname, SSLSession session) {

            return true;
        }
    };

    public static String postData(String request, String url) {

        HttpClient httpclient = new DefaultHttpClient();
        MyHttpClient test = new MyHttpClient();
        httpclient = test.sslClient(httpclient);

        String ua = "My Custom UA Header String";

        HttpPost httppost = new HttpPost(url);
//		HttpPost httppost = new HttpPost(GlobalVariable.LINK_API + url);


        try {

            List<NameValuePair> values = new ArrayList<NameValuePair>();

            // values.add(new BasicNameValuePair("xml", request));
            // encode UTF-8 for request string
            values.add(new BasicNameValuePair("xml", StringUtils
                    .convertToUTF8(request)));

            httppost.setEntity(new UrlEncodedFormEntity(values));
            // httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
            // System.getProperty("http.agent"));
            httppost.setHeader("User-Agent", ua);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return StringUtils.inputStreamToString(response.getEntity()
                    .getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

//	public static String postData(String request, String url) {
//		return postDataWithoutSSL(request, url);
//	}


    public static String postDataWithoutSSL(String request, String url) {
        HttpClient httpclient = new DefaultHttpClient();

        String ua = "My Custom UA Header String";

        HttpPost httppost = new HttpPost(url);
//		HttpPost httppost = new HttpPost(GlobalVariable.LINK_API2+ url);

//		System.out.println("url: " + GlobalVariable.LINK_API_Test + url);

        try {

            List<NameValuePair> values = new ArrayList<NameValuePair>();

            // values.add(new BasicNameValuePair("xml", request));
            // encode UTF-8 for request string
            values.add(new BasicNameValuePair("xml", StringUtils
                    .convertToUTF8(request)));

            httppost.setEntity(new UrlEncodedFormEntity(values));
            // httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
            // System.getProperty("http.agent"));
            httppost.setHeader("User-Agent", ua);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

            return StringUtils.inputStreamToString(response.getEntity()
                    .getContent());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return e.getMessage();
        }
    }

    public static String postFileSend(String xmlData, String url, String filePath) {
        String result = "";

        try {
            /*
             * For https
			 */
            HttpClient httpclient = new DefaultHttpClient();
            MyHttpClient test = new MyHttpClient();
            httpclient = test.sslClient(httpclient);

            HttpPost post = new HttpPost(url);

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

            entityBuilder.addTextBody("xml", StringUtils.convertToUTF8(xmlData));
            if (filePath != null) {
                File file = new File(filePath);
                entityBuilder.addBinaryBody("TransferFile", file);
            }

            HttpEntity entity = entityBuilder.build();
            post.setEntity(entity);

            HttpResponse response = httpclient.execute(post);

            HttpEntity httpEntity = response.getEntity();
            result = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }


}
