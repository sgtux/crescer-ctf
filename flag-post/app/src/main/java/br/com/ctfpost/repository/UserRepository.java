package br.com.ctfpost.repository;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.ctfpost.models.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Repository
public class UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public User buscarPorId(Long id) {
        return entityManager.find(User.class, id);
    }

    @Transactional
    public void add(User user) {
        var sql = "INSERT INTO flagpost.app_user (nickname, email, password, create_at) VALUES (:nickname, :email, :password, :create_at)";
        entityManager.createNativeQuery(sql)
                .setParameter("nickname", user.nickname)
                .setParameter("email", user.email)
                .setParameter("password", user.password)
                .setParameter("create_at", new Date())
                .executeUpdate();
    }

    @Transactional
    public void updateToken(int userId, String token) {
        var sql = "update flagpost.app_user set token = :token where id = :id";
        entityManager.createNativeQuery(sql)
                .setParameter("id", userId)
                .setParameter("token", token)
                .executeUpdate();
    }

    public User getByEmailOrNickName(String nickname, String email) {
        var sql = "SELECT u FROM User u where u.nickname = :nickname or u.email = :email";
        var list = entityManager.createQuery(sql, User.class)
                .setParameter("nickname", nickname)
                .setParameter("email", email)
                .getResultList();

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    public User getByEmail(User user) {
        var sql = "SELECT u FROM User u where u.email = :email";
        var list = entityManager.createQuery(sql, User.class)
                .setParameter("email", user.email)
                .getResultList();

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    public User getByToken(String token) {
        var sql = "SELECT u FROM User u where u.token = :token";
        var list = entityManager.createQuery(sql, User.class)
                .setParameter("token", token)
                .getResultList();

        if (list.isEmpty())
            return null;

        return list.get(0);
    }

    public List<User> getAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class)
                .getResultList();
    }
}