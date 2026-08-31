package com.springboot.teamalbam.viewer.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileTypeValidator fileTypeValidator;
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    private Path resolveSafePath(String fileId) {
        Path targetPath = uploadDir.resolve(fileId).normalize();
        if (!targetPath.startsWith(uploadDir)) {
            throw new IllegalArgumentException("잘못된 파일 경로입니다: " + fileId);
        }
        return targetPath;
    }

    public String store(MultipartFile file) {
        fileTypeValidator.validate(file);

        try {
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String originalFilename = Paths.get(file.getOriginalFilename()).getFileName().toString();
            String fileId = UUID.randomUUID() + "_" + originalFilename;

            Path targetPath = resolveSafePath(fileId);
            file.transferTo(targetPath.toFile());

            return fileId;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }

    public void delete(String fileId) throws FileNotFoundException {
        Path targetPath = resolveSafePath(fileId);

        if (!Files.exists(targetPath)) {
            throw new FileNotFoundException("파일이 존재하지 않습니다: " + fileId);
        }

        try {
            Files.delete(targetPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + fileId, e);
        }
    }

    public File getFileById(String fileId) throws FileNotFoundException {
        Path targetPath = resolveSafePath(fileId);
        if (!Files.exists(targetPath)) {
            throw new FileNotFoundException("파일이 존재하지 않습니다: " + fileId);
        }
        return targetPath.toFile();
    }

}
