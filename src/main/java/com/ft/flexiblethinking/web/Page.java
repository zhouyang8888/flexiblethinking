package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.common.SingletonCookieInfoService;
import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.data.Question;
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
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.function.Consumer;

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
            String qwer = check.getAsString();
            SingletonCookieInfoService.CookieInfo info = cookieInfo.getCookieInfo(qwer);
            if (info != null) {
                long loginid = ((Long) info.get("ID")).longValue();
                String name = (String) info.get("name");
                String md5pswd = (String) info.get("md5pswd");
                Instant startTime = (Instant) info.get("expire");
                Instant endTime = startTime.plus(Duration.ofDays(1));
                if (!Instant.now().isAfter(endTime)) {
                    long uid = qu.checkExists(name, md5pswd);
                    if (uid == loginid && uid > 0) {
                        UserResponseBody status = new UserResponseBody();
                        status.setUid(uid);
                        status.setStatusCode(10);
                        status.setMessage(name);
                        return gson.toJson(status);
                    }
                } else {
                    cookieInfo.removeCookieInfo(name);
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
            JsonObject jsonObject = new JsonObject();

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
                cookieInfo.put("expire", Instant.now());
                this.cookieInfo.putCookieInfo(md5random, cookieInfo);

                jsonObject.add("qwer", new JsonPrimitive(md5random));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            UserResponseBody status = new UserResponseBody();
            status.setUid(uid);
            status.setStatusCode(10);
            status.setMessage(name + " Login SUCCEEDED.");

            jsonObject.add("info", gson.toJsonTree(status));
            return gson.toJson(jsonObject);
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
        long uid = qu.checkExists(name);
        if (uid >= 0) {
            UserResponseBody status = new UserResponseBody();
            status.setUid(-1);
            status.setStatusCode(-1);
            status.setMessage(String.format("User %s already EXIST.", uid));
            return gson.toJson(status);
        } else {
            uid = qu.save(name, md5pswd);
            if (uid > 0) {
                UserResponseBody status = new UserResponseBody();
                status.setUid(uid);
                status.setStatusCode(0);
                status.setMessage(name + " SUCCEEDED in signup.");
                return gson.toJson(status);
            } else {
                UserResponseBody status = new UserResponseBody();
                status.setUid(-1);
                status.setStatusCode(-2);
                status.setMessage(name + " FAILED in signup.");
                return gson.toJson(status);
            }
        }
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @GetMapping("/api/list")
    public String getListByPageNo(@RequestParam long pn, @RequestParam long mpc) {
        long totalProblemCount = qq.count();
        List<QuestionStruct> problems = new ArrayList<>();
        long start = (pn - 1) * mpc + 1;
        long end = start + mpc;
        if (start <= totalProblemCount) {
            if (end > totalProblemCount + 1) {
                end = totalProblemCount + 1;
            }
            List<Question> pl = qq.findByID(start, end);
            for (Iterator<Question> itr =  pl.iterator(); itr.hasNext(); ) {
                Question q = itr.next();
                problems.add(new QuestionStruct(q));
            }
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
        Question q = qq.findByID(pid);
        return gson.toJson(new QuestionStruct(q));
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/submit")
    public String submit(@RequestBody String body) {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        long uid = job.get("uid").getAsLong();
        long pid = job.get("id").getAsLong();
        String answer = job.get("ans").getAsString().trim().toLowerCase();

        Question q = qq.findByID(pid);
        String expected = gson.fromJson(q.getContent(), JsonObject.class).get("out").getAsString();
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

    // ========================== ======================== //
    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/addproblem")
    public String addProblem(@RequestBody(required = true) String body) {
        JsonArray ja = gson.fromJson(body, JsonArray.class);
        List<Question> list = new LinkedList<>();
        for (int i = 0; i < ja.size(); i++) {
            Question q = new Question();
            q.setContent(gson.toJson(ja.get(i)));
            q.setIsvalid(true);
            list.add(q);
        }
        if (!list.isEmpty()) {
            int stored = qq.saveAll(list);
            if (stored == list.size()) {
                return gson.toJson(gson.fromJson("{message: \"All inserted.\"}", JsonObject.class));
            }
        }
        return gson.toJson(gson.fromJson("{message: \"Failure happened.\"}", JsonObject.class));
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/searchByID")
    public String searchByID(@RequestBody(required = true) String body) {
        JsonObject jso = gson.fromJson(body, JsonObject.class);
        long pid = jso.get("pid").getAsLong();
        Question q = qq.findByID(pid);
        return gson.toJson(q != null ? new QuestionStruct(q) : null);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/deleteByID")
    public String deleteByID(@RequestBody(required = true) String body) {
        JsonObject jso = gson.fromJson(body, JsonObject.class);
        long pid = jso.get("pid").getAsLong();
        qq.markDeleteByID(pid);
        Question q = qq.findByID(pid);
        return gson.toJson(q != null ? new QuestionStruct(q) : null);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/updateByID")
    public String updateByID(@RequestBody(required = true) String body) {
        JsonObject jso = gson.fromJson(body, JsonObject.class);
        long pid = jso.get("pid").getAsLong();
        String title = jso.get("t").getAsString();
        String desc = jso.get("d").getAsString();
        String input = jso.get("i").getAsString();
        String output = jso.get("o").getAsString();

        QuestionStruct qs = new QuestionStruct();
        qs.setTitle(title);
        qs.setDesc(desc);
        qs.setIn(input);
        qs.setOut(output);
        qs.setValid(true);

        Question q = qs.toQuestion();

        qq.updateByID(pid, q.getContent(), true);
        q = qq.findByID(pid);
        return gson.toJson(q == null ? null : new QuestionStruct(q));
    }
}
