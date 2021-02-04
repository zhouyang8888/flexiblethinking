package com.ft.flexiblethinking.model.img;

import com.sun.deploy.security.ValidationState;

public class ImageTypeHelper {
    enum Type{
        PNG,
        JPEG
    }
    public int name2i(String name) {
        name = name.trim().toUpperCase();
        StringBuffer sb = new StringBuffer();
        int i = name.length() - 1;
        while (i >= 0 && name.charAt(i) >= 'A' && name.charAt(i) <= 'Z') i--;
        Type t = Type.valueOf(name.substring(i+1));
        return t.ordinal();
    }

    public String i2name(int type) {
        return "image/" + Type.values()[type].name().toLowerCase();
    }

    private static ImageTypeHelper instance = null;

    public static ImageTypeHelper getInstance() {
        if (instance == null) {
            synchronized (ImageTypeHelper.class) {
                if (instance == null) {
                    instance = new ImageTypeHelper();
                }
            }
        }
        return instance;
    }
}
