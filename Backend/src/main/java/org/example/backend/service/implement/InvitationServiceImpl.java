package org.example.backend.service.implement;

import org.example.backend.entity.Invitation;
import org.example.backend.enums.InvitationStatus;
import org.example.backend.repository.InvitationRepository;
import org.example.backend.service.InvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class InvitationServiceImpl implements InvitationService {

    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public List<Invitation> findAll() {
        return invitationRepository.findAll();
    }

    @Override
    public Invitation findById(long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invitación no encontrada con id: " + id));
    }

    @Override
    public Invitation save(Invitation invitation) {
        boolean existe = invitationRepository
                .existsBySenderIdAndReceiverIdAndStatus(
                        invitation.getSenderId(), invitation.getReceiverId(), InvitationStatus.PENDING)
                ||
                invitationRepository
                        .existsBySenderIdAndReceiverIdAndStatus(
                                invitation.getReceiverId(), invitation.getSenderId(), InvitationStatus.PENDING);

        if (existe) {
            throw new RuntimeException("Ya existe una solicitud pendiente entre estos usuarios");
        }

        invitation.setStatus(InvitationStatus.PENDING);
        return invitationRepository.save(invitation);
    }

    @Override
    public void acceptInvitation(long id) {
        Invitation invitation = findById(id);
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitationRepository.save(invitation);
    }

    @Override
    public void rejectInvitation(long id) {
        Invitation invitation = findById(id);
        invitation.setStatus(InvitationStatus.REJECTED);
        invitationRepository.save(invitation);
    }

    @Override
    public void deleteById(long id) {
        invitationRepository.deleteById(id);
    }

    @Override
    public List<Invitation> getPendingByReceiver(long receiverId) {
        return invitationRepository.findByReceiverIdAndStatus(receiverId, InvitationStatus.PENDING);
    }
}