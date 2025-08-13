package com.springboot.teamalbam.viewer.ai.summary;


import com.springboot.teamalbam.viewer.ai.GptService;
import com.springboot.teamalbam.viewer.ocr.ClovaOcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@RequiredArgsConstructor
public class AiSummaryService {

    private final ClovaOcrService clovaOcrService;
    private final GptService gptService;

    public String generateSummary(File file) throws Exception {
        String extractedText = clovaOcrService.requestOcr(file);

        if (extractedText.length() > 10000) {
            extractedText = extractedText.substring(0, 10000);
        }

        return gptService.summarize(extractedText);
    }
}