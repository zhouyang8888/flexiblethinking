package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.common.SingletonCookieInfoService;
import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.data.Question;
import com.ft.flexiblethinking.model.data.QuestionStruct;
import com.ft.flexiblethinking.model.img.Image;
import com.ft.flexiblethinking.model.img.ImageStorage;
import com.ft.flexiblethinking.model.img.QueryImage;
import com.ft.flexiblethinking.model.submission.QuerySubmissions;
import com.ft.flexiblethinking.model.submission.Submission;
import com.ft.flexiblethinking.model.user.QueryUsers;
import com.ft.flexiblethinking.web.response.UserResponseBody;
import com.google.gson.*;
import org.apache.tomcat.jni.Time;
import org.apache.tomcat.util.security.MD5Encoder;
import org.hibernate.annotations.Parameter;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sun.security.provider.MD5;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    @Resource
    private QueryImage qi;

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
    @PostMapping("/api/submitsource")
    public String submitSource(@RequestBody String body) throws IOException, InterruptedException {
        JsonObject job = gson.fromJson(body, JsonObject.class);
        long uid = job.get("uid").getAsLong();
        long pid = job.get("id").getAsLong();
        String code = job.get("ans").getAsString();

        boolean correct = false;

        Question q = qq.findByID(pid);

        File dir = File.createTempFile(String.format("%010d", uid), "");
        if (dir.exists() || dir.mkdirs())
        {
            try {
                QuestionStruct questionStruct = new QuestionStruct(q);

                File source = new File(dir, questionStruct.getTitle() + ".cpp");
                File in = new File(dir, questionStruct.getTitle() + ".in");
                File out = new File(dir, questionStruct.getTitle() + ".out");

                BufferedOutputStream inFileOs = new BufferedOutputStream(new FileOutputStream(in));
                inFileOs.write(questionStruct.getIn().getBytes(StandardCharsets.UTF_8));
                inFileOs.flush();
                inFileOs.close();

                BufferedOutputStream sourceFileOs = new BufferedOutputStream(new FileOutputStream(source));
                sourceFileOs.write(code.getBytes(StandardCharsets.UTF_8));
                sourceFileOs.flush();
                sourceFileOs.close();

                Runtime runtime = Runtime.getRuntime();
                Process compiling = runtime.exec("g++ \"" + source.getName() + "\" -o \"" + questionStruct.getTitle() + ".bin\" > log", null, dir);
                if (compiling.waitFor(10, TimeUnit.SECONDS)) {
                    Process running = runtime.exec(dir.getAbsoluteFile() + File.separator + questionStruct.getTitle() + ".bin" + " < " + in.getAbsolutePath() + " > " + out.getAbsolutePath(), null, dir);
                    if (running.waitFor(1, TimeUnit.MINUTES)) {
                        BufferedReader outFileIs = new BufferedReader(new InputStreamReader(new FileInputStream(out)));
                        Stream<String> lines = outFileIs.lines();
                        Optional<String> merged = lines.reduce(new BinaryOperator<String>() {
                            @Override
                            public String apply(String s, String s2) {
                                return s + "\n" + s2;
                            }
                        });
                        if (merged.isPresent()) {
                            String execOut = merged.get();
                            if (execOut.equals(questionStruct.getOut())) {
                                correct = true;
                            }
                        }
                    } else {
                        running.destroyForcibly();
                    }
                } else {
                    compiling.destroyForcibly();
                }
            } finally {
                dir.delete();
            }
        }

        Submission submission = new Submission();
        submission.setUid(uid);
        submission.setQid(pid);
        submission.setAnswer(code);
        submission.setOK(correct);
        qs.add(submission);

        JsonObject respData = new JsonObject();
        respData.add("correct", new JsonPrimitive(correct));
        return gson.toJson(respData);
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
        submission.setOK(correct);
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

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @GetMapping("/api/getImg/{img}")
    public String getImg(@PathVariable(value = "img") Long imgID, HttpServletResponse response) throws IOException {
        Image img = qi.findByID(imgID);
        if (img != null) {
            String path = img.getRelpath();
            byte[] binaryImg = ImageStorage.getInstance().readImage(path);
            if (binaryImg != null) {
                response.setContentType(img.getType());
                OutputStream os = response.getOutputStream();
                os.write(binaryImg);
                os.flush();
                os.close();
            }
        }
        return "I don't know WHAT TO return.";
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/statistics")
    @ResponseBody
    public String getStatistics(@RequestBody(required = true) String body) {
        long uid = gson.fromJson(body, JsonObject.class).get("uid").getAsLong();
        Map<String, Long> sids = qs.getStatistics(uid);
        return gson.toJson(sids);
    }

    @CrossOrigin(origins = "http://127.0.0.1:8080")
    @PostMapping("/api/saveImg")
    @ResponseBody
    public String saveImgFiles(HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
        // 最强方式就是直接从request里面取内容
        MultipartHttpServletRequest mpHreq = (MultipartHttpServletRequest) request;
        long pid = Long.parseLong(mpHreq.getParameter("pid"));
        String[] iid = null;
        String iidString = mpHreq.getParameter("iid");
        if (iidString == null || iidString.trim().isEmpty()) {
            iid = new String[0];
        } else {
            iid = iidString.split(",");
            for (int i = 0; i < iid.length; i++)
                iid[i] = iid[i].trim();
        }
        List<MultipartFile> files = mpHreq.getFiles("file");
        if (files == null) files = new ArrayList<>();

        return saveImg(pid, iid, files);
    }

    private String saveImg(long pid, String[] iid, List<MultipartFile> files) throws IOException, NoSuchAlgorithmException {
        int code = 0;
        String msg = "";
        List<String> niid = new ArrayList<>();
        for (MultipartFile file : files) {
            int curCode = 0;
            String type = file.getContentType();
            byte[] bytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5bytes = md5.digest(bytes);
            String md5sum = MD5Encoder.encode(md5bytes);
            String path = "" + pid;

            Image img = null;
            List<Image> images = qi.findByMD5(md5sum);
            if (!images.isEmpty()) {
                img = images.get(0);
                msg += "_重复记录文件";
            } else {
                ImageStorage.SaveStatus status = ImageStorage.getInstance().saveImage(bytes, path);
                int sn = 1;
                while (status == ImageStorage.SaveStatus.EXISTENCE) {
                    // 文件路径已经存在
                    path = pid + "_" + sn++;
                    status = ImageStorage.getInstance().saveImage(bytes, path);
                }
                if (status == ImageStorage.SaveStatus.SUCCESS) {
                    img = new Image();
                    img.setMd5(md5sum);
                    img.setType(type);
                    img.setRelpath(path);
                    img = qi.saveOne(img);
                    if (img.getId() <= 0) {
                        ImageStorage.getInstance().deleteImage(path);
                        msg += "_保存失败";
                        curCode = ImageStorage.SaveStatus.EXCEPTION.ordinal();
                    } else {
                        msg += "_保存成功";
                    }
                } else if (status == ImageStorage.SaveStatus.EXCEPTION) {
                    msg += "_保存失败";
                    curCode = status.ordinal();
                }
            }

            if (curCode == 0) {
                niid.add("" + img.getId());
            } else {
                code -= curCode;
            }
        }
        Question q = qq.findByID(pid);
        QuestionStruct qs = new QuestionStruct(q);

        String[] imgs = new String[iid.length + niid.size()];
        System.arraycopy(iid, 0, imgs, 0, iid.length);
        System.arraycopy(niid.toArray(new String[niid.size()]), 0, imgs, iid.length, niid.size());
        qs.setImgs(imgs);

        q = qs.toQuestion();
        qq.updateByID(pid, q.getContent(), q.getIsvalid());

        return gson.toJson(gson.fromJson("{ status: " + code + ", msg: \"" + msg + "\" }", JsonObject.class));
    }
}
