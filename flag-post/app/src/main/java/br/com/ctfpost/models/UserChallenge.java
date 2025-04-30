package br.com.ctfpost.models;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_challenge", schema = "flagpost")
public class UserChallenge {

    @Id
    public int id;

    public String flag;

    public int attempts;

    public boolean correct;

    public Date create_at;

    public Date update_at;

    public int user_id;

    public int challenge_id;
}