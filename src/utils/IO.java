package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class IO {

    // 保存字符串到文件
    public static void SaveStringToFile(String str, File file){
        try (FileWriter fw = new FileWriter(file)){
            fw.write(str);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // 读取文件内容为字符串
    public static String ReadFileToString(File file){
        StringBuilder sb = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file)){
            byte[] buffer = new byte[1024];
            int len;
            while((len = fis.read(buffer))!= -1){
                sb.append(new String(buffer,0,len));
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return sb.toString();
    }


}
