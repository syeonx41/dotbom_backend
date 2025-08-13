package com.springboot.teamalbam.viewer.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.*;

@Service
public class ClovaOcrService {

    @Value("${clova.ocr.secret-key}")
    private String secretKey;

    private static final String CLOVA_OCR_BASE_URL = "https://s30jyww021.apigw.ntruss.com";
    private static final String CLOVA_OCR_PATH = "/custom/v1/44804/a760cf55bbc9908d47b22fabc4e20b195477b74a8056b093eee733a031246dc5/general";

    public String requestOcr(File imageFile) throws IOException {
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String fileExt = getFileExtension(imageFile.getName());

        Map<String, Object> body = new HashMap<>();
        body.put("version", "V2");
        body.put("requestId", UUID.randomUUID().toString());
        body.put("timestamp", System.currentTimeMillis());

        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("format", fileExt);
        imageMap.put("name", "image");
        imageMap.put("data", base64Image);
        body.put("images", Collections.singletonList(imageMap));

        WebClient client = WebClient.builder()
                .baseUrl(CLOVA_OCR_BASE_URL)
                .build();

        return client.post()
                .uri(CLOVA_OCR_PATH)
                .header("X-OCR-SECRET", secretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 테스트 중엔 block() 사용 OK
    }

    public String requestOcrFromPdf(File pdfFile) throws Exception {
        List<BufferedImage> images = PdfToImageConverter.convertPdfToImages(pdfFile, 200);
        StringBuilder finalResult = new StringBuilder();

        for (BufferedImage image : images) {
            String base64 = ImageEncoder.encodeToBase64(image);
            String singlePageResult = sendToClova(base64);
            finalResult.append(singlePageResult).append("\n\n");
        }

        return finalResult.toString();
    }

    private String sendToClova(String base64Image) {
        Map<String, Object> body = new HashMap<>();
        body.put("version", "V2");
        body.put("requestId", UUID.randomUUID().toString());
        body.put("timestamp", System.currentTimeMillis());

        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("format", "jpg");
        imageMap.put("name", "image");
        imageMap.put("data", base64Image);
        body.put("images", Collections.singletonList(imageMap));

        WebClient client = WebClient.builder()
                .baseUrl(CLOVA_OCR_BASE_URL)
                .build();

        String response = client.post()
                .uri(CLOVA_OCR_PATH)
                .header("X-OCR-SECRET", secretKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return extractInferText(response);
    }

    private String getFileExtension(String fileName) {
        int dotIdx = fileName.lastIndexOf('.');
        return (dotIdx != -1) ? fileName.substring(dotIdx + 1).toLowerCase() : "jpg";
    }

    private String extractInferText(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(json);
            JsonNode images = root.path("images");

            StringBuilder result = new StringBuilder();

            for (JsonNode image : images) {
                JsonNode fields = image.path("fields");
                for (JsonNode field : fields) {
                    String inferText = field.path("inferText").asText();
                    result.append(inferText);
                    if (field.path("lineBreak").asBoolean()) {
                        result.append("\n");
                    } else {
                        result.append(" ");
                    }
                }
                result.append("\n");
            }

            return result.toString().trim();
        } catch (Exception e) {
            return "⚠️ OCR 응답 파싱 실패: " + e.getMessage();
        }
    }

}