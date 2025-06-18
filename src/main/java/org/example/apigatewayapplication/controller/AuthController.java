package org.example.apigatewayapplication.controller;


import lombok.AllArgsConstructor;
import org.example.apigatewayapplication.model.AuthenticationRequest;
import org.example.apigatewayapplication.model.AuthenticationResponse;
import org.example.apigatewayapplication.model.entity.User;
import org.example.apigatewayapplication.service.JwtService;
import org.example.apigatewayapplication.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authenticationRequest.getUsername(),
                            authenticationRequest.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            String token = jwtService.generateToken(userDetails);

            return ResponseEntity.ok(new AuthenticationResponse(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            if (userService.existsByUsername(user.getUsername())) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("USER");  // Default role
            }

            userService.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }
}


