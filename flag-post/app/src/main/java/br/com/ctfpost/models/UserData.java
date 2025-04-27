package br.com.ctfpost.models;

import java.sql.Date;

public class UserData {
    public int id;
    public String nickname;
    public String email;
    public Date createAt;

    public UserData(User user) {
        id = user.id;
        nickname = user.nickname;
        email = user.email;
        createAt = user.create_at;
    }
}