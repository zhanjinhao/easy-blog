package eb.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImgFileBase64Util {
	private static BASE64Encoder encoder = new BASE64Encoder();
	private static BASE64Decoder decoder = new BASE64Decoder();
	/**
	 * 把照片转为Base64编码字符串
	 * 
	 * @param imgFilePath
	 *            imgFile路径
	 * @return
	 */
	public static String encodeImgFileToBase64(String imgFilePath) {
		byte[] data = null;
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(imgFilePath))) {
			data = new byte[inputStream.available()];
			inputStream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encoder.encode(data);
	}

	public static String encodeImgFileToBase64(File file) {
		byte[] data = null;
		try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
			data = new byte[inputStream.available()];
			inputStream.read(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return encoder.encode(data);
	}
	
	/**
	 * 把Base64编码的字符串转为照片
	 * @param imgFileBase64
	 * @param imgFilePath
	 * @return
	 */
	public static boolean decodeBase64ToImgFile(String imgFileBase64, String imgFilePath) {
		if (imgFileBase64 == null)
			return false;
		
		String pattern = "data:image/(png|jpg|jpeg);base64,";
		
		if(imgFileBase64.startsWith("data"))
			imgFileBase64 = imgFileBase64.replaceFirst(pattern, "");
		
		byte[] b = null;
		try {
			b = decoder.decodeBuffer(imgFileBase64);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try {
			FileUtil.forceCreateFile(imgFilePath);
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		try (OutputStream out = new BufferedOutputStream(new FileOutputStream(imgFilePath))){
			int length = b.length;
			for (int i = 0; i < length; i++) {
				if (b[i] < 0) {
					b[i] += 256;
				}
			}
			out.write(b);
			out.flush();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
