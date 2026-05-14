package org.example.backend.rest;

import org.example.backend.entity.Comment;
import org.example.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentRest {

    @Autowired
    private CommentService commentService;

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> porPost(@PathVariable long postId) {
        return ResponseEntity.ok(commentService.findByPostId(postId));
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            long postId = Long.parseLong(body.get("postId").toString());
            long userId = Long.parseLong(body.get("userId").toString());
            String content = body.get("content").toString();

            Comment comment = new Comment();
            comment.setPostId(postId);
            comment.setUserId(userId);
            comment.setContent(content);

            return ResponseEntity.status(HttpStatus.CREATED).body(commentService.addComment(comment));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        try {
            commentService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Comentario eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}