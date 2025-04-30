package br.com.ctfpost.models;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UserModel {
    public int id;
    public String nickname;
    public String email;
    public Date createAt;
    public String createAtFormatted;

    public UserModel(User user) {
        id = user.id;
        nickname = user.nickname;
        email = user.email;
        createAt = user.create_at;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        createAtFormatted = sdf.format(user.create_at);
    }
}