package cn.lger.encyclopedia.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import cn.lger.encyclopedia.R;


public class ChatFragment extends Fragment {

    public ChatFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        WebView webView = (WebView) rootView.findViewById(R.id.chat_robot);
        webView.loadUrl("file:///android_asset/chat_robot.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        return rootView;
    }
}
