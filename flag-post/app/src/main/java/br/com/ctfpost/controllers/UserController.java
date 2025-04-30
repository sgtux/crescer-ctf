package br.com.ctfpost.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctfpost.models.CreateUser;
import br.com.ctfpost.models.User;
import br.com.ctfpost.models.UserModel;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.Constants;
import br.com.ctfpost.utils.PasswordHasher;
import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController()
@RequestMapping("user")
public class UserController extends BaseController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        if (StringUtil.isNullOrEmpty(user.email) || StringUtil.isNullOrEmpty(user.password))
            return ResponseEntity.badRequest().body("Informe email e senha.");

        var userdb = _userRepository.getByEmail(user);
        if (userdb == null)
            return ResponseEntity.status(401).body("Usuário não encontrado.");

        if (!userdb.password.equals(PasswordHasher.hashSHA512(user.password)))
            return ResponseEntity.status(401).body("Senha incorreta.");

        var token = UUID.randomUUID().toString();
        _userRepository.updateToken(userdb.id, token);

        var challenges = _challengeRepository.getUserChallenges(user.id);
        if (!challenges.isEmpty()) {
            _challengeRepository.createFlags(userdb.id);
        }

        var cookie = new Cookie(Constants.COOKIE_NAME, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        response.addCookie(cookie);

        return ResponseEntity.ok("Login realizado com sucesso.");
    }

    @PostMapping("create")
    public ResponseEntity<?> create(@RequestBody CreateUser user, HttpServletResponse response) {
        try {

            if (StringUtil.isNullOrEmpty(user.email) || user.email.length() < 5 || user.email.contains(" ")
                    || !user.email.contains("@") || !user.email.contains("."))
                return ResponseEntity.badRequest().body("Email inválido.");

            if (StringUtil.isNullOrEmpty(user.nickname) || user.nickname.length() < 5)
                return ResponseEntity.badRequest().body("Nickname deve ter no mínimo 5 caracteres.");

            if (!user.nickname.matches("^[a-zA-Z0-9#$_@]+$"))
                return ResponseEntity.badRequest().body("Nickname possui caracteres inválidos.");

            if (StringUtil.isNullOrEmpty(user.password) || user.password.length() < 8)
                return ResponseEntity.badRequest().body("Senha deve ter no mínimo 8 caracteres.");

            if (!user.password.equals(user.confirm))
                return ResponseEntity.badRequest().body("As senhas não batem.");

            var userdb = _userRepository.getByEmailOrNickName(user.nickname, user.email);
            if (userdb != null) {
                if (userdb.email.equals(user.email))
                    return ResponseEntity.badRequest().body("Email já cadastrado.");
                return ResponseEntity.badRequest().body("Nickname já cadastrado.");
            }

            user.password = PasswordHasher.hashSHA512(user.password);

            _userRepository.add(new User(user));

            return ResponseEntity.ok("Criado com sucesso.");
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body(ex.getMessage());
        }
    }

    @GetMapping("data")
    public ResponseEntity<?> get(HttpServletRequest request) {
        var user = getLoggedUser(request, _userRepository);
        if (user == null)
            return ResponseEntity.status(401).body("Não autorizado.");
        return ResponseEntity.ok(new UserModel(user));
    }

    @GetMapping("logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        var user = getLoggedUser(request, _userRepository);

        for (var item : request.getCookies()) {
            if (item.getName().equals(Constants.COOKIE_NAME)) {
                item.setMaxAge(0);
                item.setPath("/");
                item.setHttpOnly(true);
                response.addCookie(item);
            }
        }

        if (user != null)
            _userRepository.updateToken(user.id, null);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/login.html")
                .build();
    }
}