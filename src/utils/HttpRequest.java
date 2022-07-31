package utils;

import com.alibaba.fastjson.JSONObject;
import entity.TransResult;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

// 发送http
public class HttpRequest<T>{
    // 发送get请求
    public static TransResult sendGet(String url, Map<String,String> param){
        String urlStr;
        if( param!=null && param.size()>0){
            // 拼接完整的get请求url
            StringBuilder url_path = new StringBuilder(url + "?");
            for (String str : param.keySet()) {
                url_path.append(str).append("=").append(param.get(str)).append("&");
            }
            urlStr = url_path.deleteCharAt(url_path.length() - 1).toString();
        }else{
            urlStr = url;
        }

        System.out.println("完整地址是"+urlStr);

        try {
            URL realUrl = new URL(urlStr);
            // 创建连接
            URLConnection urlConnection = realUrl.openConnection();
            urlConnection.setRequestProperty("accept","*/*");
            urlConnection.setRequestProperty("connection","Keep-Alive");
            urlConnection.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.69 Safari/537.36");
            // 连接
            urlConnection.connect();

            // 发送出去，并得到响应，接收到字节流
            InputStream inputStream = urlConnection.getInputStream();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int i = -1;
            while ((i = inputStream.read()) != -1) {
                baos.write(i);
            }

            // 结果是：{"from":"en","to":"zh","trans_result":[{"src":"apple","dst":"\u82f9\u679c"}]}

            JSONObject jsonObject = JSONObject.parseObject(String.valueOf(baos));

            return JSONObject.toJavaObject(jsonObject, TransResult.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}