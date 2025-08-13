package com.springboot.teamalbam.viewer.file;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileTypeValidator {

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "application/pdf"
    );

    public void validate(MultipartFile file) {
        String contentType = file.getContentType();
        if (!ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("허용되지 않은 파일 형식입니다: " + contentType);
        }
    }
}
