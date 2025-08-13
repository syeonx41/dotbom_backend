package com.springboot.teamalbam.viewer.ai.easytext;

import com.springboot.teamalbam.viewer.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;

@RestController
@RequestMapping("/api/v1/viewer/easy-text")
@RequiredArgsConstructor
public class AiEasyTextController {

    private final FileService fileService;
    private final AiEasyTextService aiEasyTextService;

    @PostMapping
    public ResponseEntity<AiEasyTextResponseDto> convertToEasyText(@RequestBody AiEasyTextRequestDto request) throws Exception {
        File file = fileService.getFileById(request.getFileId());
        String result = aiEasyTextService.convertToEasyText(file);
        return ResponseEntity.ok(new AiEasyTextResponseDto(result));
    }
}
