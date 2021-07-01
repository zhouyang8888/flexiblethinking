package com.ft.flexiblethinking.model.img;

import com.ft.flexiblethinking.config.Config;
import org.apache.catalina.startup.Tomcat;

import java.io.*;

public class ImageStorage {
    public static enum SaveStatus {
        SUCCESS,
        EXISTENCE,
        EXCEPTION
    }

    public void deleteImage(String relPath) {
        if (relPath == null) return;

        String filePath = root + relPath;
        File f = new File(filePath);
        if (f.exists()) {
            f.delete();
        }
    }

    public synchronized SaveStatus saveImage(byte[] binary, String relPath) {
        if (relPath == null) return SaveStatus.EXCEPTION;

        String filePath = root + relPath;
        File file = new File(filePath);
        if (file.exists()) {
            return SaveStatus.EXISTENCE;
        }
        try {
            file.createNewFile();
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            bos.write(binary);
        } catch (IOException e) {
            e.printStackTrace();
            return SaveStatus.EXCEPTION;
        }
        return SaveStatus.SUCCESS;
    }

    public byte[] readImage(String relPath) {
        if (relPath == null) return null;

        String filePath = root + relPath;
        File file = new File(filePath);
        if (file.exists()) {
            long size = file.length();
            assert((int) size == size);
            byte[] binary = new byte[(int)size];
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath));
                size = bis.read(binary, 0, binary.length);
                int buffered = 0;
                while (size > 0 && buffered + size < binary.length) {
                    buffered += size;
                    size = bis.read(binary, buffered, binary.length - buffered);
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return null;
            }
            return binary;
        } else {
            return null;
        }
    }

    static volatile ImageStorage instance = null;
    String root = null;
    public static ImageStorage getInstance() {
        if (instance == null) {
            synchronized (ImageStorage.class) {
                if (instance == null) {
                    String root = Config.get("img.root");
                    if (root == null || root.isEmpty()) {
                        root = "." + File.separator;
                    } else if (!root.endsWith(File.separator)) {
                        root += File.separator;
                    } else {
                        // Nothing TODO.
                    }

                    ImageStorage instance = new ImageStorage();
                    instance.root = root;

                    File rootFile = new File(instance.root);
                    if (!rootFile.exists()) {
                        rootFile.mkdirs();
                    }

                    ImageStorage.instance = instance;
                }
            }
        }
        return instance;
    }
}
