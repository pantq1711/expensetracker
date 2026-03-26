package com.anphan.expensetracker.service;

import org.springframework.security.core.userdetails.User;
import com.anphan.expensetracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security goi method nay de load user khi verify token
    // "username" o day la email vi ta dung email lam subject trong JWT
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        com.anphan.expensetracker.entity.User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        //wrap entity User cua minh thanh UserDetails ma Spring Security hieu duoc
        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }

}
