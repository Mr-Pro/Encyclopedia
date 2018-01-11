package cn.lger.encyclopedia.ui.impl;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import cn.lger.encyclopedia.R;
import cn.lger.encyclopedia.ui.CustomFragment;
import cn.lger.encyclopedia.util.HttpUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class TranslateFragment extends CustomFragment {


    private static TranslateFragment translateFragment = null;
    private SearchViewClickListener searchViewClickListener = null;
    private String lastSearchResult = "";
    private TextView paraphrase = null;
    private Button detail = null;
    private Button UKButton = null;
    private Button USButton = null;
    private WebView webView = null;
    private TextView USPronunciation = null;
    private TextView UKPronunciation = null;
    private MediaPlayer UKMediaPlayer = null;
    private MediaPlayer USMediaPlayer = null;
    private LinearLayout searchResult = null;

    public TranslateFragment() {
        if (searchViewClickListener == null)
            searchViewClickListener = new SearchViewClickListener();
    }

    public static TranslateFragment getInstance() {
        if (translateFragment == null) {
            translateFragment = new TranslateFragment();
            return translateFragment;
        }
        return translateFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_translate, container, false);
        SearchView searchView = (SearchView) rootView.findViewById(R.id.search_word);
        searchView.setOnQueryTextListener(searchViewClickListener);
        searchView.setSubmitButtonEnabled(true);
        paraphrase = (TextView) rootView.findViewById(R.id.paraphrase);
        detail = (Button) rootView.findViewById(R.id.detail);
        UKButton = (Button) rootView.findViewById(R.id.uk_btn);
        USButton = (Button) rootView.findViewById(R.id.us_btn);
        UKPronunciation = (TextView) rootView.findViewById(R.id.uk_pronunciation);
        USPronunciation = (TextView) rootView.findViewById(R.id.us_pronunciation);
        searchResult = (LinearLayout) rootView.findViewById(R.id.search_result);
        searchResult.setVisibility(View.INVISIBLE);
        webView = (WebView) rootView.findViewById(R.id.detail_web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setVisibility(View.INVISIBLE);
        return rootView;
    }


    public class SearchViewClickListener implements SearchView.OnQueryTextListener {


        @Override
        public boolean onQueryTextSubmit(String s) {
            if (!lastSearchResult.equals(s)){
                System.out.println("上个结果："+lastSearchResult);
                SearchWordTask task = new SearchWordTask();
                task.execute("https://api.shanbay.com/bdc/search/?word="+s);
                searchResult.setVisibility(View.VISIBLE);
            }
            lastSearchResult = s;
            System.out.println("最新结果："+s);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return true;
        }
    }

    @Override
    public void destroyAllResource() {
        searchViewClickListener = null;
        translateFragment = null;
    }


    class SearchWordTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            return HttpUtil.getJSONResult(arg0[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result == null || "".equals(result)){
                Toast.makeText(getActivity(), "查询出错！", Toast.LENGTH_LONG).show();
            }else
                fillResultForJSON(result);

        }

        private void fillResultForJSON(String JSON){
            try {
                JSONObject object = new JSONObject(JSON);
                if ("SUCCESS".equals(object.getString("msg"))){
                    final JSONObject dataObject = object.getJSONObject("data");
                    paraphrase.setText("基本释义："+dataObject.getString("definition"));
                    final String uk_audio = dataObject.getString("uk_audio");
                    final String us_audio = dataObject.getString("us_audio");
                    detail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            webView.setVisibility(View.VISIBLE);
                            webView.loadUrl("https://www.shanbay.com/bdc/mobile/preview/word?word="+lastSearchResult);
                            webView.setWebViewClient(new WebViewClient());
                        }
                    });
                    UKButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                UKMediaPlayer = new MediaPlayer();
                                UKMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                UKMediaPlayer.setDataSource(uk_audio);
                                UKMediaPlayer.prepare(); // 这个过程可能需要一段时间，例如网上流的读取
                                UKMediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    USButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                USMediaPlayer = new MediaPlayer();
                                USMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                USMediaPlayer.setDataSource(us_audio);
                                USMediaPlayer.prepare(); // 这个过程可能需要一段时间，例如网上流的读取
                                USMediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    JSONObject pronunciations =dataObject.getJSONObject("pronunciations");
                    UKPronunciation.setText("英式发音：["+pronunciations.getString("uk")+"]");
                    USPronunciation.setText("美式发音：["+pronunciations.getString("us")+"]");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
