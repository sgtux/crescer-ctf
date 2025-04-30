package br.com.ctfpost.controllers;

import java.security.Key;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.ctfpost.enums.Challenge;
import br.com.ctfpost.models.park.Login;
import br.com.ctfpost.models.park.Usuario;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import br.com.ctfpost.utils.PasswordHasher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("privilege-escalation")
public class PrivilegeEscalationController extends BaseController {

    private static final Key key = Keys.hmacShaKeyFor("minha-chave-super-secreta-de-no-mínimo-32-bytes!".getBytes());

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    String secret = "minhaChaveSecreta";

    @GetMapping("flag")
    public ResponseEntity<?> welcome(HttpServletRequest request, @RequestParam(required = false) String token) {

        var loggedUser = getLoggedUser(request, _userRepository);
        if (loggedUser == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        if (token == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION,
                            "/privilege-escalation/flag?token="
                                    + generateToken(loggedUser.id, loggedUser.email, loggedUser.nickname))
                    .build();
        }

        Map<String, Object> result;

        try {
            result = decodeClaims(token);
        } catch (Exception ex) {
            return ResponseEntity.status(400).body("Não foi possível decodificar o token.");
        }

        var funcao = result.get("funcao");

        if (funcao.equals("1")) {
            var challenge = _challengeRepository.getUserChallenges(loggedUser.id).stream()
                    .filter(p -> p.challenge_id == Challenge.PrivilegeEscalation.getValue()).findFirst().get();
            return ResponseEntity.ok(challenge.flag);
        }

        return ResponseEntity.ok("Olá " + result.get("username"));
    }

    public static String generateToken(int id, String email, String username) {
        long expirationMillis = 1000 * 60 * 60; // 1 hora

        return Jwts.builder()
                .claim("id", id)
                .claim("email", email)
                .claim("username", username)
                .claim("funcao", "2")
                .setSubject(String.valueOf(id))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public static Map<String, Object> decodeClaims(String jwt) throws Exception {
        String[] parts = jwt.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Token inválido.");
        }

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(payload, Map.class);
    }
}