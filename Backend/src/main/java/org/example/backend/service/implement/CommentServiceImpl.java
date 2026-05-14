package org.example.backend.service.implement;

import org.example.backend.entity.Comment;
import org.example.backend.repository.CommentRepository;
import org.example.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment addComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByPostId(long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    @Override
    public void deleteById(long id) {
        commentRepository.deleteById(id);
    }
}