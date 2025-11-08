package com.example.facerec.controller;

import com.example.facerec.dto.CreateUserRequest;
import com.example.facerec.entity.User;
import com.example.facerec.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadUser(@RequestParam("name") String name,
                                        @RequestParam("file") MultipartFile file) {
        try {
            User u = userService.createFromUpload(name, file);
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> captureUser(@RequestBody CreateUserRequest req) {
        try {
            User u = userService.createFromBase64(req.getName(), req.getImageBase64());
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> list() {
        return ResponseEntity.ok(userService.listAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody CreateUserRequest req) {
        try {
            User u = userService.updateName(id, req.getName());
            return ResponseEntity.ok(u);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.ok("Deleted");
    }
}
