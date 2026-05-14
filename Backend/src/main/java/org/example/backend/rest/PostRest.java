package org.example.backend.rest;

import org.example.backend.entity.Post;
import org.example.backend.service.PostService;
import org.example.backend.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostRest {

    @Autowired
    private PostService postService;

    @Autowired
    private UsersService usersService;

    @GetMapping("/listar")
    public ResponseEntity<List<Post>> listar() {
        return ResponseEntity.ok(postService.findAll());
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<?> findById(@PathVariable long id) {
        try {
            return ResponseEntity.ok(postService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/misPublicaciones")
    public ResponseEntity<?> misPublicaciones(@RequestParam String email) {
        return usersService.getUserByEmail(email).map(user ->
                ResponseEntity.ok(postService.findByUserId(user.getId()))
        ).orElse(ResponseEntity.badRequest().build());
    }

    @GetMapping("/amigos")
    public ResponseEntity<?> publicacionesAmigos(@RequestParam String email) {
        return usersService.getUserByEmail(email).map(user ->
                ResponseEntity.ok(postService.findFriendsPosts(user.getId()))
        ).orElse(ResponseEntity.badRequest().build());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, Object> body) {
        try {
            long userId   = Long.parseLong(body.get("userId").toString());
            String content  = body.get("content") != null ? body.get("content").toString() : null;
            String imageUrl = body.get("imageUrl") != null ? body.get("imageUrl").toString() : null;

            Post post = new Post();
            post.setUserId(userId);
            post.setContent(content);
            post.setImageUrl(imageUrl);

            return ResponseEntity.status(HttpStatus.CREATED).body(postService.save(post));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> editar(@PathVariable long id, @RequestBody Map<String, Object> body) {
        try {
            long userId     = Long.parseLong(body.get("userId").toString());
            String content  = body.get("content") != null ? body.get("content").toString() : null;
            String imageUrl = body.get("imageUrl") != null ? body.get("imageUrl").toString() : null;

            Post post = new Post();
            post.setUserId(userId);
            post.setContent(content);
            post.setImageUrl(imageUrl);

            return ResponseEntity.ok(postService.update(id, post));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<?> deleteById(@PathVariable long id) {
        try {
            postService.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Post eliminado"));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}