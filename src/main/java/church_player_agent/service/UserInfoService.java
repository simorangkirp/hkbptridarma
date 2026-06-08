package church_player_agent.service;

import church_player_agent.entity.User;
import church_player_agent.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userDetail = repository.findByUsername(username);

        // Mengubah User entity menjadi UserDetails (format yang dipahami Spring
        // Security)
        return userDetail.map(user -> new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                new ArrayList<>() // Tambahkan role di sini jika sudah ada
        ))
                .orElseThrow(() -> new UsernameNotFoundException("User tidak ditemukan: " + username));
    }
}