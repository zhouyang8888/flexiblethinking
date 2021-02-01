package com.ft.flexiblethinking.model.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private String title;
    private String desc;
    private String in;
    private String out;

    private boolean valid;

    Gson gson = new Gson();

    public  QuestionStruct() {
        this.valid = false;
    }

    public QuestionStruct(Question question){
        JsonObject jo = gson.fromJson(question.getContent(), JsonObject.class);
        this.setTitle(jo.get("title").getAsString());
        this.setDesc(jo.get("desc").getAsString());
        this.setIn(jo.get("in").getAsString());
        this.setOut(jo.get("out").getAsString());

        this.valid = question.getIsvalid();
    }

    public Question toQuestion() {
        JsonObject jo = new JsonObject();
        jo.add("title", new JsonPrimitive(this.title));
        jo.add("desc", new JsonPrimitive(this.desc));
        jo.add("in", new JsonPrimitive(this.in));
        jo.add("out", new JsonPrimitive(this.out));

        Question q = new Question();
        q.setContent(gson.toJson(jo));
        q.setIsvalid(this.valid);
        return q;
    }
}
