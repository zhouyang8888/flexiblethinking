package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.submission.QuerySubmissions;
import com.ft.flexiblethinking.model.user.QueryUsers;
import com.ft.flexiblethinking.web.response.UserResponseBody;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@RestController
public class Page {

    @Resource
    private QueryUsers qu;

    @Resource
    private QueryQuestions qq;

    @Resource
    private QuerySubmissions qs;

    Gson gson = new Gson();

    @CrossOrigin(origins = "http://127.0.0.1:8080")
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
            Cookie cookie = new Cookie("sn", Long.toHexString(uid));
            cookie.setMaxAge(24 * 60 * 60);
            cookie.setHttpOnly(true);
            // cookie.setSecure(true);//https
            response.addCookie(cookie);

            UserResponseBody status = new UserResponseBody();
            status.setUid(uid);
            status.setStatusCode(10);
            status.setMessage(name + " Login SUCCEEDED.");
            return gson.toJson(status);
        } else {
            UserResponseBody status = new UserResponseBody();

            uid = qu.checkExists(name);
            if (uid >= 0) {
                status.setUid(uid);
                status.setStatusCode(-11);
                status.setMessage("Password ERROR!");
            } else {
                status.setUid(uid);
                status.setStatusCode(-12);
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
            UserResponseBody status = new UserResponseBody();
            status.setUid(uid);
            status.setStatusCode(1);
            status.setMessage(String.format("User %s already EXIST.", uid));
            return gson.toJson(status);
        } else {
            uid = qu.save(name, md5pswd);
            if (uid > 0) {
                Cookie cookie = new Cookie("sn", Long.toHexString(uid));
                cookie.setMaxAge(24 * 60 * 60);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true); // https
                response.addCookie(cookie);

                UserResponseBody status = new UserResponseBody();
                status.setUid(uid);
                status.setStatusCode(0);
                status.setMessage(name + " SUCCEEDED in signup.");
                return gson.toJson(status);
            } else {
                UserResponseBody status = new UserResponseBody();
                status.setUid(uid);
                status.setStatusCode(-1);
                status.setMessage(name + " FAILED in signup.");
                return gson.toJson(status);
            }
        }
    }
}
