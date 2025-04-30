package br.com.ctfpost.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctfpost.models.Challenge;
import br.com.ctfpost.models.FlagModel;
import br.com.ctfpost.models.UserChallengeModel;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import ch.qos.logback.core.util.StringUtil;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("challenge")
public class ChallengeController extends BaseController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    @GetMapping("all")
    public ResponseEntity<?> get(HttpServletRequest request) {
        var user = getLoggedUser(request, _userRepository);
        if (user == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        var list = new ArrayList<UserChallengeModel>();

        var challenges = _challengeRepository.getChallenges();
        for (var item : _challengeRepository.getUserChallenges(user.id)) {
            Challenge challenge = challenges.stream()
                    .filter(c -> c.id == item.challenge_id)
                    .findFirst().get();
            list.add(new UserChallengeModel(item, challenge));
        }

        return ResponseEntity.ok(list);
    }

    @PostMapping("flag")
    public ResponseEntity<?> get(HttpServletRequest request, @RequestBody FlagModel flagModel) {
        var user = getLoggedUser(request, _userRepository);
        if (user == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        var item = _challengeRepository.getUserChallenges(user.id)
                .stream()
                .filter(c -> c.challenge_id == flagModel.challengeId)
                .findFirst();

        if (item.isEmpty())
            return ResponseEntity.status(400).body("Não encontrado.");

        var dbUserChallenge = item.get();
        dbUserChallenge.attempts++;
        dbUserChallenge.correct = !StringUtil.isNullOrEmpty(flagModel.flag)
                && flagModel.flag.equals(dbUserChallenge.flag);

        if (!dbUserChallenge.correct)
            return ResponseEntity.status(400).body("Flag inválida.");

        _challengeRepository.updateUserChallenge(dbUserChallenge);

        return ResponseEntity.ok("Pontuado com sucesso.");
    }
    
    @GetMapping("ranking")
    public ResponseEntity<?> ranking(HttpServletRequest request) {
        var user = getLoggedUser(request, _userRepository);
        if (user == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        var ranking = _challengeRepository.getRanking();

        return ResponseEntity.ok(ranking);
    }
}