package api;

import entity.Info;
import entity.ResultString;
import entity.TransResult;
import utils.HttpRequest;
import utils.IO;
import utils.MD5;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslateAPI{

    // 字数分段，英文建议5000，中文建议1900
    private static final int SEG = 5000;
    // 发起请求间隔，建议1.5s
    private static final int SLEEPTIME = 1500;
    // 匹配lang文件格式，aaa=bbb
    public static final String PROPERTIES_REGEX = "(?<==).*\\S";
    // 匹配json文件格式 "":"" 或 "" : ""
    public static final String JSON_REGEX = "(?<=: {0,50}\")[\\w\\W]*?(?=\")";
    // 匹配自定义
    public static String SUSTOM = "";

    private final String url= "http://api.fanyi.baidu.com/api/trans/vip/translate";  // https://fanyi-api.baidu.com/api/trans/vip/translate
    private final String salt; // 随机码
    private final String appid; // 从百度获取
    private final String security_key;    // appid的密码

    public TranslateAPI(String appid, String security_key){
        this.salt = System.currentTimeMillis()-5L+"";
        this.appid = appid;
        this.security_key = security_key;
    }

    // 输入翻译文字，返回翻译结果集   ,确保传入的q是utf-8编码，参数汉字推荐2000内，6000bytes
    public TransResult TranslateTo(String q){

        // 拼接字符串，生成md5密钥
        String sign = appid+""+q+salt+security_key;
        sign = MD5.md5(sign);

        // 将q传递给query参数，并将参数URL encode
        String query="";
        try {
            // 将字符串编码化，解码使用URLDecoder.decode()方法
            query = URLEncoder.encode(q, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("q",query);
        map.put("from","en");
        map.put("to","zh");
        map.put("appid", appid);
        map.put("salt", salt);
        map.put("sign", sign);

        return HttpRequest.sendGet(url, map);
    }



    // 输入appid、key、路径、序号，翻译指定文件
    public static String startTranslate(String appid, String security_key, String path, String regex){

        if(appid.equals("") || security_key.equals("")){
            return "appid和key为空，在设置中设置后才能翻译！";
        }

        if(regex.equals("")){
            return "正则表达式为空，输入正确后才能翻译！";
        }

        TranslateAPI trans = new TranslateAPI(appid, security_key);

        // 文件位置
        File file = new File(path);
        if(!file.exists()){
            System.out.println("文件不存在，终止翻译");
            return "文件不存在，终止翻译";
        }
        File file1 = new File(path+"1");
        // 读取文件内容
        String sb = IO.ReadFileToString(file);

        // 匹配=号到后面的非空白字符，就是[^ \f\n\r\t\v]
        // String regex = "(?<==)\\S*";
        // String regex = "(?<==).*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(sb);

        // 正则匹配到合适的字符串到集合
        ArrayList<Info> infoList = new ArrayList<>();
        int find_count = 0;
        while (m.find()){
            find_count++;
            String group = m.group();
            int start = m.start();
            int end = m.end();
            System.out.println("匹配到字符："+group+",开始下标:"+start+"，结束下标："+end);

            // 匹配minecraft的颜色字体，比如§4Minecraft，将颜色和名字分开，防止翻译错误：§4 Minecraft
            // 技术不够，不会，所以直接全部改成空
            group = group.replaceAll("§\\w","");
            // 移除换行符。json的值也许有换行符，导致发送翻译前后数量不一致
            group = group.replaceAll("[\\r\\n]","");

            // 创建一个存放信息的对象
            Info info = new Info();
            info.setInfo1(group);
            info.setStart(start);
            info.setEnd(end);
            // 存入list集合
            infoList.add(info);
        }
        System.out.println("匹配次数："+find_count+",arrayList长度："+infoList.size());
        if(infoList.size()==0){
            return "正则表达式没有匹配到字符，终止翻译";
        }

        ArrayList<String> str = new ArrayList<>();
        StringBuilder sb2 = new StringBuilder();
        // 对集合的元素分类
        for(int i=0; i<infoList.size();i++){
            Info info = infoList.get(i);
            String info1 = info.getInfo1();
            if (sb2.length() + info1.length() >= SEG) {
                // 移除最后一个换行符
                sb2.deleteCharAt(sb2.length() - 1);
                str.add(sb2.toString());
                sb2 = new StringBuilder();
            }
            sb2.append(info1).append("\n");
            // 防止最后一次循环没被添加到str集合
            if(i == infoList.size()-1){
                // 移除最后一个换行符
                sb2.deleteCharAt(sb2.length()-1);
                str.add(sb2.toString());

            }
            System.out.println("----------------------");
            System.out.println("循环次数："+i+"\n当前str的值是：\n"+str+"\n当前sb2的值是：\n"+sb2);
        }


        // 存放翻译后字符串的集合
        ArrayList<String> list = new ArrayList<>();
        // 按照2000字发送一次翻译请求，将结果存入map
        for (String s : str) {
            TransResult transResult = trans.TranslateTo(s);
            System.out.println(transResult);

            ResultString[] trans_result = transResult.getTrans_result();

            if(trans_result==null||trans_result.length==0){
                System.out.println("appid或key不对");
                return "appid或key不对";
            }

            try{
                for (ResultString resultString : trans_result) {
                    list.add(resultString.getDst());
                }
            }catch (Exception e){
                System.out.println("遍历百度api返回的翻译时出错，也许是appid或key不对导致的");
                return "遍历百度api返回的翻译时出错，也许是appid或key不对导致的";
            }


            try {
                Thread.sleep(SLEEPTIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return "翻译请求过程中报错，终止翻译";
            }
        }

        String newString = sb;
        // 将翻译结果存入infoList
        if(list.size()==infoList.size()){
            System.out.println("翻译前的集合元素数量和翻译后的数量都是："+list.size());

            for (int i = list.size()-1; i >= 0; i--) {
                // 翻译后的字符串
                String dst = list.get(i);

                Info info = infoList.get(i);
                int start = info.getStart();
                int end = info.getEnd();

                String pre = newString.substring(0,start);
                String sub = newString.substring(end);
                newString = pre + dst + sub;
            }

            System.out.println("翻译后："+newString);
            // 最终保存
            IO.SaveStringToFile(newString, file1);
            System.out.println("ok！！");

        }else{
            System.out.println("翻译前数量："+infoList.size()+"翻译后数量："+list.size()+"，数量不一致，终止翻译");
            return "未翻译字符和翻译后字符数量不一致，终止翻译";
        }
        return "翻译完成";

    }
}