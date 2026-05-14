package org.example.backend.service;

import org.example.backend.entity.Post;
import java.util.List;

public interface PostService {
    List<Post> findAll();
    Post findById(long id);
    Post save(Post post);
    Post update(long id, Post post);
    void deleteById(long id);
    List<Post> findByUserId(long userId);
    List<Post> findFriendsPosts(long userId);
}