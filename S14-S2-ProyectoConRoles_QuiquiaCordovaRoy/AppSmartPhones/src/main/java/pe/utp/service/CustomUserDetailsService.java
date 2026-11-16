package pe.utp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import pe.utp.model.User;
import pe.utp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // Se usa el rol guardado en la BD ("ADMIN" o "USER") -> authority ROLE_ADMIN / ROLE_USER.
        // Antes estaba fijo en "USER", por eso nadie podía ser ADMIN.
        String rol = (user.getRol() == null || user.getRol().isBlank())
                ? "USER"
                : user.getRol().trim().toUpperCase();

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())      // hash BCrypt
                .roles(rol)
                .disabled(!user.isActivo())
                .build();
    }
}
