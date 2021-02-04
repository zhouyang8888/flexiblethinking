package com.ft.flexiblethinking.model.img;

import javax.persistence.*;

@Entity
@Table(name="images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String md5;
    private String relpath;
    private int type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getRelpath() {
        return relpath;
    }

    public void setRelpath(String relpath) {
        this.relpath = relpath;
    }

    public void setType(String type) {
        this.type = ImageTypeHelper.getInstance().name2i(type);
    }

    public String getType() {
        return ImageTypeHelper.getInstance().i2name(this.type);
    }
}
