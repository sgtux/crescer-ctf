package br.com.ctfpost.repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.ctfpost.models.Challenge;
import br.com.ctfpost.models.UserChallenge;
import br.com.ctfpost.models.UserChallengeModel;
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
