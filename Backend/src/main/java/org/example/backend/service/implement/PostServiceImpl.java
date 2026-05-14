package org.example.backend.service.implement;

import org.example.backend.entity.Invitation;
import org.example.backend.entity.Post;
import org.example.backend.enums.InvitationStatus;
import org.example.backend.repository.InvitationRepository;
import org.example.backend.repository.PostRepository;
import org.example.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Post findById(long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post no encontrado con id: " + id));
    }

    @Override
    public Post save(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Post update(long id, Post post) {
        Post existing = findById(id);
        if (!existing.getUserId().equals(post.getUserId())) {
            throw new RuntimeException("No tienes permiso para editar este post");
        }
        existing.setContent(post.getContent());
        existing.setImageUrl(post.getImageUrl());
        return postRepository.save(existing);
    }

    @Override
    public void deleteById(long id) {
        postRepository.deleteById(id);
    }

    @Override
    public List<Post> findByUserId(long userId) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<Post> findFriendsPosts(long userId) {
        // Obtener ids de amigos desde invitaciones ACCEPTED
        List<Long> friendIds = invitationRepository.findAll()
                .stream()
                .filter(inv -> inv.getStatus() == InvitationStatus.ACCEPTED &&
                        (inv.getSenderId() == userId || inv.getReceiverId() == userId))
                .flatMap(inv -> Stream.of(
                        inv.getSenderId() == userId ? inv.getReceiverId() : inv.getSenderId()
                ))
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) return List.of();

        return postRepository.findByUserIdInOrderByCreatedAtDesc(friendIds);
    }
}