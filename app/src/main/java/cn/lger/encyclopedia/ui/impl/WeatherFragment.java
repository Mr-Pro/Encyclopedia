package cn.lger.encyclopedia.ui.impl;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.lger.encyclopedia.R;
import cn.lger.encyclopedia.model.JsonBean;
import cn.lger.encyclopedia.ui.CustomFragment;
import cn.lger.encyclopedia.util.JsonFileReader;
import com.bigkoo.pickerview.OptionsPickerView;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class WeatherFragment extends CustomFragment {

    private static WeatherFragment weatherFragment = null;

    private TextView addressTextView = null;
    private String address;
    private ArrayList<JsonBean> options1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private SharedPreferences preferences = null;
    private SharedPreferences.Editor editor = null;
    private TextView todayWeather;

    public WeatherFragment() {

    }

    public static WeatherFragment getInstance() {
        if (weatherFragment == null) {
            weatherFragment = new WeatherFragment();
            return weatherFragment;
        }
        return weatherFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        preferences = getActivity().getSharedPreferences("lger", 0);
        addressTextView = (TextView) rootView.findViewById(R.id.addressTextView);
        todayWeather = (TextView) rootView.findViewById(R.id.todayWeather);
        address = preferences.getString("address", "");
        if ("".equals(address)) {
            addressTextView.setText("点击添加城市");
        } else {
            System.out.println(address);
            addressTextView.setText(address);
            showWeather();
        }

        rootView.findViewById(R.id.addressTextView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
            }
        });
        initJsonData();
        return rootView;
    }

    private void showWeather() {
        WeatherTask weatherTask = new WeatherTask();
        weatherTask.execute("http://v.juhe.cn/weather/index?key=d506993a24f67686bd1e6295a0e5a781&cityname=", address);
    }

    @Override
    public void destroyAllResource() {
        weatherFragment = null;
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(getActivity(), new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                address = options2Items.get(options1).get(options2);
                addressTextView.setText(address);
                preferences = getActivity().getSharedPreferences("lger", 0);
                editor = preferences.edit();
                editor.putString("address", address);
                editor.commit();
                showWeather();
            }
        }).setTitleText("")
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.GRAY)
                .setContentTextSize(13)
                .setOutSideCancelable(false)
                .build();
        pvOptions.setPicker(options1Items, options2Items);//二级选择器
        pvOptions.show();
    }

    private void initJsonData() {   //解析数据

        //注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
        //关键逻辑在于循环体

        //  获取json数据
        String JsonData = JsonFileReader.getJson(getActivity(), "province_data.json");
        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体


        //添加省份数据

        //注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
        //PickerView会通过getPickerViewText方法获取字符串显示出来。

        options1Items = jsonBean;

        for (JsonBean aJsonBean : jsonBean) {//遍历省份
            ArrayList<String> CityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> Province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < aJsonBean.getCityList().size(); c++) {//遍历该省份的所有城市
                String CityName = aJsonBean.getCityList().get(c).getName();
                CityList.add(CityName);//添加城市

                ArrayList<String> City_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                if (aJsonBean.getCityList().get(c).getArea() == null
                        || aJsonBean.getCityList().get(c).getArea().size() == 0) {
                    City_AreaList.add("");
                } else {

                    //该城市对应地区所有数据
                    //添加该城市所有地区数据
                    City_AreaList.addAll(aJsonBean.getCityList().get(c).getArea());
                }
                Province_AreaList.add(City_AreaList);//添加该省所有地区数据
            }

            //添加城市数据
            options2Items.add(CityList);

        }
    }

    private ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    class WeatherTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... arg0) {
            //
            try {
                URL url = new URL(arg0[0] + URLEncoder.encode(arg0[1], "UTF-8"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("accept", "*/*");
                conn.setRequestProperty("connection", "Keep-Alive");
                conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36");

                InputStream is = conn.getInputStream();
                byte[] buff = new byte[1024];
                int hasRead;
                StringBuilder result = new StringBuilder("");
                while ((hasRead = is.read(buff)) > 0) {
                    result.append(new String(buff, 0, hasRead));
                }
                return result.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null){
                parseWeatherJSON(result);
            }
        }

        private void parseWeatherJSON(String result){
            try {
                JSONObject object = new JSONObject(result);
                if (object.getInt("error_code") == 0){
                    JSONObject resultObj = object.getJSONObject("result");
                    JSONObject todayObj = resultObj.getJSONObject("today");
                    String weatherResult = "温度："+todayObj.getString("temperature")+"\n";
                    weatherResult += "天气状况："+todayObj.getString("weather")+"\n";
                    weatherResult += "风向："+todayObj.getString("wind")+"\n";
                    weatherResult += "穿衣建议："+todayObj.getString("dressing_advice");
                    todayWeather.setText(weatherResult);
                }else {
                    todayWeather.setText("请求出错！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
