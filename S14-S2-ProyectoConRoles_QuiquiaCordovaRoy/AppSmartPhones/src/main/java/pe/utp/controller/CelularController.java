package pe.utp.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import pe.utp.model.Celular;
import pe.utp.service.CelularService;

/**
 * Protección en dos capas:
 *  1) SecurityConfig (filtro por URL)
 *  2) @PreAuthorize en cada método (por si alguien cambia las rutas del filtro).
 */
@Controller
@RequestMapping("/celulares")
public class CelularController {

	@Autowired
	private CelularService celularService;

	// ===================== LECTURA: USER y ADMIN =====================

	@GetMapping("/lista")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String listarCelulares(Model model) {
		model.addAttribute("celulares", celularService.listarTodos());
		return "lista";
	}

	@GetMapping("/ver/{numeroSerie}")
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	public String verDetalle(@PathVariable String numeroSerie, Model model, RedirectAttributes redirectAttributes) {
		Optional<Celular> celular = celularService.buscarPorNumeroSerie(numeroSerie);
		if (celular.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Celular no encontrado: " + numeroSerie);
			return "redirect:/celulares/lista";
		}
		model.addAttribute("celular", celular.get());
		return "ver";
	}

	// ===================== ESCRITURA: solo ADMIN =====================

	@GetMapping("/nuevo")
	@PreAuthorize("hasRole('ADMIN')")
	public String mostrarFormularioNuevo(Model model) {
		model.addAttribute("celular", new Celular());
		model.addAttribute("accion", "nuevo");
		return "formulario";
	}

	@PostMapping("/guardar")
	@PreAuthorize("hasRole('ADMIN')")
	public String guardarCelular(
			@Valid @ModelAttribute("celular") Celular celular,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			model.addAttribute("accion", "nuevo");
			return "formulario";
		}

		if (celularService.existe(celular.getNumeroSerie())) {
			result.rejectValue("numeroSerie", "error.celular", "Ya existe un celular con ese número de serie");
			model.addAttribute("accion", "nuevo");
			return "formulario";
		}

		celularService.guardar(celular);
		redirectAttributes.addFlashAttribute("mensaje", "Celular guardado exitosamente");
		return "redirect:/celulares/lista";
	}

	@GetMapping("/editar/{numeroSerie}")
	@PreAuthorize("hasRole('ADMIN')")
	public String mostrarFormularioEditar(@PathVariable String numeroSerie, Model model,
			RedirectAttributes redirectAttributes) {
		Optional<Celular> celular = celularService.buscarPorNumeroSerie(numeroSerie);
		if (celular.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Celular no encontrado: " + numeroSerie);
			return "redirect:/celulares/lista";
		}
		model.addAttribute("celular", celular.get());
		model.addAttribute("accion", "editar");
		return "formulario";
	}

	@PostMapping("/actualizar")
	@PreAuthorize("hasRole('ADMIN')")
	public String actualizarCelular(
			@Valid @ModelAttribute("celular") Celular celular,
			BindingResult result,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			model.addAttribute("accion", "editar");
			return "formulario";
		}

		if (!celularService.existe(celular.getNumeroSerie())) {
			redirectAttributes.addFlashAttribute("error", "Celular no encontrado: " + celular.getNumeroSerie());
			return "redirect:/celulares/lista";
		}

		celularService.actualizar(celular);
		redirectAttributes.addFlashAttribute("mensaje", "Celular actualizado exitosamente");
		return "redirect:/celulares/lista";
	}

	// Eliminar ahora es POST (antes GET): una acción que modifica datos no debe poder
	// dispararse con un simple enlace o una imagen (CSRF). Spring Security exige el token.
	@PostMapping("/eliminar/{numeroSerie}")
	@PreAuthorize("hasRole('ADMIN')")
	public String eliminarCelular(@PathVariable String numeroSerie, RedirectAttributes redirectAttributes) {
		celularService.eliminar(numeroSerie);
		redirectAttributes.addFlashAttribute("mensaje", "Celular eliminado exitosamente");
		return "redirect:/celulares/lista";
	}
}
