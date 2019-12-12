package eb.core;

import eb.service.AuthService;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;

public class Config {

    public static String BAIDUOCR_API_KEY;
    public static String BAIDUOCR_API_SECRET;
    public static String BAIDUOCR_API_URL;
    public static String BAIDU_ACCESS_TOKEN;

    public static String QINIUYUN_API_KEY;
    public static String QINIUYUN_SECRET_KEY;
    public static String QINIUYUN_BUCKET;

    public static String BAIDU_LAST_UPDATE_TIME;

    public static String BAIDU_TRANSLATE_API_ID;
    public static String BAIDU_TRANSLATE_API_SECRET;


    // 浏览本地文件时的默认路径
    public static String LOCAL_DIR_PATH;

    // 图片保存至本地时的路径
    public static String SAVE_TO_LOCAL_PATH;

    static Properties propertiesIn;
    static String propertiesPath;

    static {
        try {
            propertiesPath = new File(".").getCanonicalPath() + File.separator + "easy-blog.properties";
            InputStream inputStream = new FileInputStream(propertiesPath);
            propertiesIn = new Properties();
            propertiesIn.load(inputStream);

            BAIDUOCR_API_KEY = propertiesIn.getProperty("baiduocr-api-key");
            BAIDUOCR_API_SECRET = propertiesIn.getProperty("baiduocr-api-secret");
            BAIDUOCR_API_URL = propertiesIn.getProperty("baiduocr-api-url");

            BAIDU_LAST_UPDATE_TIME = propertiesIn.getProperty("baidu-last-update-time");
            BAIDU_ACCESS_TOKEN = propertiesIn.getProperty("baidu-access-token");
            currentAuth = BAIDU_ACCESS_TOKEN;

            QINIUYUN_API_KEY = propertiesIn.getProperty("qiniuyun-api-key");
            QINIUYUN_SECRET_KEY = propertiesIn.getProperty("qiniuyun-secret-key");
            QINIUYUN_BUCKET = propertiesIn.getProperty("qiniuyun-bucket");
            LOCAL_DIR_PATH = propertiesIn.getProperty("local-dir-path");
            SAVE_TO_LOCAL_PATH = propertiesIn.getProperty("save-to-local-path");
            BAIDU_TRANSLATE_API_ID = propertiesIn.getProperty("baidu-translate-api-id");
            BAIDU_TRANSLATE_API_SECRET = propertiesIn.getProperty("baidu-translate-api-secret");

            System.out.println(BAIDU_LAST_UPDATE_TIME);
            if ("init".equals(BAIDU_LAST_UPDATE_TIME)) {
                FileOutputStream fileOutputStream = new FileOutputStream(propertiesPath);
                Properties propertiesOut = new Properties();
                String auth = AuthService.getAuth();
                System.out.println(auth);
                propertiesOut.setProperty("baiduocr-api-key", BAIDUOCR_API_KEY);
                propertiesOut.setProperty("baiduocr-api-secret", BAIDUOCR_API_SECRET);
                propertiesOut.setProperty("baiduocr-api-url", BAIDUOCR_API_URL);

                BAIDU_ACCESS_TOKEN = auth;
                propertiesOut.setProperty("baidu-access-token", auth);
                propertiesOut.setProperty("baidu-last-update-time", String.valueOf(System.currentTimeMillis()));

                propertiesOut.setProperty("qiniuyun-api-key", QINIUYUN_API_KEY);
                propertiesOut.setProperty("qiniuyun-secret-key", QINIUYUN_SECRET_KEY);
                propertiesOut.setProperty("qiniuyun-bucket", QINIUYUN_BUCKET);
                propertiesOut.setProperty("local-dir-path", LOCAL_DIR_PATH);
                propertiesOut.setProperty("save-to-local-path", SAVE_TO_LOCAL_PATH);
                propertiesOut.setProperty("baidu-translate-api-secret", BAIDU_TRANSLATE_API_SECRET);
                propertiesOut.setProperty("baidu-translate-api-id", BAIDU_TRANSLATE_API_ID);

                propertiesOut.store(fileOutputStream, new Date().toString());
                fileOutputStream.flush();
                fileOutputStream.close();
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(null, "配置文件有错！请检查。");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            System.exit(-1);

        }
    }

    private static String currentAuth;

    public static void getCurrentAuth() {
        long lastTime = Long.valueOf(BAIDU_LAST_UPDATE_TIME);

        long now = System.currentTimeMillis();
        if (now - lastTime > 20 * 24 * 60 * 60 * 1000) {
            String auth = AuthService.getAuth();
            currentAuth = auth;
        }
    }

    public static void updateProperties() {
        try {
            BAIDU_LAST_UPDATE_TIME = propertiesIn.getProperty("baidu-last-update-time");

            FileOutputStream fileOutputStream = new FileOutputStream(propertiesPath);
            Properties propertiesOut = new Properties();

            propertiesOut.setProperty("baiduocr-api-key", BAIDUOCR_API_KEY);
            propertiesOut.setProperty("baiduocr-api-secret", BAIDUOCR_API_SECRET);
            propertiesOut.setProperty("baiduocr-api-url", BAIDUOCR_API_URL);

            propertiesOut.setProperty("baidu-access-token", currentAuth);
            propertiesOut.setProperty("baidu-last-update-time", String.valueOf(System.currentTimeMillis()));

            propertiesOut.setProperty("qiniuyun-api-key", QINIUYUN_API_KEY);
            propertiesOut.setProperty("qiniuyun-secret-key", QINIUYUN_SECRET_KEY);
            propertiesOut.setProperty("qiniuyun-bucket", QINIUYUN_BUCKET);
            propertiesOut.setProperty("local-dir-path", LOCAL_DIR_PATH);
            propertiesOut.setProperty("save-to-local-path", SAVE_TO_LOCAL_PATH);
            propertiesOut.setProperty("baidu-translate-api-secret", BAIDU_TRANSLATE_API_SECRET);
            propertiesOut.setProperty("baidu-translate-api-id", BAIDU_TRANSLATE_API_ID);

            propertiesOut.store(fileOutputStream, new Date().toString());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "配置文件有错！请检查。");
            System.exit(-1);
        }
    }
}