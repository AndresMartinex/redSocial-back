package org.example.backend.repository;

import org.example.backend.entity.Invitation;
import org.example.backend.enums.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    List<Invitation> findByReceiverIdAndStatus(Long receiverId, InvitationStatus status);
    Optional<Invitation> findBySenderIdAndReceiverId(Long senderId, Long receiverId);
    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, InvitationStatus status);
}