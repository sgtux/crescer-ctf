package br.com.ctfpost.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.ctfpost.models.Challenge;
import br.com.ctfpost.models.UserChallenge;
import br.com.ctfpost.models.park.RankingModel;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class ChallengeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Challenge> getChallenges() {
        var sql = "SELECT u FROM Challenge u";
        return entityManager.createQuery(sql, Challenge.class)
                .getResultList();
    }

    public List<UserChallenge> getUserChallenges(int userId) {
        var sql = "SELECT u FROM UserChallenge u where u.user_id = :user_id";
        return entityManager.createQuery(sql, UserChallenge.class)
                .setParameter("user_id", userId)
                .getResultList();
    }

    public List<RankingModel> getRanking() {
        var sql = "select u.nickname, sum(c.score) score from flagpost.app_user u \n" + //
                "join flagpost.user_challenge uc on uc.user_id = u.id\n" + //
                "join flagpost.challenge c on uc.challenge_id = c.id\n" + //
                "where uc.correct = true\n" + //
                "group by u.nickname";
        List<Object[]> results = entityManager.createNativeQuery(sql).getResultList();
        List<RankingModel> ranking = new ArrayList<>();
        for (Object[] row : results) {
            String nickname = (String) row[0];
            Number score = (Number) row[1];
            ranking.add(new RankingModel(nickname, score.intValue()));
        }

        return ranking;
    }

    @Transactional
    public void updateUserChallenge(UserChallenge userChallenge) {
        String sql = "update flagpost.user_challenge set attempts = :attempts, correct = :correct, update_at = :update_at where challenge_id = :challenge_id and user_id = :user_id";
        entityManager.createNativeQuery(sql)
                .setParameter("attempts", userChallenge.attempts)
                .setParameter("correct", userChallenge.correct)
                .setParameter("update_at", new Date())
                .setParameter("challenge_id", userChallenge.challenge_id)
                .setParameter("user_id", userChallenge.user_id)
                .executeUpdate();
    }

    @Transactional
    public void createFlags(int userId) {
        String sql = "insert into flagpost.user_challenge (flag, attempts, correct, create_at, user_id, challenge_id) values (:flag, :attempts, :correct, :create_at, :user_id, :challenge_id)";
        for (var c : getChallenges()) {
            String flag = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            flag = String.format("%s%s%s", "CTF{", flag, "}");
            entityManager.createNativeQuery(sql)
                    .setParameter("flag", flag)
                    .setParameter("attempts", 0)
                    .setParameter("correct", false)
                    .setParameter("create_at", new Date())
                    .setParameter("user_id", userId)
                    .setParameter("challenge_id", c.id)
                    .executeUpdate();
        }
    }
}
