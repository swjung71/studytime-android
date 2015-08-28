package kr.co.digitalanchor.studytime.purchase;

import android.os.Bundle;
import android.webkit.WebView;

import kr.co.digitalanchor.studytime.BaseActivity;
import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-08-28.
 */
public class ClauseViewActivity extends BaseActivity {

    WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_webview);

        webView = (WebView) findViewById(R.id.webView);

        webView.loadUrl("file:///android_asset/html/agreement_pay.html");
    }
}
