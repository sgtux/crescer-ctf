package br.com.ctfpost.models;

import java.util.Date;

public class UserChallengeModel {

    public int challangeId;

    public String title;

    public String description;

    public int score;

    public String site;

    public String flag;

    public int attempts;

    public boolean correct;

    public Date create_at;

    public Date update_at;

    public int userId;

    public int challenge_id;
}