package br.com.ctfpost.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctfpost.enums.Challenge;
import br.com.ctfpost.models.park.Login;
import br.com.ctfpost.models.park.Usuario;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.PasswordHasher;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("sqlinjection")
public class SqlInjectionController extends BaseController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody Login login, HttpServletRequest request) {

        var loggedUser = getLoggedUser(request, _userRepository);
        if (loggedUser == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        String sql = String.format("select id, username from park.usuarios where username = '%s' and senha = '%s' ",
                login.username, PasswordHasher.hashMd5(login.senha));

        try (Connection connection = createParkSqlConnection();
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {
            if (rs.next()) {
                var user = new Usuario();
                user.id = rs.getInt("id");
                user.username = rs.getString("username");

                if (user.username.equals("ricardolima")) {
                    var challenge = _challengeRepository.getUserChallenges(loggedUser.id).stream()
                            .filter(p -> p.challenge_id == Challenge.SqlInjection1.getValue()).findFirst().get();
                    user.username = user.username + " - " + challenge.flag;
                }
                return ResponseEntity.ok(user);
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return ResponseEntity.status(403).body("Acesso não autorizado.");
    }

    @GetMapping("users")
    public ResponseEntity<?> users(HttpServletRequest request, @RequestParam String username) throws SQLException {

        var loggedUser = getLoggedUser(request, _userRepository);
        if (loggedUser == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        List<Usuario> users = new ArrayList<>();
        try (Connection connection = createParkSqlConnection()) {
            try (Statement statement = connection.createStatement();
                    ResultSet rs = statement
                            .executeQuery(String.format("select id, username from park.usuarios where username = '%s'",
                                    username))) {
                while (rs.next()) {
                    var user = new Usuario();
                    user.id = rs.getInt("id");
                    user.username = rs.getString("username");

                    if (user.username.contains("5b6a4c770e3c4686819d073fee3802e9")) {
                        var challenge = _challengeRepository.getUserChallenges(loggedUser.id).stream()
                                .filter(p -> p.challenge_id == Challenge.SqlInjection2.getValue()).findFirst().get();
                        user.username = challenge.flag;
                    }

                    users.add(user);
                }
            }
        }

        return ResponseEntity.ok(users);
    }
}