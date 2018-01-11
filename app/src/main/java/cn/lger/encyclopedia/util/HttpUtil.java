package cn.lger.encyclopedia.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Code that Changed the World
 * Pro said
 * Created by Pro on 2017-12-29.
 */
public class HttpUtil {

    public static String getJSONResult(String urlString){
        try {
            URL url = new URL(urlString);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
