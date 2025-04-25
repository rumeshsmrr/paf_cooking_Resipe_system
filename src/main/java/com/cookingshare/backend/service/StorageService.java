package com.cookingshare.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class StorageService {

    /**
     * Stub implementation: store the file and return its public URL.
     * Replace with S3, Cloudinary, GridFS, etc. in production.
     */
    public String upload(MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            // … your upload logic here …
            return "https://cdn.example.com/images/"
                    + UUID.randomUUID()
                    + "-"
                    + file.getOriginalFilename();
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
