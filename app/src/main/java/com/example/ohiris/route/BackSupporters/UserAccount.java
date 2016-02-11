package com.example.ohiris.route.BackSupporters;


public class UserAccount {
    private long id = 0;
    private String name;
    private String email;
    private String password;

    public  UserAccount(){

    }

    public UserAccount(long id, String name, String email, String password){
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;

    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public long getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.id = id;
    }
}
