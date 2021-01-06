package com.ft.flexiblethinking.web;

import com.ft.flexiblethinking.model.data.QueryQuestions;
import com.ft.flexiblethinking.model.submission.QuerySubmissions;
import com.ft.flexiblethinking.model.user.QueryUsers;
import com.ft.flexiblethinking.web.response.UserLoginSuc;
import com.ft.flexiblethinking.web.response.UserNoExists;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class Page {

    @Resource
    private QueryUsers qu;

    @Resource
    private QueryQuestions qq;

    @Resource
    private QuerySubmissions qs;

    Gson gson = new Gson();

    @PostMapping("/login")
    public String login(@RequestParam(value = "name", defaultValue = "") String name,
                        @RequestParam(value = "md5pswd", required = true) String md5pswd) {
        // Try to check ID.
        long uid = qu.checkExists(name, md5pswd);
        if (uid >= 0) {
            UserLoginSuc status = new UserLoginSuc();
            status.setUid(uid);
            status.setRedirectUrl("/user");
            status.setStatusCode(0);
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

    @GetMapping("/")
    public String frontpage() {
        return "<html><body><form action=\"/login\" method='POST' onsubmit='return toMD5()'>" +
                "<label for=\"name\">Name</label>" +
                "<input type=\"text\" id=\"name\" name=\"name\">" + "<br/>" +
                "<label for=\"pswd\">PSWD</label>" +
                "<input type=\"password\" id=\"pswd\">" + "<br/>" +
                "<input type=\"hidden\" id=\"md5pswd\" name=\"md5pswd\">" + "<br/>" +
                "<input type=\"submit\" value=\"Submit\">" +
                "</form>" +
                "<script src=\"https://cdn.bootcss.com/blueimp-md5/2.10.0/js/md5.js\"></script>" +
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
