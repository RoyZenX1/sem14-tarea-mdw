package pe.utp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import pe.utp.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // habilita @PreAuthorize en los controladores (segunda capa de protección)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Recursos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/login", "/registro", "/error").permitAll()

                // Solo ADMIN: crear, editar y eliminar celulares
                .requestMatchers("/celulares/nuevo", "/celulares/guardar",
                                 "/celulares/editar/**", "/celulares/actualizar").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/celulares/eliminar/**").hasRole("ADMIN")
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // USER y ADMIN: solo lectura (lista y detalle)
                .requestMatchers("/celulares/lista", "/celulares/ver/**").hasAnyRole("USER", "ADMIN")

                // Página informativa de acceso denegado y raíz: cualquier usuario autenticado
                .requestMatchers("/", "/acceso-denegado").authenticated()

                // Cualquier otra ruta (incluido GET /celulares/eliminar/**) queda denegada
                .anyRequest().denyAll()
            )
            .userDetailsService(userDetailsService)
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/celulares/lista", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                 // se invoca por POST (con token CSRF)
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/acceso-denegado") // 403  para un USER que fuerza una URL de ADMIN
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
