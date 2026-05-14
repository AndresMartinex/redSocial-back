package org.example.backend.validators;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.example.backend.entity.User;
import org.example.backend.service.UsersService;

@Component
public class SignUpFormValidator implements Validator {

    @Autowired
    private UsersService usersService;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        User user = (User) o;

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            errors.rejectValue("email", "error.email", "El correo es obligatorio");
        } else if (usersService.getUserByEmail(user.getEmail()) != null) {
            errors.rejectValue("email", "error.email", "El correo ya está registrado");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            errors.rejectValue("name", "error.name", "El nombre es obligatorio");
        }

        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            errors.rejectValue("username", "error.username", "El nombre de usuario es obligatorio");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            errors.rejectValue("password", "error.password", "La contraseña debe tener al menos 6 caracteres");
        } else if (!user.getPassword().equals(user.getPasswordConfirm())) {
            errors.rejectValue("passwordConfirm", "error.passwordConfirm", "Las contraseñas no coinciden");
        }
    }
}