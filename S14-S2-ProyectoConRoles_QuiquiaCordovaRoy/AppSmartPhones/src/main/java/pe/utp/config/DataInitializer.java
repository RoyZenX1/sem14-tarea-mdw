package pe.utp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import pe.utp.model.User;
import pe.utp.repository.UserRepository;

/**
 * Crea (solo si no existen) un usuario ADMIN y un usuario USER de prueba.
 * Los usuarios que se registran desde /registro siempre nacen como USER.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner crearUsuariosIniciales(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            crearSiNoExiste(repo, encoder, "admin", "Admin1234", "Administrador", "admin@example.com", "ADMIN");
            crearSiNoExiste(repo, encoder, "usuario", "User1234", "Usuario de Prueba", "usuario@example.com", "USER");
        };
    }

    private void crearSiNoExiste(UserRepository repo, PasswordEncoder encoder,
                                 String username, String rawPassword, String nombre, String email, String rol) {
        if (repo.existsByUsername(username)) {
            return;
        }
        User u = new User(username, encoder.encode(rawPassword), rol);
        u.setNombreCompleto(nombre);
        u.setEmail(email);
        u.setActivo(true);
        repo.save(u);
    }
}
