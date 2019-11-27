package eb.core;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class SingleProcess {


    // 检查是否获得锁,true:获得锁，说明是第一次执行;false:没有取得锁，说明已经有一个程序在执行
    public static boolean checkLock() {
        FileLock lock = null;
        RandomAccessFile r = null;
        FileChannel fc = null;
        try {
            // 在临时文件夹创建一个临时文件，锁住这个文件用来保证应用程序只有一个实例被创建.
            File sf = new File(System.getProperty("java.io.tmpdir") + "lock.single");
            sf.deleteOnExit();
            sf.createNewFile();
            r = new RandomAccessFile(sf, "rw");
            fc = r.getChannel();
            lock = fc.tryLock();
            if (lock == null || !lock.isValid()) {
                // 如果没有得到锁，则程序退出.
                // 没有必要手动释放锁和关闭流，当程序退出时，他们会被关闭的.
                System.exit(-1);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}