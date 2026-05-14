package org.example.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.example.backend.entity.User;
import org.example.backend.repository.UsersRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void addUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        usersRepository.save(user);
    }

    public Optional<User> getUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public Optional<User> getUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }

    public List<User> searchUsers(String search) {
        return usersRepository
                .findByUsernameContainingIgnoreCaseOrNameContainingIgnoreCase(search, search);
    }
    public Optional<User> getUserById(Long id) {
        return usersRepository.findById(id);
    }
}