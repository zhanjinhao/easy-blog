package eb.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	
	/**
	 * 给定抽象路径名，创建文件（无论是否存在父目录），如果已存在则删除
	 * @param imgPath
	 * @throws IOException
	 */
	public static File forceCreateFile(String imgPath) throws IOException {
		File file = new File(imgPath);
		if(file.exists())
			file.delete();
		File fileParent = file.getParentFile();
		if (!fileParent.exists()) {
			fileParent.mkdirs();
		}
		file.createNewFile();
		return file;
	}
	
	public static String getGradeAndMajorFromUid(String uid) {
		return uid.substring(0, 6);
	}
	
	public static void copyFile(File oldFile, File newFile) {
		try(
				BufferedInputStream bi = new BufferedInputStream(new FileInputStream(oldFile));
				BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(newFile));
				
				){
			
	        byte[] bs = new byte[1024*1024]; 
	        int n = -1;
            while ((n = bi.read(bs)) != -1) {
            	bo.write(bs,0,n);
            }
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
