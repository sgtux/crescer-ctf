package br.com.ctfpost.controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctfpost.enums.Challenge;
import br.com.ctfpost.models.park.Usuario;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("idor")
public class IdorController extends BaseController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    @GetMapping("users")
    public ResponseEntity<?> users(HttpServletRequest request, @RequestParam int userId) throws SQLException {

        var loggedUser = getLoggedUser(request, _userRepository);
        if (loggedUser == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        var user = new Usuario();

        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://172.35.35.100:5432/postgres",
                "park_user",
                "park_123")) {
            try (Statement statement = connection.createStatement();
                    ResultSet rs = statement
                            .executeQuery(
                                    "select id, username, email, conta_bancaria, agencia, saldo, estado_civil, sexo from park.usuarios where id = "
                                            + userId)) {
                if (rs.next()) {
                    user.id = rs.getInt("id");
                    user.username = rs.getString("username");
                    user.email = rs.getString("email");
                    user.contaBancaria = rs.getString("conta_bancaria");
                    user.agencia = rs.getString("agencia");
                    user.saldo = rs.getDouble("saldo");
                    user.estadoCivil = rs.getString("estado_civil");
                    user.sexo = rs.getString("sexo");

                    if (user.id == 7) {
                        var challenge = _challengeRepository.getUserChallenges(loggedUser.id).stream()
                                .filter(p -> p.challenge_id == Challenge.Idor.getValue()).findFirst().get();
                        user.agencia = user.agencia + " - " + challenge.flag;
                    }
                }
            }
        }

        return ResponseEntity.ok(user);
    }
}