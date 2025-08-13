package com.springboot.teamalbam.viewer.file;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api/v1/viewer")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/file-upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileId = fileService.store(file);
        return ResponseEntity.ok(new FileUploadResponse(fileId));
    }

    @DeleteMapping("/file-delete/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String fileId) throws FileNotFoundException {
        fileService.delete(fileId);
        return ResponseEntity.noContent().build();
    }

}
