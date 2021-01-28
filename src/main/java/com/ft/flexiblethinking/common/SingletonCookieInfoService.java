package com.ft.flexiblethinking.common;

import org.springframework.stereotype.Service;
import sun.misc.LRUCache;

import javax.management.timer.Timer;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

@Service
public class SingletonCookieInfoService {
    public static class CookieInfo extends HashMap<String, Object> {
    };

    LRU<CookieInfo> loginUser = new LRU<CookieInfo>(100);

    public CookieInfo getCookieInfo(String md5sum) {
        return loginUser.get(md5sum);
    }

    public void putCookieInfo(String md5sum, CookieInfo ci) {
        loginUser.add(md5sum, ci);
    }

    public void removeCookieInfo(String md5sum) {
        loginUser.remove(md5sum);
    }
}
