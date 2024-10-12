package com.dacs2.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    String imgPath = System.getProperty("user.dir") + File.separator
            + "src" + File.separator + "main" + File.separator + "resources"
            + File.separator + "static" + File.separator + "img";

    @PostMapping("/file")
    public ResponseEntity<?> uploadFile(@RequestParam("upload") MultipartFile file) {
        String uploadDir = imgPath + File.separator + "ckeditor_img" + File.separator;

        try {
            file.transferTo(new File(uploadDir + file.getOriginalFilename()));
            return ResponseEntity.ok(new UploadResponse("/img/ckeditor_img/" + file.getOriginalFilename()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
    }

    public static class UploadResponse {
        private String url;

        public UploadResponse(String url) {
            this.url = url;
        }

        public String getUrl() {
            return url;
        }
    }

}
