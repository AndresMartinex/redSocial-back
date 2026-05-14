package org.example.backend.service;

import org.example.backend.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment addComment(Comment comment);
    List<Comment> findByPostId(long postId);
    void deleteById(long id);
}