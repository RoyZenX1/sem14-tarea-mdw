package pe.utp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.utp.model.User;
import pe.utp.repository.UserRepository;

@Controller
public class RegistroController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("user", new User());
        return "registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@Valid @ModelAttribute("user") User user, 
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(user.getUsername())) {
            result.rejectValue("username", "error.user", "El nombre de usuario ya está en uso");
        }

        if (result.hasErrors()) {
            return "registro";
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRol("USER");
        user.setActivo(true);
        
        userRepository.save(user);
        
        redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente");
        return "redirect:/login";
    }
}