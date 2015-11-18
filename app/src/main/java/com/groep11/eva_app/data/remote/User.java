package com.groep11.eva_app.data.remote;

public class User {
    private String email;
    private String password;
    private String name;
    private String loginType;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        this.name = "nameFoo";
        this.loginType = "loginTypeFoo";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
