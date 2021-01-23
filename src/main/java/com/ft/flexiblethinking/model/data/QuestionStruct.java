package com.ft.flexiblethinking.model.data;

public class QuestionStruct {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIn() {
        return in;
    }

    public String getOut() {
        return out;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public void setOut(String out) {
        this.out = out;
    }

    private String title;
    private String desc;
    private String in;
    private String out;

    public QuestionStruct(){}
}
