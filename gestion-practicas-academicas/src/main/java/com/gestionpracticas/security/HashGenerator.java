package com.gestionpracticas.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 1. ADMIN (admin@practicas.edu)
        String adminPassword = "admin"; // <--- Tu contraseña en texto plano
        String adminHash = encoder.encode(adminPassword);
        System.out.println("HASH para ADMIN ('" + adminPassword + "'): " + adminHash);

        // 2. TUTOR_CURSO (ana.martinez@instituto.edu)
        String tutorCursoPassword = "tutorcurso"; // <--- Tu contraseña en texto plano
        String tutorCursoHash = encoder.encode(tutorCursoPassword);
        System.out.println("HASH para TUTOR_CURSO ('" + tutorCursoPassword + "'): " + tutorCursoHash);
        
        // 3. TUTOR_PRACTICAS (ej: antonio.perez@empresa.com)
        String tutorPracticasPassword = "tutorpracticas"; // <--- Tu contraseña en texto plano
        String tutorPracticasHash = encoder.encode(tutorPracticasPassword);
        System.out.println("HASH para TUTOR_PRACTICAS ('" + tutorPracticasPassword + "'): " + tutorPracticasHash);

        // 4. ALUMNO (ej: juan.perez@estudiante.edu)
        String alumnoPassword = "alumno"; // <--- Tu contraseña en texto plano
        String alumnoHash = encoder.encode(alumnoPassword);
        System.out.println("HASH para ALUMNO ('" + alumnoPassword + "'): " + alumnoHash);
    }
}