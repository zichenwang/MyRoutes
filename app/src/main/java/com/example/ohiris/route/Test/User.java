package com.example.ohiris.route.Test;

/**
 * Created by OhIris on 1/25/16.
 */
public class User {

    private long userId;
    private String username;
    private String password;

    public User(long userId, String username, String password){
        this.userId=userId;
        this.username=username;
        this.password=password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
