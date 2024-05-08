package obed.org.apirest.init;

import obed.org.apirest.model.user.Role;
import obed.org.apirest.model.user.User;
import obed.org.apirest.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserDataInitializer implements CommandLineRunner {
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${security.user.name}")
    private String username;

    @Value("${security.user.password}")
    private String password;

    @Value("${security.user.loadDefaultUserConfigurationOnLoad}")
    private Boolean loadDefaultUserConfigurationOnLoad;
    private final Role role = Role.ADMIN;

    @Override
    public void run(String... args) throws Exception {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        if(authService.userExists(user.getUsername())
                && !loadDefaultUserConfigurationOnLoad)
            return;
        authService.registerFirstUser(user);
    }

}
