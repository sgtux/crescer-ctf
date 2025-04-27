package br.com.ctfpost.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ctfpost.models.UserChallengeModel;
import br.com.ctfpost.models.UserModel;
import br.com.ctfpost.repository.ChallengeRepository;
import br.com.ctfpost.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@RestController()
@RequestMapping("challange")
public class ChallengeController extends BaseController {

    @Autowired
    private UserRepository _userRepository;

    @Autowired
    private ChallengeRepository _challengeRepository;

    @GetMapping("")
    public ResponseEntity<?> get(HttpServletRequest request) {
        var user = getLoggedUser(request, _userRepository);
        if (user == null)
            return ResponseEntity.status(401).body("Não autorizado.");

        var challanges = _challengeRepository.getChallenges();
        var userChallanges = _challengeRepository.getUserChallenges();

        return ResponseEntity.ok(new ArrayList<UserChallengeModel>());
    }

}
