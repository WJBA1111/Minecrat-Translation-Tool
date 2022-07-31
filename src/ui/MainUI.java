package ui;

import api.TranslateAPI;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class MainUI extends Application {

    // 主要场景
    private Scene scene_main;
    private Scene scene_setting;

    private String appid;
    private String security_key;

    // 配置文件路径，当前目录的setting.properties文件
    File file_setting = new File("setting.properties");

    // 字体大小
    private static final int font_size = 15;

    // 读取数据
    private void init_data(){

        if(file_setting.exists()){
            try(FileInputStream fis = new FileInputStream(file_setting)) {
                Properties prop = new Properties();
                prop.load(fis);
                appid = prop.getProperty("appid");
                security_key = prop.getProperty("security_key");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            appid = "";
            security_key = "";
        }
    }


    // 主场景
    private void init_scene_main(Stage primaryStage){
        // 设置按钮
        Button btn_setting = new Button();
        btn_setting.setText("设置");
        btn_setting.setFont(Font.font(font_size));
        btn_setting.setOnAction(event -> {
            primaryStage.setScene(scene_setting);
        });


        // 正则表达式输入框
        TextField input_regex = new TextField();
        input_regex.setMinHeight(30);
        input_regex.setMinWidth(280);
        input_regex.setMaxWidth(280);
        input_regex.setFont(Font.font(font_size));

        // 多选框
        ChoiceBox<Object> input_choose = new ChoiceBox<>();
        input_choose.setMinHeight(30);
        input_choose.setMinWidth(280);
        input_choose.setMaxWidth(280);
        input_choose.setItems(FXCollections.observableArrayList(
                "Properties格式文件",
                new Separator(),
                "Json格式文件",
                new Separator(),
                "自定义正则"
        ));
        input_choose.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                int new_index = newValue.intValue();
                int old_index = oldValue.intValue();

                // 防止切换下拉框导致数据丢失
                if(old_index ==4){
                    TranslateAPI.SUSTOM = input_regex.getText();
                }

                // 通过模式设置不同的正则表达式
                if(new_index == 0){
                    input_regex.setDisable(true);
                    input_regex.setText(TranslateAPI.PROPERTIES_REGEX);
                } else if(new_index == 2){
                    input_regex.setDisable(true);
                    input_regex.setText(TranslateAPI.JSON_REGEX);
                } else if(new_index ==4){
                    input_regex.setDisable(false);
                    input_regex.setText(TranslateAPI.SUSTOM);
                }
            }
        });

        // 设置值或者设置序号
        // input_choose.setValue("Properties格式文件");
        input_choose.getSelectionModel().select(0);



        // 路径输入框
        TextField input_path = new TextField();
        input_path.setFont(Font.font(font_size));
        input_path.setMinHeight(30);
        input_path.setMinWidth(280);
        input_path.setMaxWidth(280);

        // 选择文件按钮
        Button btn_choose_file = new Button("选择文件");
        btn_choose_file.setMinHeight(30);
        btn_choose_file.setMinWidth(70);
        btn_choose_file.setFont(Font.font(font_size));
        btn_choose_file.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择文件");
            try{
                File file = fileChooser.showOpenDialog(primaryStage);
                input_path.setText(file.getAbsolutePath());
            }catch(NullPointerException e){
                System.out.println("没有选择文件");
            }
        });


        // 文字
        Text text_type = new Text("文件类型：");
        text_type.setFont(Font.font(font_size));

        Text text_regex = new Text("正则表达式：");
        text_regex.setFont(Font.font(font_size));

        Text text_file_path = new Text("文件路径：");
        text_file_path.setFont(Font.font(font_size));

        Text text_logs = new Text("日志：");
        text_logs.setFont(Font.font(font_size));

        TextArea input_logs = new TextArea("");
        // 设置不可编辑
        input_logs.setEditable(false);
        input_logs.setMinWidth(280);
        input_logs.setMaxWidth(280);
        //字体大小
        input_logs.setFont(Font.font(font_size));
        //允许自动换行
        input_logs.setWrapText(true);
        //初始化设置行数
        input_logs.setPrefRowCount(5);


        // 开始翻译按钮
        Button btn_start = new Button("开始翻译");
        btn_start.setMinHeight(30);
        btn_start.setMinWidth(280);
        btn_start.setFont(Font.font(font_size));
        btn_start.setOnAction(event ->{
            // 先移除日志
            input_logs.setText("");
            // 如果指定路径的文件存在，就翻译
            if(new File(input_path.getText()).exists()){
                String result_string = TranslateAPI.startTranslate(appid, security_key, input_path.getText(), input_regex.getText());
                input_logs.setText(result_string);
            } else{
                input_logs.setText("文件不存在，没有开始翻译");
            }
        });


        GridPane gridPane = new GridPane();
        // 网格布局居中
        gridPane.setAlignment(Pos.CENTER);
        // 设置水平间距
        gridPane.setHgap(20);
        // 设置垂直间距
        gridPane.setVgap(20);
        // 添加控件

        gridPane.add(text_type,0,0);
        gridPane.add(text_regex,0,1);
        gridPane.add(text_file_path,0,2);
        gridPane.add(btn_setting,0,3);
        gridPane.add(text_logs,0,4);
        gridPane.add(input_choose,1,0);
        gridPane.add(input_regex,1,1);
        gridPane.add(input_path,1,2);
        gridPane.add(btn_start,1,3);
        gridPane.add(input_logs,1,4);
        gridPane.add(btn_choose_file,2,2);

        // 场景
        scene_main = new Scene(gridPane, 600, 400);
    }

    // 设置场景
    private void init_scene_setting(Stage primaryStage){
        GridPane gridPane = new GridPane();
        // 网格布局居中
        gridPane.setAlignment(Pos.CENTER);
        // 设置水平间距
        gridPane.setHgap(20);
        // 设置垂直间距
        gridPane.setVgap(20);

        Text text_appid = new Text("appid");
        text_appid.setFont(Font.font(font_size));

        TextField input_appid = new TextField();
        input_appid.setMinHeight(30);
        input_appid.setMinWidth(280);
        input_appid.setText(appid);
        input_appid.setFont(Font.font(font_size));

        Text text_key = new Text("key");
        text_key.setFont(Font.font(font_size));

        TextField input_key = new TextField();
        input_key.setMinHeight(30);
        input_key.setMinWidth(280);
        input_key.setText(security_key);
        input_key.setFont(Font.font(font_size));

        Button btn_back = new Button("返回");
        btn_back.setFont(Font.font(font_size));
        btn_back.setOnAction(event -> {
            primaryStage.setScene(scene_main);
        });

        Button btn_set = new Button("保存");
        btn_set.setFont(Font.font(font_size));
        btn_set.setOnAction(event -> {
            appid = input_appid.getText();
            security_key = input_key.getText();

            if(!file_setting.exists()){
                try {
                    file_setting.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try(FileInputStream fis = new FileInputStream(file_setting)) {
                Properties prop = new Properties();
                prop.load(fis);

                FileOutputStream fos = new FileOutputStream(file_setting);
                prop.setProperty("appid",appid);
                prop.setProperty("security_key",security_key);
                prop.store(fos,"update");

            } catch (IOException e) {
                e.printStackTrace();
            }



        });

        Text ps = new Text("说明：");
        ps.setFont(Font.font(font_size));

        TextArea description = new TextArea("工具使用百度翻译的api，appid和key获取步骤如下：\n" +
                "1、登录百度账号登录百度翻译开放平台：http://api.fanyi.baidu.com\n" +
                "2、申请开通通用翻译：https://fanyi-api.baidu.com/choose\n" +
                "3、将appid和key填入本工具内");
        // 设置不可编辑
        description.setEditable(false);
        description.setMinWidth(280);
        description.setMaxWidth(280);
        //字体大小
        description.setFont(Font.font(font_size));
        //允许自动换行
        description.setWrapText(true);
        //初始化设置行数
        description.setPrefRowCount(5);


        gridPane.add(text_appid,0,0);
        gridPane.add(text_key,0,1);
        gridPane.add(ps, 0,2);
        gridPane.add(btn_back,0,3);
        gridPane.add(input_appid,1,0);
        gridPane.add(input_key,1,1);
        gridPane.add(description,1,2);
        gridPane.add(btn_set,1,3);


        scene_setting = new Scene(gridPane,600,400);
    }


    // Stage对象就是一个窗口对象
    @Override
    public void start(Stage primaryStage) throws Exception {
        // 设置窗口标题
        primaryStage.setTitle("文件机翻工具");
        // 加载数据
        init_data();

        System.out.println(appid+"："+security_key);

        // 初始化场景
        init_scene_main(primaryStage);
        init_scene_setting(primaryStage);

        // 设置指定场景
        primaryStage.setScene(scene_main);

        // 设置不可调节窗口大小
        primaryStage.setResizable(false);

        // 显示
        primaryStage.show();
    }


    public static void startUI() {
        launch("");
    }

}
