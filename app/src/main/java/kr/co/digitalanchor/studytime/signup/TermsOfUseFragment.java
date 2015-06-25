package kr.co.digitalanchor.studytime.signup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import kr.co.digitalanchor.studytime.R;

/**
 * Created by Thomas on 2015-06-18.
 */
public class TermsOfUseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_webview, null);

        WebView webView = (WebView) view.findViewById(R.id.webView);

        webView.loadUrl("file:///android_asset/html/agreement.html");

        return view;
    }
}
