package com.ft.flexiblethinking.model.img;

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

    static ImageStorage instance = null;
    static String root = null;
    public static ImageStorage getInstance() {
        if (instance == null) {
            synchronized (ImageStorage.class) {
                if (instance == null) {
                    instance = new ImageStorage();

                    BufferedReader fr = null;
                    try {
                        String path = ImageStorage.class.getClassLoader().getResource("app.config").getPath();
                        fr = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                        fr = null;
                    }
                    if (fr != null) {
                        try {
                            String line = fr.readLine();
                            while (line != null) {
                                String[] pair = line.trim().split("\\s*=\\s*", 2);
                                if (pair.length == 2) {
                                    if (pair[0].equals("img.root")) {
                                        root = pair[1].trim();
                                        if (root.isEmpty()) {
                                            root = "." + File.separator;
                                        } else if (!root.endsWith(File.separator)) {
                                            root += File.separator;
                                        } else {
                                            // Nothing to do.
                                        }
                                        break;
                                    }
                                }
                            }
                        } catch (IOException ios) {
                            ios.printStackTrace();
                            root = "." + File.separator;
                        }
                        try {
                            fr.close();
                        } catch (IOException ios) {
                            ios.printStackTrace();
                        }
                    } else {
                        root = "." + File.separator;
                    }
                }
            }
        }
        File rootFile = new File(root);
        if (!rootFile.exists()) {
            rootFile.mkdirs();
        }
        return instance;
    }
}
