package com.ft.flexiblethinking.model.user;

public interface IQueryUsers {
    public long checkExists(String name, String md5code);
    public long checkExists(String name);
}
