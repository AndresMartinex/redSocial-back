package org.example.backend.rest;

import org.example.backend.dto.ForgotPasswordRequest;
import org.example.backend.dto.ResetPasswordRequest;
import org.example.backend.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/password")
public class PasswordResetRest {

    private final PasswordResetService passwordResetService;

    public PasswordResetRest(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetEmail(request.getEmail());
        // Respuesta genérica por seguridad (no revela si el correo existe o no)
        return ResponseEntity.ok("Si el correo existe, recibirás un enlace en breve");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request);
        return ResponseEntity.ok("Contraseña actualizada correctamente");
    }

    @GetMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
        passwordResetService.validateToken(token);
        return ResponseEntity.ok("Token válido");
    }
}