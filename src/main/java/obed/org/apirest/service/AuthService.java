package obed.org.apirest.service;

import obed.org.apirest.model.user.UpdatePasswordRequest;
import obed.org.apirest.model.user.User;
import obed.org.apirest.repository.UserRepository;
import obed.org.apirest.model.user.AuthResponse;
import obed.org.apirest.model.user.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }
    public Boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public void registerFirstUser(User user) {
        Optional<User> existingUserOptional = userRepository.findByUsername(user.getUsername());
        if (existingUserOptional.isEmpty()) {
            userRepository.save(user);
            return;
        }
        User existingUser = existingUserOptional.get();
        existingUser.setPassword(user.getPassword());
        existingUser.setRole(user.getRole());
        userRepository.save(existingUser);
    }

    public void updatePassword(UpdatePasswordRequest request) {
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());
        if (userOptional.isEmpty())
            throw new Error("User not found");

        User user = userOptional.get();
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword()))
            throw new Error("Old password does not match");

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
