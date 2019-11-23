package eb.utils;

import com.alibaba.fastjson.JSONObject;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import eb.core.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class QiniuUploadUtil {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    public static String upload(File file) {

        //创建一个文件流
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {

            e.printStackTrace();
        }


        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(Zone.zone0());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        String accessKey = Config.QINIUYUN_API_KEY;
        String secretKey = Config.QINIUYUN_SECRET_KEY;
        String bucket = Config.QINIUYUN_BUCKET;

        LocalDateTime ldt = LocalDateTime.now();

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String fileKey = ldt.format(dtf);
        try {
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);
            try {
                Response response = uploadManager.put(stream, UUID.randomUUID().toString(), upToken, null, null);

                JSONObject parse = (JSONObject) JSONObject.parse(response.bodyString());
                String key = (String) parse.get("key");

                return "http://q0l9qvfyx.bkt.clouddn.com/" + key;

            } catch (QiniuException ex) {
                Response r = ex.response;
//                System.err.println(r.toString());
            }
        } catch (Exception ex) {
            // ignore
        }
        return null;
    }

}