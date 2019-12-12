package eb.service;

import eb.utils.http.QiniuUploadUtil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

public class QiniuService {
    static ExecutorService executor = Executors.newCachedThreadPool();

    public static void doQiniuUpload(final BufferedImage image, String captureImgPath) {
        Callable<String> task = () -> {
            File file = new File(captureImgPath);
            try {
                ImageIO.write(image, "jpg", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                String upload = QiniuUploadUtil.upload(file);
                return upload;
            } catch (Exception e) {
                return null;
            }
        };
        Future<String> submit = null;
        try {
            submit = executor.submit(task);
            String s = submit.get(10, TimeUnit.SECONDS);
            if(s == null){
                JOptionPane.showMessageDialog(null, "请检查网络设置...");
            } else {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
                JOptionPane.showMessageDialog(null, "已上传成功！");
            }
        } catch (Exception e) {
            submit.cancel(true);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "请检查网络设置...");
        }
    }
}