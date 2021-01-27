package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.common.SingletonCookieInfoService;
import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.data.QuestionStruct;
import com.ft.flexiblethinking.model.submission.QuerySubmissions;
import com.ft.flexiblethinking.model.submission.Submission;
import com.ft.flexiblethinking.model.user.QueryUsers;
import com.ft.flexiblethinking.web.response.UserResponseBody;
import com.google.gson.*;
import org.apache.tomcat.jni.Time;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.web.bind.annotation.*;
import sun.security.provider.MD5;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RestController
public class Page {

    @Resource
    private SingletonCookieInfoService cookieInfo;

    @Resource
    private QueryUsers qu;

    @Resource
    private QueryQuestions qq;

    @Resource
    private QuerySubmissions qs;

    Gson gson = new Gson();

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/signincheck")
    public String loginCheck(@RequestBody(required = true) String body) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        JsonElement check = job.get("check");
        if (check != null) {
            String checkid = check.getAsString();
            SingletonCookieInfoService.CookieInfo info = cookieInfo.getCookieInfo(checkid);
            if (info != null) {
                long id = ((Long) info.get("ID")).longValue();
                String name = (String) info.get("name");
                String md5pswd = (String) info.get("md5pswd");
                long uid = qu.checkExists(name, md5pswd);
                if (uid == id && uid > 0) {
                    UserResponseBody status = new UserResponseBody();
                    status.setUid(uid);
                    status.setStatusCode(10);
                    status.setMessage(name);
                    return gson.toJson(status);
                }
            }
        }
        UserResponseBody status = new UserResponseBody();
        status.setUid(-1l);
        status.setStatusCode(-13);
        status.setMessage("To login first.");
        return gson.toJson(status);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/signin")
    public String login(@RequestBody(required = true) String body,
                        HttpServletResponse response) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        String name = job.get("name").getAsString();
        String md5pswd = job.get("md5pswd").getAsString();
        // Try to check ID.
        long uid = qu.checkExists(name, md5pswd);
        if (uid >= 0) {
            MessageDigest md5 = null;
            try {
                md5 = MessageDigest.getInstance("MD5");
                byte[] md5bytes = md5.digest(("" + Instant.now().getEpochSecond() + '#' +
                        ((Long)Thread.currentThread().getId())).getBytes());
                String md5random = MD5Encoder.encode(md5bytes);
                SingletonCookieInfoService.CookieInfo cookieInfo = new SingletonCookieInfoService.CookieInfo();
                cookieInfo.put("name", name);
                cookieInfo.put("ID", uid);
                cookieInfo.put("md5pswd", md5pswd);
                this.cookieInfo.putCookieInfo(md5random, cookieInfo);

                Cookie cookie = new Cookie("qwer", md5random);
                cookie.setMaxAge(24 * 60 * 60);
                cookie.setHttpOnly(true);
                // cookie.setSecure(true);//https
                response.addCookie(cookie);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

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
    @PostMapping("/api/signup")
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

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @GetMapping("/api/list")
    public String getListByPageNo(@RequestParam long pn, @RequestParam long mpc) {
        long totalProblemCount = qq.count();
        List<QuestionStruct> problems = null;
        long start = (pn - 1) * mpc + 1;
        long end = start + mpc;
        if (start <= totalProblemCount) {
            if (end > totalProblemCount + 1) {
                end = totalProblemCount + 1;
            }
            problems = qq.findByID(start, end);
        } else {
            problems = new ArrayList<>();
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("pageCount", new JsonPrimitive((totalProblemCount + mpc - 1) / mpc));
        jsonObject.add("problems", gson.toJsonTree(problems));
        return gson.toJson(jsonObject);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/problem")
    public String getProblemByPID(@RequestBody String body) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        long pid = job.get("id").getAsLong();
        QuestionStruct q = qq.findByID(pid);
        return gson.toJson(q);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/submit")
    public String submit(@RequestBody String body) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        long uid = job.get("uid").getAsLong();
        long pid = job.get("id").getAsLong();
        String answer = job.get("ans").getAsString().trim().toLowerCase();

        QuestionStruct q = qq.findByID(pid);
        String expected = q.getOut();
        boolean correct = answer.equals(expected.trim().toLowerCase());
        Submission submission = new Submission();
        submission.setUid(uid);
        submission.setQid(pid);
        submission.setAnswer(answer);
        qs.add(submission);

        JsonObject respData = new JsonObject();
        respData.add("correct", new JsonPrimitive(correct));
        return gson.toJson(respData);
    }
}
