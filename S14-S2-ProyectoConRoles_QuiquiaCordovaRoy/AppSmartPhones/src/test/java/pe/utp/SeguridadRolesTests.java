package pe.utp;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import pe.utp.model.Celular;
import pe.utp.repository.CelularRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SeguridadRolesTests {

    @Autowired MockMvc mvc;
    @Autowired CelularRepository celulares;

    @BeforeEach
    void datos() {
        celulares.deleteAll();
        celulares.save(new Celular("SN-001", "Samsung", "S24", 50, 256, "https://ejemplo.com/a.jpg"));
    }

    private static final String FORM_CELULAR_VALIDO_SN = "SN-NUEVO";

    // ---------- Sin sesión ----------
    @Test
    void anonimo_esRedirigidoALogin() throws Exception {
        mvc.perform(get("/celulares/lista")).andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("**/login"));
        mvc.perform(get("/celulares/nuevo")).andExpect(status().is3xxRedirection())
           .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void loginYRegistro_sonPublicos() throws Exception {
        mvc.perform(get("/login")).andExpect(status().isOk());
        mvc.perform(get("/registro")).andExpect(status().isOk());
    }

    // ---------- USER: solo lectura ----------
    @Test
    void user_veListaYDetalle_sinBotonesDeAdmin() throws Exception {
        mvc.perform(get("/celulares/lista").with(user("juan").roles("USER")))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Samsung")))
           .andExpect(content().string(containsString("Salir")))
           .andExpect(content().string(not(containsString("Nuevo Celular"))))
           .andExpect(content().string(not(containsString("Editar"))))
           .andExpect(content().string(not(containsString("Eliminar"))));

        mvc.perform(get("/celulares/ver/SN-001").with(user("juan").roles("USER")))
           .andExpect(status().isOk())
           .andExpect(content().string(not(containsString("Editar"))))
           .andExpect(content().string(not(containsString("Eliminar"))));
    }

    @Test
    void user_noPuedeCrearEditarNiEliminar_aunqueForceLaUrl() throws Exception {
        var u = user("juan").roles("USER");

        mvc.perform(get("/celulares/nuevo").with(u))
           .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/acceso-denegado"));
        mvc.perform(get("/celulares/editar/SN-001").with(u))
           .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/acceso-denegado"));

        mvc.perform(post("/celulares/guardar").with(u).with(csrf())
                .param("numeroSerie", FORM_CELULAR_VALIDO_SN).param("marca", "X").param("modelo", "Y")
                .param("megapixeles", "50").param("almacenamiento_GB", "128"))
           .andExpect(redirectedUrl("/acceso-denegado"));
        mvc.perform(post("/celulares/actualizar").with(u).with(csrf())
                .param("numeroSerie", "SN-001").param("marca", "Hack").param("modelo", "Y")
                .param("megapixeles", "50").param("almacenamiento_GB", "128"))
           .andExpect(redirectedUrl("/acceso-denegado"));
        mvc.perform(post("/celulares/eliminar/SN-001").with(u).with(csrf()))
           .andExpect(redirectedUrl("/acceso-denegado"));

        // nada cambió en la BD
        org.junit.jupiter.api.Assertions.assertEquals(1, celulares.count());
        org.junit.jupiter.api.Assertions.assertEquals("Samsung", celulares.findById("SN-001").orElseThrow().getMarca());
    }

    // ---------- ADMIN: CRUD completo ----------
    @Test
    void admin_veTodosLosBotones() throws Exception {
        mvc.perform(get("/celulares/lista").with(user("root").roles("ADMIN")))
           .andExpect(status().isOk())
           .andExpect(content().string(containsString("Nuevo Celular")))
           .andExpect(content().string(containsString("Editar")))
           .andExpect(content().string(containsString("Eliminar")))
           .andExpect(content().string(containsString("Salir")));
    }

    @Test
    void admin_puedeCrearEditarYEliminar() throws Exception {
        var a = user("root").roles("ADMIN");

        mvc.perform(get("/celulares/nuevo").with(a)).andExpect(status().isOk());

        mvc.perform(post("/celulares/guardar").with(a).with(csrf())
                .param("numeroSerie", FORM_CELULAR_VALIDO_SN).param("marca", "Apple").param("modelo", "15")
                .param("megapixeles", "48").param("almacenamiento_GB", "128"))
           .andExpect(redirectedUrl("/celulares/lista"));
        org.junit.jupiter.api.Assertions.assertTrue(celulares.existsByNumeroSerie(FORM_CELULAR_VALIDO_SN));

        mvc.perform(get("/celulares/editar/SN-001").with(a)).andExpect(status().isOk());
        mvc.perform(post("/celulares/actualizar").with(a).with(csrf())
                .param("numeroSerie", "SN-001").param("marca", "Samsung2").param("modelo", "S24")
                .param("megapixeles", "50").param("almacenamiento_GB", "256"))
           .andExpect(redirectedUrl("/celulares/lista"));
        org.junit.jupiter.api.Assertions.assertEquals("Samsung2", celulares.findById("SN-001").orElseThrow().getMarca());

        mvc.perform(post("/celulares/eliminar/SN-001").with(a).with(csrf()))
           .andExpect(redirectedUrl("/celulares/lista"));
        org.junit.jupiter.api.Assertions.assertFalse(celulares.existsByNumeroSerie("SN-001"));
    }

    @Test
    void eliminar_porGET_noEstaPermitido_ni_paraAdmin() throws Exception {
        mvc.perform(get("/celulares/eliminar/SN-001").with(user("root").roles("ADMIN")))
           .andExpect(status().is3xxRedirection());
        org.junit.jupiter.api.Assertions.assertTrue(celulares.existsByNumeroSerie("SN-001"));
    }

    @Test
    void eliminar_sinTokenCsrf_esRechazado() throws Exception {
        mvc.perform(post("/celulares/eliminar/SN-001").with(user("root").roles("ADMIN")))
           .andExpect(status().isForbidden());
        org.junit.jupiter.api.Assertions.assertTrue(celulares.existsByNumeroSerie("SN-001"));
    }

    // ---------- Login real contra la BD (usuarios creados por DataInitializer) ----------
    @Test
    void loginReal_admin_obtieneRolAdmin() throws Exception {
        mvc.perform(formLogin("/login").user("admin").password("Admin1234"))
           .andExpect(authenticated().withRoles("ADMIN"))
           .andExpect(redirectedUrl("/celulares/lista"));
    }

    @Test
    void loginReal_usuario_obtieneRolUser() throws Exception {
        mvc.perform(formLogin("/login").user("usuario").password("User1234"))
           .andExpect(authenticated().withRoles("USER"));
    }

    @Test
    void loginReal_passwordIncorrecta_falla() throws Exception {
        mvc.perform(formLogin("/login").user("admin").password("mala"))
           .andExpect(redirectedUrl("/login?error"));
    }

    // ---------- Logout ----------
    @Test
    void logout_porPost_cierraSesion() throws Exception {
        mvc.perform(post("/logout").with(user("juan").roles("USER")).with(csrf()))
           .andExpect(redirectedUrl("/login?logout"));
    }
}
