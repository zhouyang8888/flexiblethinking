package com.ft.flexiblethinking.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Page {

    @GetMapping("/login")
    public String login(@RequestParam(value = "name", defaultValue = "") String name,
                        @RequestParam(value = "md5pswd", required = true) String md5pswd) {
        // Try to check ID.

        return String.format("It's OK, name=%s, pswd=%s", name, md5pswd);
    }

    @GetMapping("/")
    public String frontpage() {
        return "<html><body><form action=\"/login\" method='GET' onsubmit='return toMD5()'>" +
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
