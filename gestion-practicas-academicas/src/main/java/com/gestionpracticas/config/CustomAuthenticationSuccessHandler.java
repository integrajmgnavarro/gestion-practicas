package com.gestionpracticas.config; // O donde decidas ponerlo

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication) throws IOException {
        
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        String targetUrl = "/auth/login?error=true"; // Fallback de seguridad

        // 🚨 Las rutas deben coincidir con tus @RequestMapping
        if (roles.contains("ROLE_ADMIN")) {
            targetUrl = "/admin/dashboard"; 
        } else if (roles.contains("ROLE_TUTOR_CURSO")) {
            targetUrl = "/tutor-curso/dashboard"; 
        } else if (roles.contains("ROLE_TUTOR_PRACTICAS")) {
            targetUrl = "/tutor-practicas/dashboard";
        } else if (roles.contains("ROLE_ALUMNO")) {
            targetUrl = "/alumno/dashboard";
        }

        response.sendRedirect(request.getContextPath() + targetUrl);
    }
}