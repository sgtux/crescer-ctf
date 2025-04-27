package br.com.ctfpost.models;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_user", schema = "flagpost")
public class User {
    @Id
    public int id;
    public String nickname;
    public String email;
    public String password;
    public Date create_at;
    public String token;

    public User() {
    }

    public User(CreateUser user) {
        nickname = user.nickname;
        password = user.password;
        email = user.email;
    }
}
