package br.com.ctfpost.controllers;

import br.com.ctfpost.models.User;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.Constants;
import jakarta.servlet.http.HttpServletRequest;

public class BaseController {

    private User _loggedUser;

    public User getLoggedUser(HttpServletRequest request, UserRepository userRepository) {

        if (_loggedUser != null)
            return _loggedUser;

        var cookies = request.getCookies();

        if (cookies == null)
            return null;

        String token = null;

        for (var c : cookies) {
            if (c.getName().equals(Constants.COOKIE_NAME)){
                token = c.getValue();
                return userRepository.getByToken(token);
            }
        }
        return null;
    }
}