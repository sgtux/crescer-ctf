package br.com.ctfpost.controllers;

import br.com.ctfpost.models.User;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.Constants;
import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;

public class BaseController {

    private User _loggedUser;

    private String _loggedUserToken;

    public String getLoggedUserToken(HttpServletRequest request) {

        if (!StringUtil.isNullOrEmpty(_loggedUserToken))
            return _loggedUserToken;

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

        if (_loggedUser != null)
            return _loggedUser;

        if (StringUtil.isNullOrEmpty(_loggedUserToken))
            _loggedUserToken = getLoggedUserToken(request);

        if (StringUtil.isNullOrEmpty(_loggedUserToken))
            return null;

        _loggedUser = userRepository.getByToken(_loggedUserToken);
        
        return _loggedUser;
    }
}