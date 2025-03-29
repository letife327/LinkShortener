package az.texnoera.link_shortener.service;

import az.texnoera.link_shortener.entity.User;
import az.texnoera.link_shortener.repository.UserRepository;
import az.texnoera.link_shortener.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {
    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${file.download-url}")
    private String downloadUrl;
    private final UserRepository userRepository;


    public ResponseEntity<?> uploadPhoto(MultipartFile file) {

        try {
            User user = SecurityUtils.getCurrentUser();
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            user.setProfilePhoto(fileName);
            userRepository.save(user);
            return ResponseEntity.ok(downloadUrl+fileName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    public ResponseEntity<?> download(String fileName) {

        try {
            Path filePath = Paths.get(uploadDir, fileName);
            Resource resource = new UrlResource(filePath.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (MalformedURLException e) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("File not found.");
        }
    }
}
