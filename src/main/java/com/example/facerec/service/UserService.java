package com.example.facerec.service;

import com.example.facerec.entity.User;
import com.example.facerec.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository repo;
    private final FaceService faceService;

    public UserService(UserRepository repo, FaceService faceService) {
        this.repo = repo;
        this.faceService = faceService;
    }

    public User createFromUpload(String name, MultipartFile file) throws IOException {
        String savedPath = faceService.detectCropAndSave(file.getBytes(), name);
        if (savedPath == null) throw new RuntimeException("No face detected in uploaded image");
        User u = new User(name, savedPath);
        return repo.save(u);
    }

    public User createFromBase64(String name, String base64) throws IOException {
        String savedPath = faceService.detectCropAndSaveFromBase64(base64, name);
        if (savedPath == null) throw new RuntimeException("No face detected in provided image");
        User u = new User(name, savedPath);
        return repo.save(u);
    }

    public List<User> listAll() { return repo.findAll(); }

    public Optional<User> find(Long id) { return repo.findById(id); }

    public User updateName(Long id, String newName) {
        User u = repo.findById(id).orElseThrow();
        u.setName(newName);
        return repo.save(u);
    }

    public void delete(Long id) {
        User u = repo.findById(id).orElseThrow();
        faceService.deleteImage(u.getImagePath());
        repo.deleteById(id);
    }
}
