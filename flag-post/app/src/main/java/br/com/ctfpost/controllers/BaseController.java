package br.com.ctfpost.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.ctfpost.models.User;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.Constants;
import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;

public class BaseController {

    public String getLoggedUserToken(HttpServletRequest request) {

        var cookies = request.getCookies();

        if (cookies == null)
            return null;

        String token = null;

        for (var c : cookies) {
            if (c.getName().equals(Constants.COOKIE_NAME)) {
                token = c.getValue();
                return token;
            }
        }
        return null;
    }

    public User getLoggedUser(HttpServletRequest request, UserRepository userRepository) {

        var loggedUserToken = getLoggedUserToken(request);

        if (StringUtil.isNullOrEmpty(loggedUserToken))
            return null;

        return userRepository.getByToken(loggedUserToken);
    }

    protected Connection createParkSqlConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://172.35.35.100:5432/postgres",
                    "park_user",
                    "park_123");
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }
}