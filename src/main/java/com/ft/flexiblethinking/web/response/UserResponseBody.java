package com.ft.flexiblethinking.web.response;

public class UserResponseBody {
    long uid;
    int statusCode;
    String message;

    public long getUid() {
        return uid;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
