package com.ft.flexiblethinking.user;

import javax.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMd5code() {
        return md5code;
    }

    public void setMd5code(String md5code) {
        this.md5code = md5code;
    }

    private String name;
    private String md5code;
}
