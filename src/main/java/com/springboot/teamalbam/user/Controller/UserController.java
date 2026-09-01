package com.springboot.teamalbam.user.Controller;

import com.springboot.teamalbam.user.Entity.User;
import com.springboot.teamalbam.user.Repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Value("${app.cookie.secure}")
    private boolean cookieSecure;

    @Value("${app.cookie.same-site}")
    private String cookieSameSite;

    @GetMapping("/api/v1/main")
    public int entry(@CookieValue(value = "UUID", required = false) String uuidValue, HttpServletResponse response) {
        if (uuidValue != null && !uuidValue.isEmpty()) {
            return 0;
        } else {
            User user = new User();
            userRepository.save(user);

            ResponseCookie cookie = ResponseCookie.from("UUID", user.getUuid())
                    .path("/")
                    .maxAge(60 * 60 * 24 * 7)
                    .httpOnly(true)
                    .secure(cookieSecure)
                    .sameSite(cookieSameSite)
                    .build();
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
            return 1;
        }
    }

}
