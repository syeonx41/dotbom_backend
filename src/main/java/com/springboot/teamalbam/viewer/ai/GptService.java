package com.springboot.teamalbam.viewer.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String GPT_URL = "https://api.openai.com/v1/chat/completions";

    public String summarize(String text) {
        if (text == null || text.trim().isEmpty()) return "요약할 내용이 없습니다.";

        // WebClient 생성
        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        // 요청 바디 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "다음 문서를 난독증이 있는 사람도 이해할 수 있도록 핵심 내용을 중심으로 쉽고 자세하게 요약해줘. " +
                "중요한 정보는 모두 포함해서 문장을 완성해줘." +
                "대답이나 설명 없이 요약된 문장만 출력해줘." + "존댓말(~습니다)로 해줘." + "문단이 조금 길었으면 좋겠어"));
        messages.add(Map.of("role", "user", "content", text));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        int retries = 3;
        for (int i = 0; i < retries; i++) {
            try {
                return client.post()
                        .uri("/chat/completions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(Map.class)
                        .map(response -> {
                            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                            return (String) message.get("content");
                        })
                        .block();
            } catch (Exception e) {
                if (i == retries - 1) throw e;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        return "요약 실패";
    }

    public String convertToEasyText(String text) {
        if (text == null || text.trim().isEmpty()) return "변환할 내용이 없습니다.";

        WebClient client = WebClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-3.5-turbo");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", "다음 텍스트를 난독증이 있는 사람도 이해할 수 있도록, 어려운 단어를 아이들도 이해할 수 있게 쉽게 바꾸고 문장을 풀어서 설명해줘. 대신 요약은 절대 하지 말고 있는 그대로의 텍스트들을 어린 아이들도 이해할 수 있게 단어를 쉽게 문장으로 풀어서 최대한 길게 적어줘. 대답은 하지 말고 바꾼 문장만 존댓말(~습니다)로 출력해."));
        messages.add(Map.of("role", "user", "content", text));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        return client.post()
                .uri("/chat/completions")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                })
                .block();
    }

}

