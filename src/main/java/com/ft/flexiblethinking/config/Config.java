package com.ft.flexiblethinking.config;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static volatile Config conf = null;
    private Map<String, String> map = null;

    public static String get(String name) {
        Config conf = null;
        try {
            conf = getConf();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        return conf.map.get(name);
    }

    private static Config getConf() throws IOException {
        if (conf != null) return conf;
        else {
            synchronized (Config.class) {
                if (conf != null) return conf;
                else {
                    Map<String, String> map = new HashMap<>();
                    String path = Config.class.getClassLoader().getResource("app.config").getPath();
                    File file = new File(path);
                    if (file.exists()) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                        String line = br.readLine();
                        while (line != null) {
                            String[] pair = line.split("=", 2);
                            pair[0] = pair[0].trim();
                            if (!pair[0].isEmpty()) {
                                map.put(pair[0], pair[1].trim());
                            }
                            line = br.readLine();
                        }
                    }

                    Config newConfig = new Config();
                    newConfig.map = map;
                    conf = newConfig;
                    return conf;
                }
            }
        }
    }
}
