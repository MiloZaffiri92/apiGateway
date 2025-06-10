package org.example.apigatewayapplication.controller;


import lombok.RequiredArgsConstructor;
import org.example.apigatewayapplication.service.JwtService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Mono<AuthResponse> login(@RequestBody AuthRequest request) {
        return userDetailsService.findByUsername(request.getUsername())
                .filter(userDetails ->
                        passwordEncoder.matches(request.getPassword(), userDetails.getPassword()))
                .map(userDetails -> {
                    String token = jwtService.generateToken(userDetails);
                    return new AuthResponse(token);
                });
    }
}
record AuthRequest(String username, String password) {}
record AuthResponse(String token) {}

