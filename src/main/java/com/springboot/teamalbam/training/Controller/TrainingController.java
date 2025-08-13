package com.springboot.teamalbam.training.Controller;

import com.springboot.teamalbam.training.DTO.TrainingDto;
import com.springboot.teamalbam.training.Service.TrainingService;
import com.springboot.teamalbam.user.Entity.User;
import com.springboot.teamalbam.user.Repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/training")
public class TrainingController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TrainingService trainingService;

    @PostMapping("/start")
    public boolean startTraining(HttpServletRequest request) {
        String uuid = getUuidFromCookie(request);
        if (uuid == null) return false;

        User user = userRepository.findByUuid(uuid).orElse(null);
        if (user == null) return false;

        int attempts = user.getAttemptsLeft();
        if (attempts <= 0) return false;

        user.setAttemptsLeft(attempts - 1);
        userRepository.save(user);
        System.out.println("UUID: " + uuid + ", Attempts left: " + user.getAttemptsLeft());

        return true;
    }

    @GetMapping("/load")
    public List<TrainingDto> loadTests() {
        return trainingService.loadAllTestsWithOptions();
    }


    private String getUuidFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> "UUID".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}
