package eb.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import eb.core.Config;
import eb.utils.http.BaiduOCRHttpUtil;
import eb.utils.ImgFileBase64Util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.*;

public class BaiduService {
    static ExecutorService executor = Executors.newCachedThreadPool();

    public static String doOcr(BufferedImage image, String tempFilePath){

        Callable<String> task = () -> {
            File file = new File(tempFilePath);
            try {
                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String base64 = ImgFileBase64Util.encodeImgFileToBase64(file);

            String result = null;
            try {
                result = BaiduOCRHttpUtil.post(Config.BAIDUOCR_API_URL, Config.BAIDU_ACCESS_TOKEN, "application/x-www-form-urlencoded",
                        URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(base64, "UTF-8"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println(result);
            JSONObject parse = (JSONObject) JSONObject.parse(result);

            JSONArray words_result = (JSONArray) parse.get("words_result");
            String format = "";
            Iterator<Object> iterator = words_result.iterator();
            while (iterator.hasNext()) {
                JSONObject jsonObject = (JSONObject) iterator.next();
                format += jsonObject.get("words") + "\n";
            }
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(format), null);
            return format;
        };
        Future<String> submit = executor.submit(task);
        try {
            String s = submit.get(10, TimeUnit.SECONDS);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
            return s;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            submit.cancel(true);
            e.printStackTrace();
        }
        return null;
    }
}