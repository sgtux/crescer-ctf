package br.com.ctfpost.models;

import java.util.Date;

public class UserChallengeModel {

    public int challengeId;

    public String title;

    public String description;

    public int score;

    public String site;

    public String flag;

    public int attempts;

    public boolean correct;

    public Date createAt;

    public Date updateAt;

    public int userId;

    public UserChallengeModel(UserChallenge userChallenge, Challenge challenge) {
        challengeId = challenge.id;
        title = challenge.title;
        description = challenge.description;
        score = challenge.score;
        site = challenge.site;
        flag = userChallenge.correct ? userChallenge.flag : "";
        attempts = userChallenge.attempts;
        correct = userChallenge.correct;
        createAt = userChallenge.create_at;
        updateAt = userChallenge.update_at;
        userId = userChallenge.user_id;
    }
}