package com.springboot.teamalbam.viewer.readability;

import org.springframework.stereotype.Service;

@Service
public class ReadabilityService {

    public String improve(String text) {
        // 아주 간단한 줄바꿈 가공 예시
        return text.replaceAll("([.!?])\\s*", "$1\n");
    }
}