package com.ft.flexiblethinking.model.data;

import com.google.gson.*;

import java.util.Iterator;

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

    public String[] getImgs() {
        return imgs;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    private String title;
    private String desc;
    private String in;
    private String out;
    private String[] imgs;

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
        JsonElement jeimgs = jo.get("imgs");
        if (jeimgs != null) {
            JsonArray ja = jeimgs.getAsJsonArray();
            imgs = new String[ja.size()];
            int i = 0;
            for (Iterator<JsonElement> itr = ja.iterator(); itr.hasNext(); ) {
                imgs[i++] = itr.next().getAsString();
            }
        }

        this.valid = question.getIsvalid();
    }

    public Question toQuestion() {
        JsonObject jo = new JsonObject();
        jo.add("title", new JsonPrimitive(this.title));
        jo.add("desc", new JsonPrimitive(this.desc));
        jo.add("in", new JsonPrimitive(this.in));
        jo.add("out", new JsonPrimitive(this.out));
        if (this.imgs != null && this.imgs.length > 0) {
            JsonArray ja = new JsonArray();
            for (int i = 0; i < this.imgs.length; i++)
                ja.add(new JsonPrimitive(this.imgs[i]));
            jo.add("imgs", ja);
        }

        Question q = new Question();
        q.setContent(gson.toJson(jo));
        q.setIsvalid(this.valid);
        return q;
    }
}
