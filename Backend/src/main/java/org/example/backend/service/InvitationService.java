package org.example.backend.service;

import org.example.backend.entity.Invitation;
import java.util.List;
import java.util.UUID;

public interface InvitationService {
    List<Invitation> findAll();
    Invitation findById(long id);
    Invitation save(Invitation invitation);
    void acceptInvitation(long id);
    void rejectInvitation(long id);
    void deleteById(long id);
    List<Invitation> getPendingByReceiver(long receiverId);
}