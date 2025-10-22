package com.gestionpracticas.services;

import com.gestionpracticas.models.Usuario;
import com.gestionpracticas.models.Usuario.Rol;
import com.gestionpracticas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    // Crear usuario
    public Usuario crearUsuario(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }
        
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        
        return usuarioRepository.save(usuario);
    }
    
    // Obtener todos los usuarios
    public List<Usuario> getTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }
    
    // Obtener usuario por ID
    public Optional<Usuario> getUsuarioPorId(Long id) {
        return usuarioRepository.findById(id);
    }
    
    // Obtener usuario por email
    public Optional<Usuario> getUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
    
    // Obtener usuarios por rol
    public List<Usuario> getUsuariosPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }
    
    // Obtener usuarios activos
    public List<Usuario> getUsuariosActivos() {
        return usuarioRepository.findByActivo(true);
    }
    
    // Actualizar usuario
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> usuarioExistente = usuarioRepository.findById(id);
        
        if (usuarioExistente.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioExistente.get();
        
        // Actualizar campos
        if (usuarioActualizado.getEmail() != null && 
            !usuarioActualizado.getEmail().equals(usuario.getEmail())) {
            // Verificar que el nuevo email no exista
            if (usuarioRepository.existsByEmail(usuarioActualizado.getEmail())) {
                throw new RuntimeException("El email ya está registrado");
            }
            usuario.setEmail(usuarioActualizado.getEmail());
        }
        
        if (usuarioActualizado.getPassword() != null && 
            !usuarioActualizado.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioActualizado.getPassword()));
        }
        
        if (usuarioActualizado.getRol() != null) {
            usuario.setRol(usuarioActualizado.getRol());
        }
        
        if (usuarioActualizado.getReferenceId() != null) {
            usuario.setReferenceId(usuarioActualizado.getReferenceId());
        }
        
        if (usuarioActualizado.getActivo() != null) {
            usuario.setActivo(usuarioActualizado.getActivo());
        }
        
        return usuarioRepository.save(usuario);
    }
    
    // Actualizar último acceso
    public void actualizarUltimoAcceso(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.ifPresent(u -> {
            u.setUltimoAcceso(LocalDateTime.now());
            usuarioRepository.save(u);
        });
    }
    
    // Desactivar usuario (soft delete)
    public void desactivarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.ifPresent(u -> {
            u.setActivo(false);
            usuarioRepository.save(u);
        });
    }
    
    // Activar usuario
    public void activarUsuario(Long id) {
        Optional<Usuario> usuario = usuarioRepository.findById(id);
        usuario.ifPresent(u -> {
            u.setActivo(true);
            usuarioRepository.save(u);
        });
    }
    
    // Eliminar usuario (hard delete)
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }
    
    // Verificar credenciales (para login)
    public Optional<Usuario> verificarCredenciales(String email, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
        
        if (usuario.isPresent() && 
            passwordEncoder.matches(password, usuario.get().getPassword()) &&
            usuario.get().getActivo()) {
            return usuario;
        }
        
        return Optional.empty();
    }
}
