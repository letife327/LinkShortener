package az.texnoera.link_shortener.controller;

import az.texnoera.link_shortener.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@CrossOrigin
public class ProfileController {
    private final ProfileService profileService;
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @PostMapping("/uploadPhoto")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        return profileService.uploadPhoto(file);
    }
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/download/{file-name}")
    public ResponseEntity<?> download(@PathVariable("file-name") String fileName) {
        return profileService.download(fileName);
    }
}
