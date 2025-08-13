package com.springboot.teamalbam.viewer.readability;


import com.springboot.teamalbam.viewer.file.FileService;
import com.springboot.teamalbam.viewer.ocr.ClovaOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/viewer")
@RequiredArgsConstructor
public class ReadabilityController {

    private final FileService fileService;
    private final ClovaOcrService clovaOcrService;
    private final ReadabilityService readabilityService;

    @PostMapping("/readability")
    public ResponseEntity<ReadabilityResponseDto> improve(@RequestBody ReadabilityRequestDto request) throws Exception {
        // 1. 저장된 파일을 가져옴
        File file = fileService.getFileById(request.getFileId());

        // 2. OCR 추출
        String extractedText = clovaOcrService.requestOcrFromPdf(file);

        // 3. 가독성 향상 (줄바꿈 처리 등)
        String improvedText = readabilityService.improve(extractedText);

        // 4. 응답
        return ResponseEntity.ok(new ReadabilityResponseDto(request.getFileId(), improvedText));
    }
}