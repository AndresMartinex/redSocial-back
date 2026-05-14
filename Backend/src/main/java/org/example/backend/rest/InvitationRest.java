package org.example.backend.rest;

import org.example.backend.entity.Invitation;
import org.example.backend.service.InvitationService;
import org.example.backend.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/invitations")
public class InvitationRest {

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private UsersService usersService;

    @GetMapping("/listar")
    public ResponseEntity<List<Invitation>> listar() {
        return ResponseEntity.ok(invitationService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Invitation> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(invitationService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            System.out.println(">>> Body recibido: " + body);
            long senderId   = Long.parseLong(body.get("senderId").toString());
            long receiverId = Long.parseLong(body.get("receiverId").toString());

            Invitation invitation = new Invitation();
            invitation.setSenderId(senderId);
            invitation.setReceiverId(receiverId);

            Invitation saved = invitationService.save(invitation);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Invitación enviada", "id", saved.getId()));
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println(">>> ERROR: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/pendientes")
    public ResponseEntity<?> pendientes(@RequestParam String email) {
        return usersService.getUserByEmail(email).map(user -> {
            long userId = Long.parseLong(user.getId().toString());

            List<Map<String, Object>> result = invitationService
                    .getPendingByReceiver(userId)
                    .stream()
                    .map(inv -> {
                        String senderName = usersService
                                .getUserById(inv.getSenderId())
                                .map(s -> s.getName() + " " + s.getLastName())
                                .orElse("Usuario desconocido");
                        return Map.<String, Object>of(
                                "id",         inv.getId(),
                                "senderName", senderName
                        );
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        }).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> accept(@PathVariable long id) {
        try {
            invitationService.acceptInvitation(id);
            return ResponseEntity.ok(Map.of("message", "Solicitud aceptada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable long id) {
        try {
            invitationService.rejectInvitation(id);
            return ResponseEntity.ok(Map.of("message", "Solicitud rechazada"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Invitation> deleteById(@PathVariable long id) {
        try {
            Invitation invitation = invitationService.findById(id);
            invitationService.deleteById(id);
            return ResponseEntity.ok(invitation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/amigos")
    public ResponseEntity<?> amigos(@RequestParam String email) {
        return usersService.getUserByEmail(email).map(user -> {
            long userId = user.getId();

            List<Map<String, Object>> result = invitationService.findAll()
                    .stream()
                    .filter(inv -> inv.getStatus().name().equals("ACCEPTED") &&
                            (inv.getSenderId() == userId || inv.getReceiverId() == userId))
                    .map(inv -> {
                        long friendId = inv.getSenderId() == userId ? inv.getReceiverId() : inv.getSenderId();
                        return usersService.getUserById(friendId)
                                .map(friend -> Map.<String, Object>of(
                                        "invitationId", inv.getId(),
                                        "friendId",     friend.getId(),
                                        "name",         friend.getName() + " " + friend.getLastName()
                                ))
                                .orElse(null);
                    })
                    .filter(m -> m != null)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(result);
        }).orElse(ResponseEntity.badRequest().build());
    }
}