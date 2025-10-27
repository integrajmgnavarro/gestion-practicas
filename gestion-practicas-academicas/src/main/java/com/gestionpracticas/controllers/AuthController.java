package com.gestionpracticas.controllers;

import com.gestionpracticas.dto.UsuarioDTO;
import com.gestionpracticas.dto.UsuarioCreateDTO;
import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ========================= VISTAS ========================= //

    /**
     * Muestra el formulario de login
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                           @RequestParam(value = "logout", required = false) String logout,
                           Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }
        if (logout != null) {
            model.addAttribute("message", "Sesión cerrada correctamente");
        }
        return "auth/login";
    }

    /**
     * Muestra el formulario de registro
     */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("usuarioCreateDTO", new UsuarioCreateDTO());
        return "auth/register";
    }

    /**
     * Procesa el registro de usuario (vista)
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UsuarioCreateDTO createDTO,
                          BindingResult result,
                          Model model) {
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(createDTO.getEmail()).isPresent()) {
            model.addAttribute("error", "El email ya está registrado");
            return "auth/register";
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(createDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        usuario.setRol(createDTO.getRol());
        usuario.setActivo(true);
        usuario.setReferenceId(createDTO.getReferenceId());

        usuarioRepository.save(usuario);

        model.addAttribute("message", "Usuario registrado correctamente");
        return "redirect:/auth/login?registered=true";
    }

    // ========================= API REST ========================= //

    /**
     * POST /auth/register (API)
     * Registro de usuario vía JSON
     */
    @PostMapping("/register/api")
    @ResponseBody
    public ResponseEntity<?> registerApi(@Valid @RequestBody UsuarioCreateDTO createDTO) {
        // Verificar si el email ya existe
        if (usuarioRepository.findByEmail(createDTO.getEmail()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El email ya está registrado");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        }

        // Crear usuario
        Usuario usuario = new Usuario();
        usuario.setEmail(createDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(createDTO.getPassword()));
        usuario.setRol(createDTO.getRol());
        usuario.setActivo(true);
        usuario.setReferenceId(createDTO.getReferenceId());

        usuario = usuarioRepository.save(usuario);

        UsuarioDTO response = convertToDTO(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /auth/me
     * Obtiene los datos del usuario autenticado actual
     */
    @GetMapping("/me")
    @ResponseBody
    public ResponseEntity<UsuarioDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar último acceso
        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UsuarioDTO dto = convertToDTO(usuario);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /auth/logout (manejado por Spring Security automáticamente)
     * Este método es opcional, Spring Security ya maneja el logout
     */

    // ========================= MÉTODOS AUXILIARES ========================= //

    private UsuarioDTO convertToDTO(Usuario usuario) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol().name());
        dto.setReferenceId(usuario.getReferenceId());
        dto.setActivo(usuario.getActivo());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        return dto;
    }
}