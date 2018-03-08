package com.antt.security;

import com.antt.domain.User;
import com.antt.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Created by antt on 3/5/2018.
 */
@Component(value = "authenticationProvider")
public class MPAuthenProvider implements AuthenticationProvider {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    public MPAuthenProvider(@Lazy UserDetailsService userDetailsService,
                            @Lazy PasswordEncoder passwordEncoder,
                            @Lazy UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getName());
        String providedPassword = (String) authentication.getCredentials();
        if (!passwordEncoder.matches(providedPassword, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid username/password");
        }

//        Optional<User> userFromDb = userRepository.findOneWithAuthoritiesByLogin(userDetails.getUsername());
//        return userFromDb.map(user -> {
//                return new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities());
//            }).orElseThrow(() -> new UsernameNotFoundException("User " + userDetails.getUsername() + " was not found in the " +
//                "database"));
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }
}
