package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.submission.QuerySubmissions;
import com.ft.flexiblethinking.model.user.QueryUsers;
import com.ft.flexiblethinking.web.response.UserExists;
import com.ft.flexiblethinking.web.response.UserLoginSuc;
import com.ft.flexiblethinking.web.response.UserNoExists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

@RestController
public class Page {

    @Resource
    private QueryUsers qu;

    @Resource
    private QueryQuestions qq;

    @Resource
    private QuerySubmissions qs;

    Gson gson = new Gson();

    @GetMapping("/problems")
    public String getProblems(@RequestParam int uid, HttpServletResponse response) {
        long cnt = qq.count();
        return "";
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/signin")
    public String login(@RequestBody(required = true) String body,
                        HttpServletResponse response) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        String name = job.get("name").getAsString();
        String md5pswd = job.get("md5pswd").getAsString();
        // Try to check ID.
        long uid = qu.checkExists(name, md5pswd);
        if (uid >= 0) {
            UserLoginSuc status = new UserLoginSuc();
            status.setUid(uid);
            status.setRedirectUrl("/user");
            status.setStatusCode(0);

            Cookie cookie = new Cookie("sn", Long.toHexString(uid));
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setHttpOnly(true);
            // cookie.setSecure(true);//https
            response.addCookie(cookie);

            return gson.toJson(status);
        } else {
            UserNoExists status = new UserNoExists();

            uid = qu.checkExists(name);
            if (uid >= 0) {
                status.setUid(uid);
                status.setStatusCode(-2);
                status.setMessage("Password ERROR!");
            } else {
                status.setUid(uid);
                status.setStatusCode(-1);
                status.setMessage(String.format("User %s NOT exist!", name));
            }
            return gson.toJson(status);
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/signup")
    public String logup(@RequestBody(required = true) String body,
                        HttpServletResponse response) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        String name = job.get("name").getAsString();
        String md5pswd = job.get("md5pswd").getAsString();
        long uid = qu.checkExists(name, md5pswd);
        if (uid >= 0) {
            UserExists status = new UserExists();
            status.setUid(uid);
            status.setStatusCode(-1);
            status.setMessage(String.format("User %s already exist.", uid));
            return gson.toJson(status);
        } else {
            boolean ok = qu.save(name, md5pswd);
            if (ok) {
                Cookie cookie = new Cookie("sn", Long.toHexString(uid));
                cookie.setMaxAge(24 * 60 * 60);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true); // https
                response.addCookie(cookie);
            }
            return gson.toJson(name + (ok ? " Succeeded " : " Failed ") + "in signing.");
        }
    }

    @GetMapping("/")
    public String frontpage() {
        return "<html><body><form action=\"/login\" method='POST' onsubmit='return toMD5()'>" +
                "<label for=\"name\">Name</label>" +
                "<input type=\"text\" id=\"name\" name=\"name\">" + "<br/>" +
                "<label for=\"pswd\">PSWD</label>" +
                "<input type=\"password\" id=\"pswd\">" + "<br/>" +
                "<input type=\"hidden\" id=\"md5pswd\" name=\"md5pswd\">" + "<br/>" +
                "<input type=\"submit\" id=\"signin\" value=\"signin\" formaction=\"signin\" formmethod=\"POST\">" +
                "<input type=\"submit\" id=\"signup\" value=\"signup\" formaction=\"signup\" formmethod=\"POST\">" +
                "</form>" +
                "<script src=\"https://cdn.bootcss.com/blueimp-md5/2.10.0/js/md5.js\"></script>" +
                "<script src=\"http://ajax.googleapis.com/ajax/libs/jquery/1.4.0/jquery.min.js\"></script>" +
                "<script>" +
                "function toMD5() {" +
                "   var a = document.getElementById('pswd');" +
                "   md5pswd = document.getElementById('md5pswd');" +
                "   md5pswd.value = md5(a);" +
                "   /*alert(md5pswd.value);*/" +
                "   return true;" +
                "}" +
                "</script>" +
                "</body></html>";
    }
}
