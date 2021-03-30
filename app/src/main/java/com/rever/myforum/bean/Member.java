package com.rever.myforum.bean;

import java.sql.Timestamp;

public class Member {

    private int id;
    private String account;
    private String password;
    private String nickname;
    private Timestamp register_datetime;

    public Member(int id, String account, String password, String nickname, Timestamp register_datetime) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.register_datetime = register_datetime;
    }

    public Member(int id, String account, String password, String nickname) {
        this.id = id;
        this.account = account;
        this.password = password;
        this.nickname = nickname;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Timestamp getRegister_datetime() {
        return register_datetime;
    }

    public void setRegister_datetime(Timestamp register_datetime) {
        this.register_datetime = register_datetime;
    }
}
