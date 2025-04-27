package br.com.ctfpost.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "app_user", schema = "flagpost")
public class User {
    @Id
    public int id;

    public String nickname;

    public String email;

    public String password;

    @Temporal(TemporalType.TIMESTAMP)
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
