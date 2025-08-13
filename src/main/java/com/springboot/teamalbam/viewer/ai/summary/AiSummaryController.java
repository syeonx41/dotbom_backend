package com.springboot.teamalbam.viewer.ai.summary;

import com.springboot.teamalbam.viewer.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/v1/viewer/ai")
@RequiredArgsConstructor
public class AiSummaryController {

    private final FileService fileService;
    private final AiSummaryService aiSummaryService;

    @PostMapping
    public ResponseEntity<AiSummaryResponseDto> summarize(@RequestBody AiSummaryRequestDto request) throws Exception {
        File file = fileService.getFileById(request.getFileId());
        String summary = aiSummaryService.generateSummary(file);
        return ResponseEntity.ok(new AiSummaryResponseDto(summary));
    }
}