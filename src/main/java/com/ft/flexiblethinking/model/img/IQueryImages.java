package com.ft.flexiblethinking.model.img;

public interface IQueryImages {
    public Image findByID(long id);
    public Image saveOne(Image img);
}
