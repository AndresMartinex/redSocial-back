package org.example.backend.service;

import org.example.backend.enums.Roles;
import org.springframework.stereotype.Service;

@Service
public class RolesService {

    public Roles getDefaultRole() {
        return Roles.ROLE_USER;
    }

    public Roles getAdminRole() {
        return Roles.ROLE_ADMIN;
    }
}