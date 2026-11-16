package pe.utp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // El cierre de sesión lo procesa Spring Security (POST /logout) -> ver SecurityConfig.

    @GetMapping("/")
    public String inicio() {
        return "redirect:/celulares/lista";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
