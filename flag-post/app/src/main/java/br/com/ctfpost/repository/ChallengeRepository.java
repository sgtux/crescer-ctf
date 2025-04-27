package br.com.ctfpost.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import br.com.ctfpost.models.Challenge;
import br.com.ctfpost.models.UserChallenge;
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

    public List<UserChallenge> getUserChallenges() {
        var sql = "SELECT u FROM UserChallenge u";
        return entityManager.createQuery(sql, UserChallenge.class)
                .getResultList();
    }

    @Transactional
    public void createFlags(int userId) {
        String sql = "insert into flagpost.user_challenge (flag, attempts, correct, user_id, challenge_id) values (:flag, :attempts, :correct, :user_id, :challenge_id)";
        for (var c : getChallenges()) {
            String flag = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
            flag = String.format("%s%s%s", "CTF{", flag, "}");
            entityManager.createNativeQuery(sql)
                    .setParameter("flag", flag)
                    .setParameter("attempts", 0)
                    .setParameter("correct", false)
                    .setParameter("user_id", userId)
                    .setParameter("challenge_id", c.id)
                    .executeUpdate();
        }
    }
}
