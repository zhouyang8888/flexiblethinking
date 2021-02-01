package com.ft.flexiblethinking.model.data;

import javax.persistence.*;

@Entity
@Table(name="questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(boolean valid) {
        isvalid = valid;
    }

    private String content;

    private boolean isvalid = true;
}
