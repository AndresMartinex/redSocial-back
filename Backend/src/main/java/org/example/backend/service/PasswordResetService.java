package org.example.backend.service;

import jakarta.mail.MessagingException;
import org.example.backend.dto.ResetPasswordRequest;
import org.example.backend.entity.PasswordResetToken;
import org.example.backend.entity.User;
import org.example.backend.repository.PasswordResetTokenRepository;
import org.example.backend.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PasswordResetService {

    private final UsersRepository usersRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public PasswordResetService(UsersRepository usersRepository,
                                PasswordResetTokenRepository tokenRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void sendResetEmail(String email) {
        User user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con ese correo"));

        // Eliminar token anterior con flush forzado
        tokenRepository.deleteByUser(user);
        tokenRepository.flush();

        // Generar nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        String resetLink = frontendUrl + "/reset-password.html?token=" + token;
        String htmlBody = buildEmailTemplate(user.getName(), resetLink);
        try {
            emailService.sendHtmlEmail(email, "Restauración de contraseña", htmlBody);
        } catch (MessagingException e) {
            throw new RuntimeException("Error al enviar el correo: " + e.getMessage());
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Las contraseñas no coinciden");
        }

        PasswordResetToken resetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            tokenRepository.flush();
            throw new RuntimeException("El token ha expirado, solicita uno nuevo");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Este token ya fue utilizado");
        }

        // Actualizar contraseña
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        // Eliminar token en lugar de marcarlo como usado
        tokenRepository.delete(resetToken);
        tokenRepository.flush();
    }

    public void validateToken(String token) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new RuntimeException("El token ha expirado");
        }

        if (resetToken.isUsed()) {
            throw new RuntimeException("Este token ya fue utilizado");
        }
    }

    private String buildEmailTemplate(String userName, String resetLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px;">
                    <h2 style="color: #333;">Hola, %s</h2>
                    <p>Recibimos una solicitud para restaurar la contraseña de tu cuenta.</p>
                    <p>Haz clic en el siguiente botón. El enlace expira en <strong>1 minutos</strong>.</p>
                    <a href="%s"
                       style="display:inline-block; padding:12px 24px; background:#4F46E5;
                              color:white; border-radius:6px; text-decoration:none;
                              font-weight:bold; margin: 16px 0;">
                        Restaurar contraseña
                    </a>
                    <p style="color:#888; font-size:13px;">
                        Si no solicitaste esto, ignora este correo. Tu contraseña no cambiará.
                    </p>
                </div>
                """.formatted(userName, resetLink);
}    }
